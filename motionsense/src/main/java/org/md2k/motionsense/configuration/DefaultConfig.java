package org.md2k.motionsense.configuration;
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

import android.os.Environment;

import org.md2k.mcerebrum.commons.storage.Storage;

import java.util.ArrayList;

class DefaultConfig {
    private static final String CONFIG_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mCerebrum/org.md2k.motionsense/";
    private static final String DEFAULT_CONFIG_FILENAME = "default_config.json";

    int required;
    ArrayList<Device> devices;

    static ArrayList<Sensor> getSensors(String platformType, String platformId) {
        DefaultConfig defaultConfig = read();
        if(defaultConfig==null) return null;
        for(int i=0;i<defaultConfig.devices.size();i++){
            if(defaultConfig.devices.get(i).platform_type.equals(platformType) && defaultConfig.devices.get(i).platform_id.equals(platformId))
                if(defaultConfig.devices.get(i).sensors!=null && defaultConfig.devices.get(i).sensors.size()!=0)
                    return defaultConfig.devices.get(i).sensors;
        }
        return null;
    }

    static String[] getPlatformId() {
        ArrayList<String> ids=new ArrayList<>();
        boolean flag;
        DefaultConfig defaultConfig = read();
        if(defaultConfig==null || defaultConfig.devices.size()==0) return null;
        for(int i=0;i<defaultConfig.devices.size();i++){
            flag=false;
            String d1 = defaultConfig.devices.get(i).platform_id;
            for(int j=0;j<ids.size();j++){
                if(ids.get(j).equals(d1)){
                    flag=true;
                    break;
                }
            }
            if(!flag){
                ids.add(d1);
            }
        }
        if(ids.size()==0) return null;
        String[] res=new String[ids.size()];
        for(int i=0;i<ids.size();i++){
            res[i]=ids.get(i);
        }
        return res;

    }

    class Device{
        String use_as;
        String platform_id;
        String platform_type;
        ArrayList<Sensor> sensors;
    }
    class Sensor{
        String id;
        String type;
    }
    static boolean hasDefault() {
        DefaultConfig defaultConfig = read();
        return defaultConfig != null;
    }
    static DefaultConfig read() {
        try {
            return Storage.readJson(CONFIG_DIRECTORY+DEFAULT_CONFIG_FILENAME, DefaultConfig.class);
        } catch (Exception e) {
            return null;
        }
    }

}
