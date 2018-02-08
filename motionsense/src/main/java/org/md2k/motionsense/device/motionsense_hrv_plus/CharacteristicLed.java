package org.md2k.motionsense.device.motionsense_hrv_plus;
/*
 * Copyright (c) 2016, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.polidea.rxandroidble.RxBleConnection;

import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.motionsense.Data;
import org.md2k.motionsense.device.Characteristic;
import org.md2k.motionsense.device.Sensor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import rx.Observable;

public class CharacteristicLed extends Characteristic {
    private HashMap<String, Sensor> listSensor;

    CharacteristicLed() {
        super("da39c921-1d81-48e2-9c68-d0ae4bbd351f", "CHARACTERISTIC_LED", 25.0);
    }

    public Observable<Data> getObservable(RxBleConnection rxBleConnection, ArrayList<Sensor> sensors) {
        prepareList(sensors);
        return setNotify(rxBleConnection);
    }

    private void prepareList(ArrayList<Sensor> sensors) {
        listSensor = new HashMap<>();
        for (Sensor sensor : sensors) {
            String t = sensor.getDataSourceType();
            if (sensor.getDataSourceId() != null)
                t += sensor.getDataSourceId();
            listSensor.put(t, sensor);
        }
    }

    private Observable<Data> setNotify(RxBleConnection rxBleConnection) {
        UUID uuid = UUID.fromString(getId());
        return rxBleConnection.setupNotification(uuid)
                .flatMap(notificationObservable -> notificationObservable)
                .map(bytes -> {
                    ArrayList<Data> data = new ArrayList<>();
                    int curSeq = (int) TranslateLed.getSequenceNumber(bytes)[0];
                    long curTime = correctTimeStamp(curSeq,1024);
                    if (listSensor.containsKey(DataSourceType.ACCELEROMETER)) {
                        DataType d = new DataTypeDoubleArray(curTime, TranslateLed.getAccelerometer(bytes));
                        data.add(new Data(listSensor.get(DataSourceType.ACCELEROMETER), d));
                    }
                    if (listSensor.containsKey(DataSourceType.QUATERNION)) {
                        DataType d = new DataTypeDoubleArray(curTime, TranslateLed.getQuaternion(bytes));
                        data.add(new Data(listSensor.get(DataSourceType.QUATERNION), d));
                    }

                    if (listSensor.containsKey(DataSourceType.LED)) {
                        DataType d = new DataTypeDoubleArray(curTime, TranslateLed.getLED(bytes));
                        data.add(new Data(listSensor.get(DataSourceType.LED), d));
                    }
                    if (listSensor.containsKey(DataSourceType.SEQUENCE_NUMBER + getName())) {
                        DataType d = new DataTypeDoubleArray(curTime, TranslateLed.getSequenceNumber(bytes));
                        data.add(new Data(listSensor.get(DataSourceType.SEQUENCE_NUMBER + getName()), d));
                    }

                    if (listSensor.containsKey(DataSourceType.RAW + getName())) {
                        DataType d = new DataTypeDoubleArray(curTime, TranslateLed.getRaw(bytes));
                        data.add(new Data(listSensor.get(DataSourceType.RAW + getName()), d));
                    }
                    lastSequence = curSeq;
                    lastTimestamp = curTime;
                    return data;
                }).flatMap(Observable::from);
    }
}
