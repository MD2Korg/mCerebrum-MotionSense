package org.md2k.motionsense.device.motionsense_hrv_plus;
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

import android.util.Log;

class TranslateLed {
    static double[] getAccelerometer(byte[] bytes) {
        double[] sample = new double[3];
        sample[0] = convertAccelADCtoSI((short)((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff));
        sample[1] = convertAccelADCtoSI((short)((bytes[2] & 0xff) << 8) | (bytes[3] & 0xff));
        sample[2] = convertAccelADCtoSI((short)((bytes[4] & 0xff) << 8) | (bytes[5] & 0xff));
        return sample;
    }
    private static double convertQuaternionToSI(double x){
        return (2.0*x)/(65535.0) - 1;
    }
    static double[] getQuaternion(byte[] bytes) {
        double[] sample = new double[3];
        sample[0] = convertQuaternionToSI((bytes[6] & 0xff) << 8 | (bytes[7] & 0xff));
        sample[1] = convertQuaternionToSI((bytes[8] & 0xff) << 8 | (bytes[9] & 0xff));
        sample[2] = convertQuaternionToSI((bytes[10] &0xff) << 8 | (bytes[11] & 0xff));
        return sample;
    }

    static double[] getSequenceNumber(byte[] data) {
        int seq = ((data[18] & 0xff) << 8) | (data[19] & 0xff);
        return new double[]{seq};
    }

    static double[] getLED(byte[] bytes) {
        double[] sample = new double[3];
        sample[0] = convertLEDValues((short)((bytes[12] & 0xff) << 8) | (bytes[13] & 0xff));
        sample[1] = convertLEDValues((short)((bytes[14] & 0xff) << 8) | (bytes[15] & 0xff));
        sample[2] = convertLEDValues((short)((bytes[16] & 0xff) << 8) | (bytes[17] & 0xff));
        return sample;
    }

    private static double convertAccelADCtoSI(double x) {
        return 2.0 * x / 16384;
    }
    private static double convertLEDValues(double x) {
        return (x*300.0)/(32767);
    }

    static double[] getRaw(byte[] bytes) {
        double[] sample = new double[bytes.length];
        for (int i = 0; i < bytes.length; i++)
            sample[i] = bytes[i];
        return sample;
    }
}
