package org.md2k.motionsense.configuration;

import android.content.Context;

import org.md2k.datakitapi.source.METADATA;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.platform.Platform;
import org.md2k.datakitapi.source.platform.PlatformBuilder;

import java.util.ArrayList;

/*
 * Copyright (c) 2015, The University of Memphis, MD2K Center
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
public class ConfigurationManager {

    public static boolean isEqualDefault() {
        boolean flag;
        ArrayList<Platform> platforms=Config.getPlatforms();
        DefaultConfig defaultConfig=DefaultConfig.read();
        if(defaultConfig==null) return true;
        if(defaultConfig.required!=0 && defaultConfig.required!=platforms.size()) return false;
        for(int i = 0;i<defaultConfig.devices.size();i++){
            if(defaultConfig.devices.get(i).use_as.equalsIgnoreCase("OPTIONAL")) continue;
            flag=false;
            for(int j=0;j<platforms.size();j++){
                if(defaultConfig.devices.get(i).platform_type.equals(platforms.get(j).getType()) && defaultConfig.devices.get(i).platform_id.equals(platforms.get(j).getId())) {
                    flag = true;
                    break;
                }
            }
            if(!flag) return false;
        }
        return true;
    }

    public static ArrayList<Platform> getPlatforms() {
        return Config.getPlatforms();
    }

    public static ArrayList<DataSource> getDataSources(ArrayList<DataSource> dataSources, Platform platform){
        ArrayList<DataSource> selected=new ArrayList<>();
        for(int i=0;i<dataSources.size();i++){
            if(dataSources.get(i).getPlatform().getType().equals(platform.getType()) && dataSources.get(i).getPlatform().getId().equals(platform.getId()))
                selected.add(dataSources.get(i));
        }
        return selected;
    }
    public static String[] getPlatformIdFromDefault() {
        return DefaultConfig.getPlatformId();
    }

    public static boolean hasDefault() {
        return DefaultConfig.hasDefault();
    }

    public static void deleteDevice(String deviceId) {
        Config.deleteDevice(deviceId);
    }

    public static boolean isConfigured(String platformId, String deviceId) {
        return Config.isConfigured(platformId, deviceId);
    }

    public static boolean isConfigured(String deviceId) {
        return Config.isConfigured(deviceId);
    }

    public static void addPlatform(Context context, String platformType, String platformId, String deviceId) {
        ArrayList<DataSource> res=getDataSources(context, platformType, platformId, deviceId);
       ArrayList<DataSource> d = Config.read();
       if(d==null || d.size()==0) d = res;
       else d.addAll(res);
       Config.write(d);
    }

    public static boolean isConfigured() {
        return Config.isConfigured();
    }
    private static ArrayList<DataSource> getDataSources(Context context, String platformType, String platformId, String deviceId){
        ArrayList<DefaultConfig.Sensor> sensors = DefaultConfig.getSensors(platformType, platformId);
        ArrayList<DataSource> res = new ArrayList<>();
        ArrayList<DataSource> dataSources=new ArrayList<>();
        if(sensors==null || sensors.size()==0){
            dataSources = MetaData.getDataSources(context, platformType);
        }else{
            for(int i=0;i<sensors.size();i++){
                DataSource dataSource=MetaData.getDataSource(context, sensors.get(i).type, sensors.get(i).id, platformType);
                if(dataSource!=null)
                    dataSources.add(dataSource);
            }
        }
        for(int i=0;i<dataSources.size();i++){
            PlatformBuilder platformBuilder = new PlatformBuilder(dataSources.get(i).getPlatform());
            platformBuilder=platformBuilder.setType(platformType).setId(platformId).setMetadata(METADATA.DEVICE_ID, deviceId);

            DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(dataSources.get(i));
            dataSourceBuilder = dataSourceBuilder.setPlatform(platformBuilder.build());
            res.add(dataSourceBuilder.build());
        }
        return res;
    }

    public static ArrayList<DataSource> read(Context context) {
        ArrayList<DataSource> res = new ArrayList<>();
        ArrayList<Platform> platforms = Config.getPlatforms();
        for(int i=0;i<platforms.size();i++){
            ArrayList<DataSource> temp = getDataSources(context, platforms.get(i).getType(), platforms.get(i).getId(), platforms.get(i).getMetadata().get(METADATA.DEVICE_ID));
            if(temp!=null && temp.size()!=0)
                res.addAll(temp);
        }
        Config.write(res);
        return res;
    }
}
