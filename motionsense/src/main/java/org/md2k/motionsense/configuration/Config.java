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
import android.widget.Toast;

import org.md2k.datakitapi.source.METADATA;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.platform.Platform;
import org.md2k.mcerebrum.commons.storage.Storage;
import org.md2k.motionsense.MyApplication;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

class Config {
    private static final String CONFIG_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mCerebrum/org.md2k.motionsense/";
    private static final String CONFIG_FILENAME = "config.json";

    static ArrayList<DataSource> read() {
        try {
            return Storage.readJsonArrayList(CONFIG_DIRECTORY+CONFIG_FILENAME, DataSource.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    static void write(ArrayList<DataSource> dataSources) {
        try {
            Storage.writeJsonArray(CONFIG_DIRECTORY+CONFIG_FILENAME, dataSources);
        } catch (Exception e) {
//            Toasty.error(MyApplication.getContext(), "Save configuration file - FAILED", Toast.LENGTH_LONG).show();
        }
    }
    static void deleteDevice(String deviceId) {
        ArrayList<DataSource> dataSources=read();
        ArrayList<DataSource> selected = new ArrayList<>();
        if (dataSources == null || dataSources.size() == 0) return ;
        for(int i=0;i<dataSources.size();i++){
            String d1 = dataSources.get(i).getPlatform().getMetadata().get(METADATA.DEVICE_ID);
            if(!d1.equals(deviceId))
                selected.add(dataSources.get(i));
        }
        write(selected);
    }

    static ArrayList<Platform> getPlatforms() {
        ArrayList<DataSource> dataSources=read();
        ArrayList<Platform> platforms=new ArrayList<>();
        boolean flag;
        for(int i=0;dataSources!=null && i<dataSources.size();i++){
            flag=false;
            for(int j=0;j<platforms.size();j++){
                if(dataSources.get(i).getPlatform().getType().equals(platforms.get(j).getType()) && dataSources.get(i).getPlatform().getId().equals(platforms.get(j).getId())) {
                    flag = true;
                    break;
                }
            }
            if(!flag)
                platforms.add(dataSources.get(i).getPlatform());
        }
        return platforms;
    }
    static boolean isConfigured() {
        ArrayList<DataSource> dataSources=read();
        return !(dataSources == null || dataSources.size() == 0);
    }

    static boolean isConfigured(String deviceId){
        ArrayList<DataSource> dataSources=read();
        if(dataSources==null || dataSources.size()==0) return false;
        for(int i=0;i<dataSources.size();i++)
            if(dataSources.get(i).getPlatform().getMetadata().get(METADATA.DEVICE_ID).equals(deviceId)) return true;
        return false;
    }

    static boolean isConfigured(String platformId, String deviceId) {
        ArrayList<DataSource> dataSources=read();
        if(dataSources==null || dataSources.size()==0) return false;
        for(int i=0;i<dataSources.size();i++) {
            if (dataSources.get(i).getPlatform().getMetadata().get(METADATA.DEVICE_ID).equals(deviceId))
                return true;
            if (dataSources.get(i).getPlatform().getId().equals(platformId))
                return true;
        }
        return false;
    }
}
