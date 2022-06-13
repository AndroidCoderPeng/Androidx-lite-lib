package com.pengxh.androidx.lib;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.pengxh.androidx.lib.databinding.ActivityMainBinding;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.utils.BroadcastManager;
import com.pengxh.androidx.lite.utils.Constant;
import com.pengxh.androidx.lite.utils.WeakReferenceHandler;
import com.pengxh.androidx.lite.utils.ble.BLEManager;

import java.util.Objects;

public class MainActivity extends AndroidxBaseActivity<ActivityMainBinding> {

    private static final String TAG = "MainActivity";
    private final Context context = MainActivity.this;
    private BLEManager bleManager;
    private WeakReferenceHandler weakReferenceHandler;

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    protected void initData() {
        BroadcastManager.getInstance(this).addAction(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(Objects.requireNonNull(intent.getAction()))) {
                    switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)) {
                        case BluetoothAdapter.STATE_ON:
                            weakReferenceHandler.sendEmptyMessage(Constant.BLUETOOTH_ON);
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            weakReferenceHandler.sendEmptyMessage(Constant.BLUETOOTH_OFF);
                            break;
                    }
                }
            }
        }, Constant.BLUETOOTH_STATE_CHANGED);
        bleManager = BLEManager.getInstance();
        weakReferenceHandler = new WeakReferenceHandler(callback);
        if (bleManager.initBLE(this)) {
            if (bleManager.isBluetoothEnable()) {
                Log.d(TAG, "initData: 蓝牙状态: ON");
            } else {
                Log.d(TAG, "initData: 蓝牙状态: OFF");
                bleManager.openBluetooth(true);
            }
        } else {
            Log.d(TAG, "initData: 该设备不支持低功耗蓝牙");
        }
    }

    @Override
    protected void initEvent() {
        viewBinding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bleManager.isDiscovery()) {
                    bleManager.stopDiscoverDevice();
                }
            }
        });
    }

    private final Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case Constant.BLUETOOTH_ON:
                    Log.d(TAG, "handleMessage: 蓝牙状态: ON");
                    break;
                case Constant.BLUETOOTH_OFF:
                    Log.d(TAG, "handleMessage: 蓝牙状态: OFF");
                    break;
            }
            return true;
        }
    };
}