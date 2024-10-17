package com.pengxh.androidx.lib.view;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.pengxh.androidx.lib.databinding.ActivityMainBinding;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.hub.StringHub;
import com.pengxh.androidx.lite.widget.TitleBarView;

public class MainActivity extends AndroidxBaseActivity<ActivityMainBinding> {

    private static final String TAG = "MainActivity";

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    protected void initOnCreate(@Nullable Bundle savedInstanceState) {
        binding.airDashBoardView.setCenterText("优").setCurrentValue(255);
    }

    @Override
    protected void initEvent() {
        binding.titleView.setOnClickListener(new TitleBarView.OnClickListener() {
            @Override
            public void onLeftClick() {
                StringHub.show(MainActivity.this, "onLeftClick");
            }

            @Override
            public void onRightClick() {
                StringHub.show(MainActivity.this, "onRightClick");
            }
        });
    }

    @Override
    protected void observeRequestState() {

    }
}