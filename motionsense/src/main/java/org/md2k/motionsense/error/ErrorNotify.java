package org.md2k.motionsense.error;
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
import android.os.Bundle;

import org.md2k.motionsense.ActivityMain;
import org.md2k.motionsense.R;

import br.com.goncalves.pugnotification.notification.PugNotification;

public class ErrorNotify {
    public static final int PERMISSION=0;
    public static final int BLUETOOTH_OFF=1;
    public static final int GPS_OFF=2;
    public static final int NOT_CONFIGURED=3;
    public static final int DATAKIT_CONNECTION_ERROR =4;
    public static final int DATAKIT_REGISTRATION_ERROR=5;
    public static final int DATAKIT_INSERT_ERROR=6;
    public static void handle(Context context, int type){
     switch(type){
         case PERMISSION:
             showNotification(context, "MotionSense App: Permission required", "(Please click to continue)");
             break;
         case BLUETOOTH_OFF:
             showNotification(context, "MotionSense App: Bluetooth Disabled", "(Please click to enable bluetooth)");
             break;
         case GPS_OFF:
             showNotification(context, "MotionSense App: GPS off", "(Please click to turn on GPS)");
             break;
         case NOT_CONFIGURED:
//             showNotification("MotionSense App: GPS off", "(Please click to turn on GPS)");
             break;
         case DATAKIT_CONNECTION_ERROR:
//             showNotification("MotionSense App: GPS off", "(Please click to turn on GPS)");
             break;
         case DATAKIT_REGISTRATION_ERROR:
//             showNotification("MotionSense App: GPS off", "(Please click to turn on GPS)");
             break;
         case DATAKIT_INSERT_ERROR:
//             showNotification("MotionSense App: GPS off", "(Please click to turn on GPS)");
             break;

     }
    }
    private static void showNotification(Context context, String title, String message) {
        Bundle bundle = new Bundle();
        bundle.putInt(ActivityMain.OPERATION, ActivityMain.OPERATION_START_BACKGROUND);
        PugNotification.with(context).load().identifier(21).title(title).smallIcon(R.mipmap.ic_launcher)
                .message(message).autoCancel(true).click(ActivityMain.class, bundle).simple().build();
    }
    public static void removeNotification(Context context) {
        PugNotification.with(context).cancel(21);
    }
}
