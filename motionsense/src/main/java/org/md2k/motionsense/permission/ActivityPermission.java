package org.md2k.motionsense.permission;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.md2k.mcerebrum.core.access.MCerebrum;

import es.dmoral.toasty.Toasty;

public class ActivityPermission extends AppCompatActivity {
    private static final int REQUEST_ENABLE_GPS = 1121;
    private static final int REQUEST_ENABLE_BT = 1122;
    BluetoothAdapter bluetoothAdapter;
    private static final int ERROR_PERMISSION=1;
    private static final int ERROR_BLUETOOTH=2;
    private static final int ERROR_GPS=3;
    private static final int SUCCESS=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Permission.requestPermission(this, new PermissionCallback() {
            @Override
            public void OnResponse(boolean isGranted) {
                if (!isGranted) {
                    setStatus(ERROR_PERMISSION);
                } else
                    enableBluetooth();
            }
        });

//        setContentView(R.layout.activity_permission);
    }
    void setStatus(int status){
        switch(status){
            case ERROR_BLUETOOTH:
                Toasty.error(getApplicationContext(), "MotionSense - !!! Bluetooth OFF !!! Could not continue...", Toast.LENGTH_SHORT).show();
                MCerebrum.setPermission(ActivityPermission.this, false);
                setResult(Activity.RESULT_CANCELED);
                break;
            case ERROR_GPS:
                Toasty.error(getApplicationContext(), "MotionSense - !!! GPS OFF !!! Could not continue...", Toast.LENGTH_SHORT).show();
                MCerebrum.setPermission(ActivityPermission.this, false);
                setResult(Activity.RESULT_CANCELED);
                break;
            case ERROR_PERMISSION:
                Toasty.error(getApplicationContext(), "MotionSense - !!! PERMISSION DENIED !!! Could not continue...", Toast.LENGTH_SHORT).show();
                MCerebrum.setPermission(ActivityPermission.this, false);
                setResult(Activity.RESULT_CANCELED);
                break;
            case SUCCESS:
                MCerebrum.setPermission(ActivityPermission.this, true);
                setResult(Activity.RESULT_OK);
            default:
        }
        finish();
    }
    private void enableBluetooth(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isEnabled()) {
            enableGPS();
        } else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode==RESULT_OK)
                    enableGPS();
                else setStatus(ERROR_BLUETOOTH);
                break;
            case REQUEST_ENABLE_GPS:
                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
                if(manager==null) {setStatus(ERROR_GPS);return;}
                boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if(!statusOfGPS)
                    setStatus(ERROR_GPS);
                else setStatus(SUCCESS);
        }
    }

    public void enableGPS() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        if(manager==null) {setStatus(ERROR_GPS);return;}
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!statusOfGPS){
            Intent gpsOptionsIntent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(gpsOptionsIntent, REQUEST_ENABLE_GPS);
        }else
            setStatus(SUCCESS);
    }
}
