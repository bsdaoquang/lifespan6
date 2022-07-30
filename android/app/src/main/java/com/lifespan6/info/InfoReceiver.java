package com.lifespan6.info;

import static com.lifespan6.common.UUIDs.CHAR_STEPS;
import static com.lifespan6.common.UUIDs.SERVICE1;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.UUID;

public class InfoReceiver extends ReactContextBaseJavaModule {
    private BluetoothGattCharacteristic stepsChar;

    private BluetoothGatt btGatt;

    private String steps = "0";
    private String battery = "0";

    InfoReceiver(ReactApplicationContext context){
        super(context);
    }

    public void updateInfoChars(BluetoothGatt gatt){
        this.btGatt = gatt;
        BluetoothGattService service1 = btGatt.getService(UUID.fromString(SERVICE1));
        stepsChar = service1.getCharacteristic(UUID.fromString(CHAR_STEPS));
        btGatt.readCharacteristic(stepsChar);
    }

    /**
     * Returns main info from device including steps, battery level.
     * @param successCallback - a Callback instance that contains result of native code execution
     */
    @ReactMethod
    private void getInfo(Callback successCallback) {
        if(btGatt != null && stepsChar != null){
            btGatt.readCharacteristic(stepsChar);
        }
        successCallback.invoke(null, steps, battery);
    }

    /**
     * Updates steps variable with current step value on device side
     * @param value - an array with step value
     *
     */
    public void handleInfoData(final byte[] value) {
        if(value != null){
            byte receivedSteps = value[1];
            steps = String.valueOf(receivedSteps);
        }
    }

    /**
     * Updates steps variable with current battery value on device side
     * @param value - an array with battery value
     *
     */
    public void handleBatteryData(final byte[] value) {
        if(value != null){
            byte currentSteps = value[1];
            battery = String.valueOf(currentSteps);
        }
    }


    @NonNull
    @Override
    public String getName() {
        return "InfoReceiver";
    }
}
