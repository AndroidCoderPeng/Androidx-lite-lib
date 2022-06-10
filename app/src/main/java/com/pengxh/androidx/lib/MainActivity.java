package com.pengxh.androidx.lib;

import com.pengxh.androidx.lib.databinding.ActivityMainBinding;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.utils.ColorUtil;

public class MainActivity extends AndroidxBaseActivity<ActivityMainBinding> {

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    protected void initData() {
        viewBinding.layout.setBackgroundColor(ColorUtil.randomColor());
    }

    @Override
    protected void initEvent() {

    }
}