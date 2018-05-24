package org.md2k.motionsense;
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

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.polidea.rxandroidble.RxBleClient;
import com.polidea.rxandroidble.internal.RxBleLog;

import org.md2k.mcerebrum.commons.debug.MyLogger;
import org.md2k.mcerebrum.core.access.MCerebrum;

public class MyApplication extends Application {
    private RxBleClient rxBleClient;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("abc","MyApplication.. onCreate()");
        rxBleClient = RxBleClient.create(this);
//        RxBleClient.setLogLevel(RxBleLog.);
        MCerebrum.init(getApplicationContext(), MyMCerebrumInit.class);
        Log.d("abc","rxBleClient: state="+rxBleClient.getState().toString());
        Log.d("abc","rxBleClient: bondedDevices size="+rxBleClient.getBondedDevices().size());
        MyLogger.setLogger(getApplicationContext());
    }

    public static RxBleClient getRxBleClient(Context context) {
        MyApplication application = (MyApplication) context.getApplicationContext();
        Log.d("abc","rxBleClient: state="+application.rxBleClient.getState().toString());
        Log.d("abc","rxBleClient: bondedDevices size="+application.rxBleClient.getBondedDevices().size());
        return application.rxBleClient;
    }
}

