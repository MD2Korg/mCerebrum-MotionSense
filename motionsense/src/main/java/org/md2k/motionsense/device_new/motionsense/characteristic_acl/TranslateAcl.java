package org.md2k.motionsense.device_new.motionsense.characteristic_acl;
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

import org.md2k.datakitapi.source.platform.PlatformType;

class TranslateAcl {
    private byte[] data;
    private long timestamp;
    String platformType;

     static double[] getAccelerometer(byte[] bytes) {
        double[] sample = new double[3];
        sample[0] = convertAccelADCtoSI(byteArrayToIntBE(new byte[]{bytes[0], bytes[1]}));
        sample[1] = convertAccelADCtoSI(byteArrayToIntBE(new byte[]{bytes[2], bytes[3]}));
        sample[2] = convertAccelADCtoSI(byteArrayToIntBE(new byte[]{bytes[4], bytes[5]}));
        return sample;
    }
     static double[] getGyroscope(byte[] bytes) {
        double[] sample = new double[3];
        sample[0] = convertGyroADCtoSI(byteArrayToIntBE(new byte[]{bytes[6], bytes[7]}));
        sample[1] = convertGyroADCtoSI(byteArrayToIntBE(new byte[]{bytes[8], bytes[9]}));
        sample[2] = convertGyroADCtoSI(byteArrayToIntBE(new byte[]{bytes[10], bytes[11]}));
        return sample;
    }
    static double[] getGyroscope2(byte[] bytes) {
        double[] sample = new double[3];
        sample[0] = convertGyroADCtoSI(byteArrayToIntBE(new byte[]{bytes[12], bytes[13]}));
        sample[1] = convertGyroADCtoSI(byteArrayToIntBE(new byte[]{bytes[14], bytes[15]}));
        sample[2] = convertGyroADCtoSI(byteArrayToIntBE(new byte[]{bytes[16], bytes[17]}));
        return sample;
    }



     static double[] getSequenceNumber(byte[] data) {
        int seq=byteArrayToIntBE(new byte[]{data[18], data[19]});
        return new double[]{seq};
    }

    double[] getSequenceNumber() {
        int seq;
        if(platformType.equals(PlatformType.MOTION_SENSE))
            seq=byteArrayToIntBE(new byte[]{data[18], data[19]});
        else{
            int x=(data[19] & 0x00000000000000ff);
            int y=(data[18] & 0x03);
            seq=(y<<8)+x;
        }
        return new double[]{seq};
    }




    private static double convertAccelADCtoSI(double x) {
        return 2.0 * x / 16384;
    }
    private static int byteArrayToIntBE(byte[] bytes) {
        return java.nio.ByteBuffer.wrap(bytes).getShort();
    }
    private static double convertGyroADCtoSI(double x) {
        return 500.0 * x / 32768;
    }

    static double[] getRaw(byte[] bytes) {
            double[] sample=new double[bytes.length];
            for(int i=0;i<bytes.length;i++)
                sample[i]=bytes[i];
            return sample;
    }
}
