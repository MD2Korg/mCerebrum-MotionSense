package org.md2k.motionsense.device;

import android.content.Context;
import android.util.Log;

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
    private Subscription subscriptionConnect;
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

    abstract protected Observable<Data> getCharacteristicsObservable(RxBleConnection rxBleConnection);

    void connect(Context context, ReceiveCallback receiveCallback) {
        Log.d("abc", "connect start....device=" + deviceId);
        subscriptionRetryConnect = Observable.just(true)
                .map(new Func1<Boolean, RxBleDevice>() {
                    @Override
                    public RxBleDevice call(Boolean aBoolean) {
                        RxBleDevice device = MyApplication.getRxBleClient(context).getBleDevice(deviceId);
                        Log.d("abc", "Device retry: connect(): device=" + device.getMacAddress() + " status=" + device.getConnectionState().toString());
                        unsubscribeConnect();
//                if (device.getConnectionState() == RxBleConnection.RxBleConnectionState.DISCONNECTED) {
                        subscribeConnect(context, receiveCallback);
//                }
                        return device;
                    }
                }).flatMap(new Func1<RxBleDevice, Observable<? extends RxBleConnection.RxBleConnectionState>>() {
                    @Override
                    public Observable<? extends RxBleConnection.RxBleConnectionState> call(RxBleDevice device) {
                        return device.observeConnectionStateChanges();
                    }
                }).doOnUnsubscribe(this::unsubscribeConnect)
                .flatMap(new Func1<RxBleConnection.RxBleConnectionState, Observable<RxBleConnection.RxBleConnectionState>>() {
                    @Override
                    public Observable<RxBleConnection.RxBleConnectionState> call(RxBleConnection.RxBleConnectionState rxBleConnectionState) {
                        if (rxBleConnectionState == RxBleConnection.RxBleConnectionState.DISCONNECTED)
                            return Observable.error(new Throwable("abc"));
                        else return Observable.just(rxBleConnectionState);
                    }
                })
                .retryWhen(errors -> errors.flatMap((Func1<Throwable, Observable<?>>) throwable -> {
                    Log.e("abc", "Device retry: retrywhen(): error=" + throwable.toString());
                    for (int i = 0; i < throwable.getStackTrace().length; i++)
                        Log.e("abc", "Device retry: retrywhen(): error[]=" + throwable.getStackTrace()[i]);
                    unsubscribeConnect();
                    return Observable.timer(1000, TimeUnit.MILLISECONDS);
                })).subscribe(new Observer<RxBleConnection.RxBleConnectionState>() {
                    @Override
                    public void onCompleted() {
                        Log.e("abc", "Device retry: onCompleted()");
                        unsubscribeConnect();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("abc", "Device retry: onError()=" + e.toString());
                        unsubscribeConnect();
//                        subscribeConnect(context, receiveCallback);
                    }

                    @Override
                    public void onNext(RxBleConnection.RxBleConnectionState rxBleConnectionState) {
                        Log.d("abc", "Device retry: OnNext() device=" + deviceId + " state change=" + rxBleConnectionState.toString());
/*
                        if (rxBleConnectionState == RxBleConnection.RxBleConnectionState.DISCONNECTED) {
                            unsubscribeConnect();
                            subscribeConnect(context, receiveCallback);
                        }
*/
                    }
                });
    }

    private void subscribeConnect(Context context, ReceiveCallback receiveCallback) {
        RxBleDevice device = MyApplication.getRxBleClient(context).getBleDevice(deviceId);
        Log.d("abc", "device = " + device.getMacAddress() + " connectionstate = " + device.getConnectionState().toString());
        subscriptionConnect = device.establishConnection(false)
                .onBackpressureBuffer(1024, null, BackpressureOverflow.ON_OVERFLOW_DROP_OLDEST)
                .flatMap(rxBleConnection -> {
                    Log.d("abc", "subscribeConnect() device=" + device.getMacAddress() + " connectionstate=" + device.getConnectionState().toString());
                    BLEPair.pairDevice(context, device.getBluetoothDevice());
                    return getCharacteristicsObservable(rxBleConnection);
                })
                //               .retry()
//                .retryWhen(new RetryWithDelay(2000))

/*
                .retryWhen(errors -> errors.flatMap((Func1<Throwable, Observable<?>>) throwable -> {
                    Log.e("abc", "Device=" + device.getMacAddress() + " error=" + throwable.toString());
                    for(int i=0;i<throwable.getStackTrace().length;i++)
                        Log.e("abc", "Device=" + device.getMacAddress() + " error[]=" + throwable.getStackTrace()[i]);

                    return Observable.timer(3000,
                            TimeUnit.MILLISECONDS);
//                    return Observable.new RetryWithDelay(3000);
//                    return Observable.just(null);
                }))

*/
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<Data>() {
                    @Override
                    public void onCompleted() {
                        Log.e("abc", "Device -> onCompleted()");

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("abc", "Device: onError()... e=" + e.toString());
                        for (int i = 0; i < e.getStackTrace().length; i++)
                            Log.e("abc", "Device: onError()... e[" + i + "]=" + e.getStackTrace()[i]);
                    }

                    @Override
                    public void onNext(Data data) {
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

    private void unsubscribeConnect() {
        Log.d("abc", "device=" + deviceId + " unsubscribeConnect() subscriptionConnect=" + subscriptionConnect);
        try {
            if (subscriptionConnect != null && !subscriptionConnect.isUnsubscribed())
                subscriptionConnect.unsubscribe();
            subscriptionConnect = null;
        } catch (Exception e) {
            Log.e("abc", "device=" + deviceId + " unsubscribeConnect() error = " + e.toString());
        }
    }
}
