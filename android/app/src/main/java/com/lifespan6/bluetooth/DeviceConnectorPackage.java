package com.lifespan6.bluetooth;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

public class DeviceConnectorPackage implements ReactPackage {
    private DeviceConnector deviceConnector;

    @Nonnull
    @Override
    public List<ViewManager> createViewManagers(@Nonnull ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public List<NativeModule> createNativeModules(
            ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        modules.add(initDeviceConnector(reactContext));
        return modules;
    }

    private DeviceConnector initDeviceConnector(ReactApplicationContext reactContext){
        deviceConnector = new DeviceConnector(reactContext);
        return deviceConnector;
    }

    public DeviceConnector getDeviceConnector() {
        return deviceConnector;
    }
}
