import React, {useEffect, useState} from 'react';
import {
  View,
  Text,
  FlatList,
  TouchableOpacity,
  NativeModules,
} from 'react-native';

export const ScanScreen = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [devices, setDevices] = useState([]);

  //gọi đến module search device trong bluetooth/DeviceConnector/searchDevices
  const searchDevices = () => {
    NativeModules.DeviceConnector.discoverDevices((error, data) => {
      setDevices([data]);
    });
  };

  //gọi đến function connect trong module jaca
  //nếu kết nối thành công chuyển đến trang hiển thị thông tin thiết bị
  const handleConnectToDevice = mac => {
    NativeModules.DeviceConnector.linkWithDevice(macAddress, (error, data) => {
      //this.setState({deviceBondLevel: data});

      console.log(data.toString());

      // // toDo: get real deviceId instead of what's transferring to storage below...
      // AsyncStorage.setItem(
      //   globals.DEVICE_ID_KEY,
      //   (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1),
      // );
    });
    // this.setState({
    //   bluetoothSearchInterval: setInterval(this.getDeviceInfo, 15000),
    // });
  };

  const dataDefault = [
    {
      deviceMac: 'device 1',
      deviceName: 'Device 1',
    },

    {
      deviceMac: 'device 2',
      deviceName: 'Device 2',
    },

    {
      deviceMac: 'device 3',
      deviceName: 'Device 3',
    },

    {
      deviceMac: 'device 4',
      deviceName: 'Device 4',
    },
  ];

  useEffect(() => {
    setDevices(dataDefault);
  }, []);

  return (
    <View
      style={{
        flex: 1,
      }}
    >
      <View
        style={{
          padding: 20,
        }}
      >
        {/* 
          Nếu không tìm thấy thiết bị hoặc thiết bị thứ 1 là rỗng thì không hiện
         */}
        {devices.length > 0 && devices[0].deviceName !== undefined ? (
          <FlatList
            data={devices}
            renderItem={({item}) =>
              item.deviceName === undefined ? (
                <View />
              ) : (
                <TouchableOpacity
                  style={{
                    paddingVertical: 10,
                    borderBottomWidth: 1,
                    borderBottomColor: '#e0e0e0',
                  }}
                  onPress={() => handleConnectToDevice(item.deviceMac)}
                >
                  <Text
                    style={{
                      fontWeight: 'bold',
                      fontSize: 14,
                    }}
                  >
                    {item.deviceName}
                  </Text>
                  <Text>{item.deviceMac}</Text>
                </TouchableOpacity>
              )
            }
            keyExtractor={item => item.deviceMac}
          />
        ) : (
          <>
            <Text>Device not found</Text>
          </>
        )}
      </View>

      <View
        style={{
          flex: 1,
        }}
      >
        <TouchableOpacity
          onPress={searchDevices}
          style={{
            backgroundColor: '#16a085',
            padding: 10,
            position: 'absolute',
            bottom: 0,
            left: 0,
            right: 0,
            justifyContent: 'center',
            alignItems: 'center',
          }}
        >
          <Text
            style={{
              color: 'white',
            }}
          >
            Scan
          </Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};
