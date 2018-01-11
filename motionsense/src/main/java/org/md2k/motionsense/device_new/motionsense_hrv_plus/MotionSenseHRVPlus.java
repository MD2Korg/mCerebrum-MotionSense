package org.md2k.motionsense.device_new.motionsense_hrv_plus;
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

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.source.METADATA;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.source.platform.Platform;
import org.md2k.datakitapi.source.platform.PlatformBuilder;
import org.md2k.motionsense.MetaData;
import org.md2k.motionsense.MyApplication;
import org.md2k.motionsense.device_new.Characteristic;
import org.md2k.motionsense.device_new.motionsense_hrv_plus.characteristic_acl.CharacteristicAcl;
import org.md2k.motionsense.device_new.motionsense_hrv_plus.characteristic_battery.CharacteristicBattery;
import org.md2k.motionsense.device_new.motionsense_hrv_plus.characteristic_magnitude.CharacteristicMag;

import java.util.ArrayList;
import java.util.HashMap;

public class MotionSenseHRVPlus {
    private String address;
    private String platformId;
    private CharacteristicAcl cAcl;
    private CharacteristicBattery cBat;
    private CharacteristicMag cMag;
    protected HashMap<String, DataSourceClient> dsc;

    public MotionSenseHRVPlus(String address, String platformId, ArrayList<DataSource> dataSources){
        this.address = address;
        this.platformId = platformId;
        cAcl=new CharacteristicAcl();
        cBat=new CharacteristicBattery();
        cMag=new CharacteristicMag();
        dsc=new HashMap<>();

    }
/*
    public void register(){
        cAcl.register(platformId, address);
        cBat.register(platformId, address);
        cMag.register(platformId, address);
    }
    public String[] getCharacteristicsID(){
        return new String[]{cAcl.getId(), cMag.getId(), cBat.getId()};
    }
    public void insert(byte[] bytes){
        switch(bytes.length){
            case 20: cAcl.insert(bytes);break;
            case 2: cBat.insert(bytes);break;
            case 17: cMag.insert(bytes);break;
            default:break;
        }
    }
*/
/*
    public abstract void register(String platformId, String address);
    public void register(String platformType, String platformId, String address, String dataSourceType, String dataSourceId){
        try {
            DataSource temp= MetaData.getDataSource(dataSourceType, dataSourceId, platformType);
            Platform platform=new PlatformBuilder(temp.getPlatform()).setType(platformType).setId(id).setMetadata(METADATA.DEVICE_ID, address).build();
            DataSourceBuilder dsb=new DataSourceBuilder(temp).setPlatform(platform);

            DataSourceClient d = DataKitAPI.getInstance(MyApplication.getContext()).register(dsb);
            dsc.put(dataSourceType, d);
        }catch (Exception e){

        }
    }
     @Override
    public void insert(byte[] bytes) {
        try {
            double[] s = TranslateAcl.getSequenceNumber(bytes);
            int seqNo = (int) s[0];
            DataTypeDoubleArray seq = correctTimeStamp(seqNo, s);
            DataTypeDoubleArray acl = correctTimeStamp(seqNo, TranslateAcl.getAccelerometer(bytes));
            DataTypeDoubleArray led = correctTimeStamp(seqNo, TranslateAcl.getLED(bytes));
            DataTypeDoubleArray gyro = correctTimeStamp(seqNo, TranslateAcl.getGyroscope(bytes));
            DataTypeDoubleArray raw = correctTimeStamp(seqNo, TranslateAcl.getRaw(bytes));

            DataKitAPI.getInstance(MyApplication.getContext()).insertHighFrequency(dsc.get(DataSourceType.ACCELEROMETER), acl);
            DataKitAPI.getInstance(MyApplication.getContext()).insertHighFrequency(dsc.get(DataSourceType.GYROSCOPE), gyro);
            DataKitAPI.getInstance(MyApplication.getContext()).insertHighFrequency(dsc.get(DataSourceType.LED), led);
            DataKitAPI.getInstance(MyApplication.getContext()).insertHighFrequency(dsc.get(DataSourceType.SEQUENCE_NUMBER), seq);
            DataKitAPI.getInstance(MyApplication.getContext()).insertHighFrequency(dsc.get(DataSourceType.RAW), raw);
        } catch (Exception e) {

        }
    }
    @Override
    public void register(String platformId, String address) {
        register(PlatformType.MOTION_SENSE_HRV, platformId, address, DataSourceType.ACCELEROMETER, null);
        register(PlatformType.MOTION_SENSE_HRV, platformId, address, DataSourceType.GYROSCOPE, null);
        register(PlatformType.MOTION_SENSE_HRV, platformId, address, DataSourceType.LED, null);
        register(PlatformType.MOTION_SENSE_HRV, platformId, address, DataSourceType.SEQUENCE_NUMBER, null);
        register(PlatformType.MOTION_SENSE_HRV, platformId, address, DataSourceType.RAW, "ACL_GYRO_LED");

    }

*/
}
