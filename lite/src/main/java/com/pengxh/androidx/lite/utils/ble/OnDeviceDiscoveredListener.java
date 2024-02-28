package com.pengxh.androidx.lite.utils.ble;


public interface OnDeviceDiscoveredListener {
    void onDeviceFound(BluetoothBean bluetoothBean); //搜索到设备

    void onDiscoveryTimeout(); //扫描超时
}
