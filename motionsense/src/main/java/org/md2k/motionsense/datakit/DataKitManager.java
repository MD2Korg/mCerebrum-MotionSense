package org.md2k.motionsense.datakit;
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

import android.content.Context;
import android.util.Log;

import com.orhanobut.logger.Logger;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.messagehandler.OnConnectionListener;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.motionsense.Data;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;

public class DataKitManager {
    private DataKitAPI dataKitAPI;
    public Observable<Boolean> connect(Context context){
        return Observable.create(subscriber -> {
            dataKitAPI=DataKitAPI.getInstance(context);
            try {
                dataKitAPI.connect(() -> {
                    Logger.d("DataKit Connected..");
                    subscriber.onNext(true);
                    subscriber.onCompleted();
                });
            } catch (DataKitException e) {
                Logger.e("datakit exception on connection..e="+e.getMessage());
                subscriber.onNext(false);
                subscriber.onCompleted();
            }
        });
    }
    public DataSourceClient register(DataSource dataSource){
        DataSourceBuilder dataSourceBuilder=new DataSourceBuilder(dataSource);
        try {
            return dataKitAPI.register(dataSourceBuilder);
        } catch (DataKitException e) {
            Logger.e("datakit exception on register");
            throw new RuntimeException("DataKit registration error e="+e.getMessage());
        }

    }
    public DataType[] insert(ArrayList<Data> dataTemp) {
        if(dataTemp.size()==0) return null;
        try {
            if (dataTemp.get(0).getDataType() instanceof DataTypeDoubleArray) {
                DataTypeDoubleArray[] dataTypeDoubleArrays = new DataTypeDoubleArray[dataTemp.size()];
                for (int i = 0; i < dataTemp.size(); i++)
                    dataTypeDoubleArrays[i] = (DataTypeDoubleArray) dataTemp.get(i).getDataType();
                dataKitAPI.insertHighFrequency(dataTemp.get(0).getSensor().getDataSourceClient(), dataTypeDoubleArrays);
                return dataTypeDoubleArrays;
            } else {
                DataType[] dataTypes = new DataType[dataTemp.size()];
                for (int i = 0; i < dataTemp.size(); i++)
                    dataTypes[i] = dataTemp.get(i).getDataType();
                dataKitAPI.insert(dataTemp.get(0).getSensor().getDataSourceClient(), dataTypes);
                return dataTypes;
            }
        }catch (DataKitException e){
            Logger.e("datakit exception on insert error="+e.getMessage());
            throw new RuntimeException("DataKit Insert error e="+e.getMessage());

        }

    }
/*

    public void insert(DataSourceClient dataSourceClient, DataType dataType) {
        try {
            if (dataType instanceof DataTypeDoubleArray)
                dataKitAPI.insertHighFrequency(dataSourceClient, (DataTypeDoubleArray) dataType);
            else
                dataKitAPI.insert(dataSourceClient, dataType);

        }catch (Exception ignored){
            Logger.e("datakit exception on insert error="+ignored.getMessage());
            throw new RuntimeException("DataKit Insert error e="+ignored.getMessage());
        }
    }
    public void insert(DataSourceClient dataSourceClient, DataTypeDoubleArray dataType[]) {
        try {
                dataKitAPI.insertHighFrequency(dataSourceClient, dataType);
        }catch (Exception ignored){
            Logger.e("datakit exception on insert error="+ignored.getMessage());
            throw new RuntimeException("DataKit Insert error e="+ignored.getMessage());
        }
    }
*/

    public void disconnect() {
        dataKitAPI.disconnect();
    }

    public void setSummary(DataSourceClient dataSourceClient, DataType dataType) {
        try {
            dataKitAPI.setSummary(dataSourceClient, dataType);
        } catch (DataKitException e) {
            Logger.e("datakit exception on setSummary error="+e.getMessage());
            throw new RuntimeException("DataKit setSummary error e="+e.getMessage());
        }
    }

}
