package com.pengxh.androidx.lib.view;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.pengxh.androidx.lib.databinding.ActivityMainBinding;
import com.pengxh.androidx.lib.vm.NetworkViewModel;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.utils.LoadState;
import com.pengxh.androidx.lite.utils.LoadingDialogHub;

public class MainActivity extends AndroidxBaseActivity<ActivityMainBinding> {

    private static final String TAG = "MainActivity";
    private NetworkViewModel viewModel;

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    protected void initOnCreate(@Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(NetworkViewModel.class);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void observeRequestState() {
        viewModel.loadState.observe(this, new Observer<LoadState>() {
            @Override
            public void onChanged(LoadState loadState) {
                if (loadState == LoadState.Loading) {
                    LoadingDialogHub.show(MainActivity.this, "数据加载中，请稍后");
                } else {
                    LoadingDialogHub.dismiss();
                }
            }
        });
    }
}