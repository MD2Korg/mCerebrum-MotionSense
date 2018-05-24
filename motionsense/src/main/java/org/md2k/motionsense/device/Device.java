package org.md2k.motionsense.device;

import android.content.Context;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.polidea.rxandroidble.RxBleConnection;
import com.polidea.rxandroidble.RxBleDevice;

import org.md2k.motionsense.BLEPair;
import org.md2k.motionsense.Data;
import org.md2k.motionsense.MyApplication;
import org.md2k.motionsense.ReceiveCallback;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.BackpressureOverflow;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Copyright (c) 2015, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p/>
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p/>
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p/>
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
public abstract class Device {
    public static final String UUID = "0000180f-0000-1000-8000-00805f9b34fb";

    private String deviceId;
    protected ArrayList<Sensor> sensors;
    private Subscription subscriptionRetryConnect;

    protected Device(String deviceId) {
        this.deviceId = deviceId;
        sensors = new ArrayList<>();
    }

    String getDeviceId() {
        return deviceId;
    }

    void add(Sensor sensor) {
        sensors.add(sensor);
    }

    protected ArrayList<Sensor> getSensors(Characteristic characteristic, ArrayList<Sensor> sensors) {
        ArrayList<Sensor> selected = new ArrayList<>();
        for (Sensor sensor1 : sensors) {
            if (sensor1.getCharacteristicName().equals(characteristic.getName())) {
                selected.add(sensor1);
            }
        }
        return selected;
    }

    abstract protected Observable<ArrayList<Data>> getCharacteristicsObservable(RxBleConnection rxBleConnection);

    void connect(Context context, ReceiveCallback receiveCallback) {
        Log.d("abc", "connect start....device=" + deviceId);
        subscriptionRetryConnect = Observable.just(true)
                .flatMap(new Func1<Boolean, Observable<? extends RxBleConnection.RxBleConnectionState>>() {
                    @Override
                    public Observable<? extends RxBleConnection.RxBleConnectionState> call(Boolean aBoolean) {
                        RxBleDevice device = MyApplication.getRxBleClient(context).getBleDevice(deviceId);
                        return Observable.merge(Observable.just(device.getConnectionState()), device.observeConnectionStateChanges());
                    }
                }).filter(new Func1<RxBleConnection.RxBleConnectionState, Boolean>() {
                    @Override
                    public Boolean call(RxBleConnection.RxBleConnectionState rxBleConnectionState) {
                        Logger.d("connection status=" + rxBleConnectionState.toString() + " deviceId=" + deviceId);
                        if (rxBleConnectionState == RxBleConnection.RxBleConnectionState.DISCONNECTED)
                            return true;
                        return false;
                    }
                }).flatMap(new Func1<RxBleConnection.RxBleConnectionState, Observable<ArrayList<Data>>>() {
                    @Override
                    public Observable<ArrayList<Data>> call(RxBleConnection.RxBleConnectionState rxBleConnectionState) {
                        RxBleDevice device = MyApplication.getRxBleClient(context).getBleDevice(deviceId);
                        Logger.d("Device connect() ... deviceId=" + deviceId);
                        return device.establishConnection(true)
                                .flatMap(rxBleConnection -> {
                                    Logger.d("Device: subscribeConnect() deviceId = " + device.getMacAddress() + " connection_state =" + device.getConnectionState().toString());
                                    BLEPair.pairDevice(context, device.getBluetoothDevice());
                                    return getCharacteristicsObservable(rxBleConnection)
                                            .onBackpressureBuffer(100, new Action0() {
                                                @Override
                                                public void call() {
                                                    Logger.e("Device...subscribeConnect()...Data Overflow occurs...after buffer... drop oldest packet");
                                                }
                                            }, BackpressureOverflow.ON_OVERFLOW_DROP_OLDEST);
                                });
                    }
                }).retryWhen(errors -> errors.flatMap((Func1<Throwable, Observable<?>>) throwable -> {
                    Logger.e("Device: connect() retry()..retryWhen(): error e=" + throwable.getMessage(), throwable);
                    return Observable.just(true);
                })).subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Observer<ArrayList<Data>>() {
                    @Override
                    public void onCompleted() {
                        Logger.d("Device:  onCompleted()");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e("Device: onError() e=" + e.toString());
                    }

                    @Override
                    public void onNext(ArrayList<Data> data) {
                        if (receiveCallback != null)
                            receiveCallback.onReceive(data);
                    }
                });
    }


    void disconnect() {
        Log.d("abc", "device=" + deviceId + " disconnect() subscriptionRetryConnect=" + subscriptionRetryConnect);
        if (subscriptionRetryConnect != null && !subscriptionRetryConnect.isUnsubscribed())
            subscriptionRetryConnect.unsubscribe();
        subscriptionRetryConnect = null;
    }

}
