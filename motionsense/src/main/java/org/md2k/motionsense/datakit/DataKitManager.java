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

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.messagehandler.OnConnectionListener;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;

import rx.Observable;
import rx.Subscriber;

public class DataKitManager {
    private DataKitAPI dataKitAPI;
    public Observable<Boolean> connect(Context context){
        return Observable.create(subscriber -> {
            dataKitAPI=DataKitAPI.getInstance(context);
            try {
                dataKitAPI.connect(() -> {
                    subscriber.onNext(true);
                    subscriber.onCompleted();
                });
            } catch (DataKitException e) {
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
            return null;
        }

    }

    public void insert(DataSourceClient dataSourceClient, DataType dataType) {
        try {
            if (dataType instanceof DataTypeDoubleArray)
                dataKitAPI.insertHighFrequency(dataSourceClient, (DataTypeDoubleArray) dataType);
            else
                dataKitAPI.insert(dataSourceClient, dataType);
        }catch (Exception ignored){

        }
    }

    public void disconnect() {
        dataKitAPI.disconnect();
    }

    public void setSummary(DataSourceClient dataSourceClient, DataType dataType) {
        try {
            dataKitAPI.setSummary(dataSourceClient, dataType);
        } catch (DataKitException e) {
            //e.printStackTrace();
        }
    }
}
