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

class TranslateMag {
    static double[] getMagnetometer1(byte[] bytes) {
        double[] sample = new double[3];
        double[] sensitivity = getSensitivity(bytes);
        sample[0] = convertADCtoSI((short)((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff), sensitivity[0]);
        sample[1] = convertADCtoSI((short)((bytes[4] & 0xff) << 8) | (bytes[5] & 0xff), sensitivity[1]);
        sample[2] = convertADCtoSI((short)((bytes[8] & 0xff) << 8) | (bytes[9] & 0xff), sensitivity[2]);
        return sample;
    }

    static double[] getMagnetometer2(byte[] bytes) {
        double[] sample = new double[3];
        double[] sensitivity = getSensitivity(bytes);
        sample[0] = convertADCtoSI((short)((bytes[2] & 0xff) << 8) | (bytes[1] & 0xff), sensitivity[0]);
        sample[1] = convertADCtoSI((short)((bytes[6] & 0xff) << 8) | (bytes[5] & 0xff), sensitivity[1]);
        sample[2] = convertADCtoSI((short)((bytes[8] & 0xff) << 8) | (bytes[9] & 0xff), sensitivity[2]);
        return sample;
    }
    private static double convertADCtoSI(double mag, double sensitivity){
        return mag*((sensitivity-128)*.5/128.0+1);
    }

    static double[] getSensitivity(byte[] bytes) {
        double[] sample = new double[3];
        sample[0] = bytes[12] & 0xff;
        sample[1] = bytes[13] & 0xff;
        sample[2] = bytes[14] & 0xff;
        return sample;
    }

    static double[] getSequenceNumber(byte[] data) {
        int y=(data[15] & 0x03);
        int x=(data[16] & 0xff);
        int seq=(y<<8)| x;

        return new double[]{seq};
    }

    static double[] getRaw(byte[] bytes) {
        double[] sample = new double[bytes.length];
        for (int i = 0; i < bytes.length; i++)
            sample[i] = bytes[i];
        return sample;
    }

}
