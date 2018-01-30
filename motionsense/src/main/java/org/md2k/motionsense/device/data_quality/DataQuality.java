package org.md2k.motionsense.device.data_quality;
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

import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.datakitapi.datatype.DataTypeInt;
import org.md2k.datakitapi.datatype.DataTypeIntArray;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.motionsense.Data;
import org.md2k.motionsense.device.Sensor;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;

public abstract class DataQuality {
    private static final int DELAY = 3000;
    ArrayList<DataTypeDoubleArray> samples;
    DataQuality(){
        samples=new ArrayList<>();
    }
    public abstract int getStatus();
    public Observable<Data> start(Sensor sensor){
        return Observable.interval(DELAY, DELAY, TimeUnit.MILLISECONDS).map(aLong -> {
            DataTypeInt dataTypeInt = new DataTypeInt(DateTime.getDateTime(), getStatus());
            return new Data(sensor, dataTypeInt);
        });
    }
    public synchronized void add(DataTypeDoubleArray sample) {
        samples.add(sample);
    }

    DataType getSummary(DataTypeInt dataTypeInt) {
            int[] intArray=new int[7];
            for(int i=0;i<7;i++) intArray[i]=0;
            int value=dataTypeInt.getSample();
            intArray[value]=DELAY;
            return new DataTypeIntArray(dataTypeInt.getDateTime(), intArray);
    }
}
