import React, {useState} from 'react';
import {
  View,
  Text,
  Flatlist,
  TouchableOpacity,
  NativeModules,
} from 'react-native';

export const ScanScreen = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [devices, setDevices] = useState([]);

  const searchDevices = () => {
    NativeModules.DeviceConnector.discoverDevices((error, data) => {
      setDevices(data);
      console.log(data);
    });
  };

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
        <Text>Phần hiện danh sách thiết bị</Text>
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
