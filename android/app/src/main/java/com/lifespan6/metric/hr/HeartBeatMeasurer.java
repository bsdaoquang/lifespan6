package com.lifespan6.metric.hr;

import static com.lifespan6.common.UUIDs.CHAR_HEART_RATE_CONTROL;
import static com.lifespan6.common.UUIDs.CHAR_HEART_RATE_MEASURE;
import static com.lifespan6.common.UUIDs.CHAR_SENSOR;
import static com.lifespan6.common.UUIDs.SERVICE1;
import static com.lifespan6.common.UUIDs.SERVICE_HEART_RATE;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.UUID;

public class HeartBeatMeasurer extends ReactContextBaseJavaModule {

    private BluetoothGattService service1;
    private BluetoothGattService heartService;
    private BluetoothGattCharacteristic hrCtrlChar;
    private BluetoothGattCharacteristic hrMeasureChar;
    private BluetoothGattCharacteristic sensorChar;

    private BluetoothGatt btGatt;
    private String heartRateValue = "0";

    HeartBeatMeasurer(ReactApplicationContext context){
        super(context);
    }

    public void updateHrChars(BluetoothGatt gatt){
        this.btGatt = gatt;
        service1 = btGatt.getService(UUID.fromString(SERVICE1));
        heartService = btGatt.getService(UUID.fromString(SERVICE_HEART_RATE));

        hrCtrlChar = heartService.getCharacteristic(UUID.fromString(CHAR_HEART_RATE_CONTROL));
        hrMeasureChar = heartService.getCharacteristic(UUID.fromString(CHAR_HEART_RATE_MEASURE));
        sensorChar = service1.getCharacteristic(UUID.fromString(CHAR_SENSOR));

        btGatt.setCharacteristicNotification(hrCtrlChar, true);
        btGatt.setCharacteristicNotification(hrMeasureChar, true);
    }

    /**
     * Reads recieved data from miband device with current heart beat state.
     * @param characteristic GATT characteristic is a basic data element used
     *                       to construct a GATT service
     */
    public void handleHeartRateData(final BluetoothGattCharacteristic characteristic) {
        // byte currentHrValue = characteristic.getValue()[1];
        int format = -1;
        format = BluetoothGattCharacteristic.FORMAT_UINT8;
        final int heartRate2 = characteristic.getIntValue(format, 3);
        final int heartRate = (characteristic.getIntValue(format, 4)  +( heartRate2 * 256 )) / 36;
        heartRateValue = String.valueOf(heartRate);
    }

    /**
     * Starts heartBeat data fetching from miband device.
     */
    @ReactMethod
    private void startHrCalculation(Callback successCallback) {
        // sensorChar.setValue(new byte[]{0x01, 0x03, 0x19});
        btGatt.readCharacteristic(hrMeasureChar);

        successCallback.invoke(null, heartRateValue);
    }

    @ReactMethod
    private void stopHrCalculation() {
        hrCtrlChar.setValue(new byte[]{0x15, 0x01, 0x00});
        Log.d("INFO","hrCtrlChar: " + btGatt.writeCharacteristic(hrCtrlChar));
    }

    /**
     * Returns current heart beat value.
     * @param successCallback - a Callback instance that contains result of native code execution
     */
    @ReactMethod
    private void getHeartRate(String currentHeartBeat, Callback successCallback) {
        if(Integer.valueOf(heartRateValue).equals(Integer.valueOf(currentHeartBeat))){
            // hrCtrlChar.setValue(new byte[]{0x16});
            // btGatt.writeCharacteristic(hrCtrlChar);
            btGatt.readCharacteristic(hrMeasureChar);
        }
        successCallback.invoke(null, heartRateValue);
    }

    /**
     * Re-inits BluetoothGatt instance in case bluetooth connection was interrupted somehow.
     * @param bluetoothGatt instance to be re-initialized
     */
    @ReactMethod
    public void updateBluetoothConfig(BluetoothGatt bluetoothGatt){
        this.btGatt = bluetoothGatt;
    }

    @NonNull
    @Override
    public String getName() {
        return "HeartBeatMeasurer";
    }
}
