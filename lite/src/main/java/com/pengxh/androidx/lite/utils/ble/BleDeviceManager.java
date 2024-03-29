package com.pengxh.androidx.lite.utils.ble;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.pengxh.androidx.lite.hub.StringHub;
import com.pengxh.androidx.lite.utils.Constant;
import com.pengxh.androidx.lite.utils.WeakReferenceHandler;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 1、扫描设备
 * 2、配对设备
 * 3、解除设备配对
 * 4、连接设备
 * 6、发现服务
 * 7、打开读写功能
 * 8、数据通讯（发送数据、接收数据）
 * 9、断开连接
 */
public class BleDeviceManager implements Handler.Callback {
    private static final String TAG = "BleDeviceManager";
    /**
     * 解决双重锁单例Context导致内存泄漏的问题
     */
    private static WeakReference<Context> weakReferenceContext;
    private final WeakReferenceHandler weakReferenceHandler = new WeakReferenceHandler(this);
    private BluetoothAdapter bluetoothAdapter;
    private boolean isConnecting = false;
    private OnDeviceDiscoveredListener bleDiscoveryListener;
    private OnDeviceConnectListener bleConnectListener;
    private UUID serviceUuid;
    private UUID writeUuid;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic writeCharacteristic;

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        return true;
    }

    /**
     * 采用单例模式进行调用
     */
    private BleDeviceManager() {

    }

    private static class BLEManagerHolder {
        private static final BleDeviceManager INSTANCE = new BleDeviceManager();
    }

    public static BleDeviceManager get(Context context) {
        weakReferenceContext = new WeakReference<>(context);
        return BLEManagerHolder.INSTANCE;
    }

    /**
     * 初始化BLE
     */
    public boolean init() {
        BluetoothManager bluetoothManager = (BluetoothManager) weakReferenceContext.get().getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        return bluetoothAdapter != null;
    }

    /**
     * 打开蓝牙
     */
    public void openBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            if (checkConnectPermission()) {
                weakReferenceContext.get().startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
            }
        }
    }

    /**
     * 蓝牙是否已打开
     */
    public boolean isBluetoothEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    /**
     * 本地蓝牙是否处于正在扫描状态
     *
     * @return true false
     */
    public boolean isDiscovering() {
        if (bluetoothAdapter == null) {
            return false;
        }
        if (checkScanPermission()) {
            return bluetoothAdapter.isDiscovering();
        } else {
            return false;
        }
    }

    /**
     * 开始扫描设备
     */
    public void startScanDevice(OnDeviceDiscoveredListener listener, long scanTime) {
        if (bluetoothAdapter == null) {
            return;
        }
        this.bleDiscoveryListener = listener;
        if (checkScanPermission()) {
            bluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);
            //设定最长扫描时间
            weakReferenceHandler.postDelayed(stopScanRunnable, scanTime);
        }
    }

    /**
     * 停止扫描设备
     */
    public void stopDiscoverDevice() {
        weakReferenceHandler.removeCallbacks(stopScanRunnable);
    }

    @SuppressLint("MissingPermission")
    public final Runnable stopScanRunnable = new Runnable() {
        @Override
        public void run() {
            if (bleDiscoveryListener != null) {
                bleDiscoveryListener.onDeviceDiscoveryEnd();
            }
            //scanTime之后还没有扫描到设备，就停止扫描。
            if (bluetoothAdapter == null) {
                return;
            }
            if (checkScanPermission()) {
                bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
            }
        }
    };

    /**
     * 扫描设备回调
     */
    public final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (result == null) {
                return;
            }
            BluetoothDevice device = result.getDevice();
            if (checkConnectPermission()) {
                if (device.getName() != null || !TextUtils.isEmpty(device.getName())) {
                    if (bleDiscoveryListener != null) {
                        bleDiscoveryListener.onDeviceFound(device);
                    }
                }
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {

        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.d(TAG, "onScanFailed: errorCode ===> " + errorCode);
        }
    };

    /**
     * 连接设备
     */
    public void connectBleDevice(BluetoothDevice device, String serviceUuid, String writeUuid, long connectTime, OnDeviceConnectListener listener) {
        if (isConnecting) {
            Log.d(TAG, "connectBleDevice() ===> isConnecting = true");
            return;
        }
        this.serviceUuid = UUID.fromString(serviceUuid);
        this.writeUuid = UUID.fromString(writeUuid);
        this.bleConnectListener = listener;
        if (checkConnectPermission()) {
            Log.d(TAG, "开始准备连接：" + device.getName() + " ===> " + device.getAddress());
            try {
                bluetoothGatt = device.connectGatt(weakReferenceContext.get(), false, bluetoothGattCallback);
                bluetoothGatt.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //设置连接超时时间10s
            weakReferenceHandler.postDelayed(connectTimeoutRunnable, connectTime);
        }
    }

    public final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            BluetoothDevice bluetoothDevice = gatt.getDevice();
            if (checkConnectPermission()) {
                Log.d(TAG, "连接的设备：" + bluetoothDevice.getName() + " ===> " + bluetoothDevice.getAddress());
                isConnecting = true;
                //移除连接超时
                weakReferenceHandler.removeCallbacks(connectTimeoutRunnable);
                switch (newState) {
                    case BluetoothGatt.STATE_CONNECTING:
                        Log.d(TAG, "正在连接...");
                        if (bleConnectListener != null) {
                            //正在连接回调
                            bleConnectListener.onConnecting(gatt);
                        }
                        break;
                    case BluetoothGatt.STATE_CONNECTED:
                        Log.d(TAG, "连接成功");
                        //连接成功去发现服务
                        gatt.discoverServices();
                        //设置发现服务超时时间
                        weakReferenceHandler.postDelayed(serviceDiscoverTimeoutRunnable, Constant.MAX_CONNECT_TIME);
                        if (bleConnectListener != null) {
                            bleConnectListener.onConnectSuccess(gatt, status);
                        }
                        break;
                    case BluetoothGatt.STATE_DISCONNECTING:
                        Log.d(TAG, "正在断开...");
                        if (bleConnectListener != null) {
                            bleConnectListener.onDisConnecting(gatt);
                        }
                        break;
                    case BluetoothGatt.STATE_DISCONNECTED:
                        switch (status) {
                            case 133:
                                //连接异常,无法连接
                                if (bleConnectListener != null) {
                                    bleConnectListener.onConnectFailure(gatt, "连接异常！", status);
                                    Log.d(TAG, "连接失败status：" + status + " ===> " + bluetoothDevice.getAddress());
                                }
                                break;
                            case 62:
                                //没有发现服务 异常断开
                                if (bleConnectListener != null) {
                                    bleConnectListener.onConnectFailure(gatt, "连接成功服务未发现断开！", status);
                                }
                                break;
                            default:
                                if (bleConnectListener != null) {
                                    bleConnectListener.onDisConnectSuccess(gatt, status);
                                    Log.d(TAG, "断开连接status: " + status);
                                }
                                break;
                        }
                        gatt.close();
                        isConnecting = false;
                        break;
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            isConnecting = false;
            //移除发现服务超时
            weakReferenceHandler.removeCallbacks(serviceDiscoverTimeoutRunnable);
            //配置服务信息
            if (bleConnectListener != null) {
                if (configBleService(gatt, serviceUuid, writeUuid)) {
                    //成功发现服务回调
                    bleConnectListener.onServiceDiscoverySuccess(gatt, status);
                } else {
                    bleConnectListener.onServiceDiscoveryFailed(gatt, "获取服务特征异常");
                }
            }
        }

        //读取蓝牙设备发出来的数据回调
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.d(TAG, "onCharacteristicRead: " + status);
        }

        //向蓝牙设备写入数据结果回调
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            //将收到的字节数组转换成十六进制字符串
            switch (status) {
                case BluetoothGatt.GATT_SUCCESS:
                    //写入成功
                    if (bleConnectListener != null) {
                        bleConnectListener.onWriteSuccess(gatt, characteristic.getValue());
                    }
                    break;
                case BluetoothGatt.GATT_FAILURE:
                    //写入失败
                    if (bleConnectListener != null) {
                        bleConnectListener.onWriteFailed(gatt, "写入失败");
                    }
                    break;
                case BluetoothGatt.GATT_WRITE_NOT_PERMITTED:
                    StringHub.show(weakReferenceContext.get(), "没有写入设备权限");
                    break;
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            //接收数据
            Log.d(TAG, "收到数据:" + Arrays.toString(characteristic.getValue()));
            if (bleConnectListener != null) {
                bleConnectListener.onReceiveMessage(gatt, characteristic);  //接收数据回调
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            //开启监听成功，可以从设备读数据了
            Log.d(TAG, "onDescriptorRead开启监听成功");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            //开启监听成功，可以向设备写入命令了
            Log.d(TAG, "onDescriptorWrite开启监听成功");
        }

        //蓝牙信号强度
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            switch (status) {
                case BluetoothGatt.GATT_SUCCESS:
                    Log.w(TAG, "读取RSSI值成功，RSSI值: " + rssi);
                    if (bleConnectListener != null) {
                        bleConnectListener.onReadRssi(gatt, rssi, status);
                    }
                    break;
                case BluetoothGatt.GATT_FAILURE:
                    Log.d(TAG, "读取RSSI值失败，status: " + status);
                    break;
            }
        }
    };

    @SuppressLint("MissingPermission")
    public final Runnable connectTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            if (bluetoothGatt == null) {
                Log.d(TAG, "connectOutTimeRunnable ===> bluetoothGatt == null");
                return;
            }
            isConnecting = false;
            if (checkConnectPermission()) {
                bluetoothGatt.disconnect();
                //连接超时当作连接失败回调
                if (bleConnectListener != null) {
                    bleConnectListener.onConnectFailure(bluetoothGatt, "连接超时", -1);
                }
            }
        }
    };

    @SuppressLint("MissingPermission")
    public final Runnable serviceDiscoverTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            if (bluetoothGatt == null) {
                Log.d(TAG, "serviceDiscoverTimeoutRunnable ===> bluetoothGatt == null");
                return;
            }
            isConnecting = false;
            if (checkConnectPermission()) {
                bluetoothGatt.disconnect();
                //发现服务超时当作连接失败回调
                if (bleConnectListener != null) {
                    bleConnectListener.onConnectFailure(bluetoothGatt, "发现服务超时！", -1);
                }
            }
        }
    };

    public boolean configBleService(BluetoothGatt bluetoothGatt, UUID serviceUuid, UUID writeUuid) {
        BluetoothGattCharacteristic notifyCharacteristic = null;
        for (BluetoothGattService service : bluetoothGatt.getServices()) {
            UUID uuid = service.getUuid();
            Log.d(TAG, "configBleService: " + uuid.toString());
            if (uuid == serviceUuid) {
                for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                    int charaProp = characteristic.getProperties();

                    if (characteristic.getUuid() == writeUuid) {
                        //写特征
                        writeCharacteristic = characteristic;
                    }
                    if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                        notifyCharacteristic = bluetoothGatt
                                .getService(uuid)
                                .getCharacteristic(characteristic.getUuid());
                    }
                }
            } else {
                Log.d(TAG, "configBleService: 未匹配到uuid");
            }
        }
        //打开读通知，打开的是notifyCharacteristic，不然不走onCharacteristicChanged回调
        if (checkConnectPermission()) {
            bluetoothGatt.setCharacteristicNotification(notifyCharacteristic, true);
            //一定要重新设置
            for (BluetoothGattDescriptor descriptor : notifyCharacteristic.getDescriptors()) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                bluetoothGatt.writeDescriptor(descriptor);
            }
            //延迟2s，保证所有通知都能及时打开
            weakReferenceHandler.postDelayed(() -> {
            }, 2000);
        }
        return false;
    }

    public void sendCommand(byte[] cmd) {
        if (writeCharacteristic == null) {
            Log.d(TAG, "sendCommand() ===> writeGattCharacteristic == null");
            return;
        }

        if (bluetoothGatt == null) {
            Log.d(TAG, "sendCommand() ===> bluetoothGatt == null");
            return;
        }

        if (checkConnectPermission()) {
            Object value;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                value = bluetoothGatt.writeCharacteristic(writeCharacteristic, cmd, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            } else {
                value = writeCharacteristic.setValue(cmd);
                bluetoothGatt.writeCharacteristic(writeCharacteristic);
            }
            Log.d(TAG, "写特征设置值结果：" + value);
        }
    }

    public void disConnectDevice() {
        if (bluetoothGatt == null) {
            Log.d(TAG, "disConnectDevice() ===> bluetoothGatt == null");
            return;
        }
        if (checkConnectPermission()) {
            bluetoothGatt.disconnect();
            isConnecting = false;
        }
    }

    private boolean checkConnectPermission() {
        if (ActivityCompat.checkSelfPermission(weakReferenceContext.get(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
        ) {
            StringHub.show(weakReferenceContext.get(), "缺少蓝牙连接权限");
            return false;
        } else {
            return true;
        }
    }

    private boolean checkScanPermission() {
        if (ActivityCompat.checkSelfPermission(weakReferenceContext.get(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
        ) {
            StringHub.show(weakReferenceContext.get(), "缺少蓝牙扫描权限");
            return false;
        } else {
            return true;
        }
    }
}
