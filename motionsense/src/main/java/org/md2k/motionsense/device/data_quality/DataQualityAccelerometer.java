package org.md2k.motionsense.device.data_quality;



import android.util.Log;

import org.md2k.mcerebrum.core.data_format.DATA_QUALITY;

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
public class DataQualityAccelerometer extends DataQuality{
    private final static float MAGNITUDE_VARIANCE_THRESHOLD = (float) 0.01;   //this threshold comes from the data we collect by placing the wrist sensor on table. It compares with the wrist accelerometer on-body from participant #11 (smoking pilot study)


    public synchronized int getStatus() {
        try {
            int status;
            int size = samples.size();
            double samps[] = new double[size];
            for (int i = 0; i < size; i++)
                samps[i] = samples.get(i).getSample()[0];
            samples.clear();
            status = currentQuality(samps);
            return status;
        }catch (Exception e){
            return DATA_QUALITY.GOOD;
        }
    }

    private double getMean(double[] data) {
        double sum = 0.0;
        for (double a : data)
            sum += a;
        return sum / data.length;
    }

    private double getVariance(double[] data) {
        double mean = getMean(data);
        double temp = 0;
        for (double a : data)
            temp += (mean - a) * (mean - a);
        return temp / data.length;
    }

    private double getStdDev(double[] data) {
        return Math.sqrt(getVariance(data));
    }

    private int currentQuality(double[] x) {       //just receive x axis, in fact it should work with any single axis.
        int len_x = x.length;
        if (len_x == 0) return DATA_QUALITY.BAND_OFF;

        double sd =getStdDev(x);
        if (sd < MAGNITUDE_VARIANCE_THRESHOLD)
            return DATA_QUALITY.NOT_WORN;

        return DATA_QUALITY.GOOD;
    }


}