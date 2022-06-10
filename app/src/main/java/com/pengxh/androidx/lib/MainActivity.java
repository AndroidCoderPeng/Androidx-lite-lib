package com.pengxh.androidx.lib;

import android.os.Environment;
import android.view.View;

import com.pengxh.androidx.lib.databinding.ActivityMainBinding;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;

import java.io.File;

public class MainActivity extends AndroidxBaseActivity<ActivityMainBinding> {

    private static final String TAG = "MainActivity";

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    protected void initData() {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "");
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