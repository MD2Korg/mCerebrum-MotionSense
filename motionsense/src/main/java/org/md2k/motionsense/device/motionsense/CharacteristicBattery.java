package org.md2k.motionsense.device.motionsense;

import com.polidea.rxandroidble.RxBleConnection;

import org.md2k.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.motionsense.device.Characteristic;
import org.md2k.motionsense.Data;
import org.md2k.motionsense.device.Sensor;

import java.util.ArrayList;
import java.util.UUID;

import rx.Observable;

/**
 * Defines the battery characteristic of the device.
 */
public class CharacteristicBattery extends Characteristic {

    /**
     * Constructor
     */
    CharacteristicBattery() {
        super("00002A19-0000-1000-8000-00805f9b34fb", "CHARACTERISTIC_BATTERY", 25.0);
        //TODO fix frequency

    }

    /**
     * Returns an <code>Observable</code> over the data for this <code>Characteristic</code>.
     * @param rxBleConnection The BLE connection handle
     * @param sensors Arraylist of <code>Sensor</code>s
     * @return An <code>Observable</code> over the data for this <code>Characteristic</code>.
     */
    @Override
    public Observable<Data> getObservable(RxBleConnection rxBleConnection, ArrayList<Sensor> sensors) {
        UUID uuid = UUID.fromString(getId());
        return rxBleConnection.setupNotification(uuid)
                .flatMap(notificationObservable -> notificationObservable).map(bytes -> {
                    DataTypeDoubleArray battery = new DataTypeDoubleArray(DateTime.getDateTime(), TranslateBattery.getBattery(bytes));
                    return new Data(sensors.get(0), battery);
                });
    }
}
