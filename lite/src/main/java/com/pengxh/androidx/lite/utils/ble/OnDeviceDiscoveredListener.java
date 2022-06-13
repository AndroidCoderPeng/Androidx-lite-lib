package com.pengxh.androidx.lite.utils.ble;


public interface OnDeviceDiscoveredListener {
    void onDeviceFound(BlueToothBean blueToothBean); //搜索到设备

    void onDiscoveryTimeout(); //扫描超时
}
