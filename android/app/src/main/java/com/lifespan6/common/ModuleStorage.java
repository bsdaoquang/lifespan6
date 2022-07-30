package com.lifespan6.common;

import com.facebook.react.shell.MainReactPackage;
import com.lifespan6.bluetooth.DeviceConnectorPackage;
import com.lifespan6.info.InfoPackage;
import com.lifespan6.metric.hr.HeartBeatMeasurerPackage;

public class ModuleStorage {
    private static ModuleStorage instance;

    private MainReactPackage mainReactPackage;

    private DeviceConnectorPackage deviceConnectorPackage;

    private HeartBeatMeasurerPackage heartBeatMeasurerPackage;

    private InfoPackage infoPackage;

    private ModuleStorage(){
        mainReactPackage = new MainReactPackage();
        deviceConnectorPackage = new DeviceConnectorPackage();
        heartBeatMeasurerPackage = new HeartBeatMeasurerPackage();
        infoPackage = new InfoPackage();
    }

    public static ModuleStorage getModuleStorage(){
        if(instance == null){
            instance = new ModuleStorage();
        }
        return instance;
    }

    public MainReactPackage getMainReactPackage() {
        return mainReactPackage;
    }

    public DeviceConnectorPackage getDeviceConnectorPackage() {
        return deviceConnectorPackage;
    }

    public HeartBeatMeasurerPackage getHeartBeatMeasurerPackage() {
        return heartBeatMeasurerPackage;
    }

    public InfoPackage getInfoPackage() {
        return infoPackage;
    }
}
