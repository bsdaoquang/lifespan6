package com.lifespan6.bluetooth;

import static android.content.Context.BLUETOOTH_SERVICE;

import static com.lifespan6.common.ModuleStorage.getModuleStorage;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.lifespan6.common.GattCallback;
import com.lifespan6.metric.hr.HeartBeatMeasurer;
import com.lifespan6.metric.hr.HeartBeatMeasurerPackage;

import java.util.Objects;

public class DeviceConnector extends ReactContextBaseJavaModule {

    // Bluetooth variable section
    private BluetoothGatt bluetoothGatt;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private GattCallback gattCallback;
    private ProgressDialog searchProgressDialog;

    private String currentDeviceMacAddress;

    DeviceConnector(ReactApplicationContext context){
        super(context);

        HeartBeatMeasurerPackage hBMeasurerPackage = getModuleStorage().getHeartBeatMeasurerPackage();
        HeartBeatMeasurer heartBeatMeasurer = hBMeasurerPackage.getHeartBeatMeasurer();
        gattCallback = new GattCallback(heartBeatMeasurer);
    }

    @ReactMethod
    public void discoverDevices(Callback successCallback) {
        Context mainContext = getReactApplicationContext().getCurrentActivity();
        bluetoothAdapter = ((BluetoothManager) Objects.requireNonNull(mainContext)
                .getSystemService(BLUETOOTH_SERVICE))
                .getAdapter();

        searchProgressDialog = new ProgressDialog(mainContext);
        searchProgressDialog.setIndeterminate(true);
        searchProgressDialog.setTitle("Bluetooth Scanner");
        searchProgressDialog.setMessage("Searching...");
        searchProgressDialog.setCancelable(false);
        searchProgressDialog.show();

        if (!bluetoothAdapter.isEnabled()) {
            ((AppCompatActivity)mainContext)
                    .startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                            0);
        }

        final DeviceScanCallback deviceScanCallback = new DeviceScanCallback();
        BluetoothLeScanner bluetoothScanner = bluetoothAdapter.getBluetoothLeScanner();
        if(bluetoothScanner != null){
            bluetoothScanner.startScan(deviceScanCallback);
        }

        final int DISCOVERY_TIME_DELAY_IN_MS = 15000;
        new Handler().postDelayed(() -> {
            bluetoothAdapter.getBluetoothLeScanner().stopScan(deviceScanCallback);
            searchProgressDialog.dismiss();
            successCallback.invoke(null, deviceScanCallback.getDiscoveredDevices());
        }, DISCOVERY_TIME_DELAY_IN_MS);

    }

    /**
     * Tries to connect a found miband device with tha app. In case of succeed a bound level value
     * will be send back to be displayed on UI.
     * @param macAddress - MAC address of a device that must be linked with app
     * @param successCallback - a Callback instance that will be needed in the end of discovering
     *                        process to send back a result of work.
     */
    @ReactMethod
    public void linkWithDevice(String macAddress, Callback successCallback) {
        currentDeviceMacAddress = macAddress;
        updateBluetoothGatt();
        getModuleStorage().getHeartBeatMeasurerPackage()
                .getHeartBeatMeasurer()
                .updateBluetoothConfig(bluetoothGatt);
        successCallback.invoke(null, bluetoothGatt.getDevice().getBondState());
    }

    @ReactMethod
    void disconnectDevice(Callback successCallback) {
        if(bluetoothGatt != null){
            bluetoothGatt.disconnect();
            bluetoothGatt = null;
        }
        bluetoothDevice = null;
        bluetoothAdapter = null;
        successCallback.invoke(null, 0);
    }

    /**
     * Returns a bluetooth bound level of connection between miband device and android app.
     * Used by react UI part when connection has been established.
     * @param successCallback - a Callback instance that will be needed in the end of discovering
     *                        process to send back a result of work.
     */
    @ReactMethod
    private void getDeviceBondLevel(Callback successCallback){
        if (bluetoothGatt == null){
            successCallback.invoke(null, 0);
        } else {
            successCallback.invoke(null, bluetoothGatt.getDevice().getBondState());

        }
    }

    @NonNull
    @Override
    public String getName() {
        return "DeviceConnector";
    }

    private void updateBluetoothGatt(){
        Context mainContext = getReactApplicationContext().getCurrentActivity();
        bluetoothAdapter = ((BluetoothManager) Objects.requireNonNull(mainContext)
                .getSystemService(BLUETOOTH_SERVICE))
                .getAdapter();

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(currentDeviceMacAddress);
        setBluetoothDevice(device);
        HeartBeatMeasurerPackage hBMeasurerPackage = getModuleStorage().getHeartBeatMeasurerPackage();
        HeartBeatMeasurer heartBeatMeasurer = hBMeasurerPackage.getHeartBeatMeasurer();
        gattCallback = new GattCallback(heartBeatMeasurer);
        bluetoothGatt = bluetoothDevice.connectGatt(mainContext, true, gattCallback);
    }

    void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }
}
