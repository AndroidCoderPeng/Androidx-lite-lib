package com.pengxh.androidx.lib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.pengxh.androidx.lib.databinding.ActivityMainBinding;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.utils.BroadcastManager;
import com.pengxh.androidx.lite.utils.SaveKeyValues;
import com.pengxh.androidx.lite.utils.StringUtil;
import com.pengxh.androidx.lite.utils.TimeOrDateUtil;

public class MainActivity extends AndroidxBaseActivity<ActivityMainBinding> {

    private static final String TAG = "MainActivity";
    private static final String MainActivityAction = "MAIN_ACTIVITY_ACTION";
    private final Context context = MainActivity.this;
    private BroadcastManager broadcastManager;

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    protected void initData() {
        SaveKeyValues.initSharedPreferences(this);
        broadcastManager = BroadcastManager.getInstance(this);
        broadcastManager.addAction(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        }, MainActivityAction);
        Log.d(TAG, "initData: " + TimeOrDateUtil.isInCurrentMonth((System.currentTimeMillis()-10000000000L)));
    }

    @Override
    protected void initEvent() {
        viewBinding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                broadcastManager.sendBroadcast(MainActivityAction, "");

//                viewBinding.textView.setText();
            }
        });
    }

    @Override
    protected void onDestroy() {
        broadcastManager.destroy(MainActivityAction);
        super.onDestroy();
    }
}