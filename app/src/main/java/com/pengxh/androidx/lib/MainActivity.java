package com.pengxh.androidx.lib;

import android.content.Context;
import android.view.View;

import com.pengxh.androidx.lib.databinding.ActivityMainBinding;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;

public class MainActivity extends AndroidxBaseActivity<ActivityMainBinding> {

    private static final String TAG = "MainActivity";
    private final Context context = MainActivity.this;

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {
        viewBinding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}