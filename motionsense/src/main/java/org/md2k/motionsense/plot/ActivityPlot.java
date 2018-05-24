package org.md2k.motionsense.plot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeDouble;
import org.md2k.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.datakitapi.datatype.DataTypeFloat;
import org.md2k.datakitapi.datatype.DataTypeFloatArray;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.mcerebrum.commons.plot.RealtimeLineChartActivity;
import org.md2k.motionsense.ServiceMotionSense;

public class ActivityPlot extends RealtimeLineChartActivity {
    DataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            dataSource = getIntent().getExtras().getParcelable(DataSource.class.getSimpleName());
        }catch (Exception e){
            finish();
        }
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(ServiceMotionSense.INTENT_DATA));

        super.onResume();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DataSource ds = intent.getParcelableExtra(DataSource.class.getSimpleName());
            if(!ds.getType().equals(dataSource.getType())) return;
            if(ds.getId()!=null && dataSource.getId()!=null && !ds.getId().equals(dataSource.getId())) return;
            if(ds.getId()==null && dataSource.getId()!=null) return;
            if(ds.getId()!=null && dataSource.getId()==null) return;
            if(!ds.getPlatform().getId().equals(dataSource.getPlatform().getId())) return;
//            if(!ds.getPlatform().getType().equals(dataSource.getPlatform().getType())) return;
            updatePlot(intent, ds.getType());
        }
    };

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

        super.onPause();
    }

    void updatePlot(Intent intent, String ds) {
        float[] sample = new float[1];
        String[] legends;

        getmChart().getDescription().setText(dataSource.getType());
        getmChart().getDescription().setPosition(1f, 1f);
        getmChart().getDescription().setEnabled(true);
        getmChart().getDescription().setTextColor(Color.WHITE);
        switch (ds) {
            case DataSourceType.LED:
                legends = new String[]{"LED 1", "LED 2", "LED 3"};
                break;
            case DataSourceType.ACCELEROMETER:
                legends = new String[]{"Accelerometer X", "Accelerometer Y", "Accelerometer Z"};
                break;
            case DataSourceType.GYROSCOPE:
                legends = new String[]{"Gyroscope X", "Gyroscope Y", "Gyroscope Z"};
                break;
            case DataSourceType.MAGNETOMETER:
                legends = new String[]{"Magnetometer X", "Magnetometer Y", "Magnetometer Z"};
                break;
            case DataSourceType.QUATERNION:
                legends = new String[]{"Quaternion X", "Quaternion Y", "Quaternion Z"};
                break;
            case DataSourceType.MAGNETOMETER_SENSITIVITY:
                legends = new String[]{"Sensitivity X", "Sensitivity Y", "Sensitivity Z"};
                break;
            default:
                legends = new String[]{ds};
                break;
        }
        DataType[] datas = (DataType[]) intent.getParcelableArrayExtra(DataType.class.getSimpleName());
        for(int ii = 0;ii<datas.length;ii++) {
            DataType data = datas[ii];
            if (data instanceof DataTypeFloat) {
                sample = new float[]{((DataTypeFloat) data).getSample()};
            } else if (data instanceof DataTypeFloatArray) {
                sample = ((DataTypeFloatArray) data).getSample();
            } else if (data instanceof DataTypeDoubleArray) {
                double[] samples = ((DataTypeDoubleArray) data).getSample();
                sample = new float[samples.length];
                for (int i = 0; i < samples.length; i++) {
                    sample[i] = (float) samples[i];
                }
            } else if (data instanceof DataTypeDouble) {
                double samples = ((DataTypeDouble) data).getSample();
                sample = new float[]{(float) samples};
            }
            addEntry(sample, legends, 600);
        }
    }

}
