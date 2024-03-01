package com.pengxh.androidx.lite.utils.ble;


import android.bluetooth.BluetoothDevice;

public interface OnDeviceDiscoveredListener {
    void onDeviceFound(BluetoothDevice bluetoothDevice); //搜索到设备

    void onDeviceDiscoveryEnd(); //扫描超时
}
