package org.md2k.motionsense.device_new.motionsense_hrv_plus.characteristic_acl;
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

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.source.platform.PlatformType;
import org.md2k.motionsense.MyApplication;
import org.md2k.motionsense.device_new.Characteristic;
import org.md2k.motionsense.device_new.Data;

import java.util.ArrayList;

public class CharacteristicAcl extends Characteristic {

    public CharacteristicAcl() {
        super("da39c921-1d81-48e2-9c68-d0ae4bbd351f", "CHARACTERISIC_ACL", 25.0);
    }
    public ArrayList<Data> getData(byte[] bytes){
        ArrayList<Data> data=new ArrayList<>();
        int curSeq = (int) TranslateAcl.getSequenceNumber(bytes)[0];
        DataTypeDoubleArray acl=correctTimeStamp(curSeq, TranslateAcl.getAccelerometer(bytes));
        data.add(new Data(DataSourceType.ACCELEROMETER, acl));

        DataTypeDoubleArray gyro=correctTimeStamp(curSeq, TranslateAcl.getGyroscope(bytes));
        data.add(new Data(DataSourceType.GYROSCOPE, gyro));


        DataTypeDoubleArray led=correctTimeStamp(curSeq, TranslateAcl.getLED(bytes));
        data.add(new Data(DataSourceType.LED, led));


        DataTypeDoubleArray seq=correctTimeStamp(curSeq, TranslateAcl.getSequenceNumber(bytes));
        data.add(new Data(DataSourceType.SEQUENCE_NUMBER, seq));

        DataTypeDoubleArray raw=correctTimeStamp(curSeq, TranslateAcl.getRaw(bytes));
        data.add(new Data(DataSourceType.RAW, raw));



        return data;
    }

}
