package com.pengxh.androidx.lite.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.pengxh.androidx.lite.utils.BroadcastManager;
import com.pengxh.androidx.lite.utils.ContextUtil;
import com.pengxh.androidx.lite.utils.PageNavigationManager;
import com.pengxh.androidx.lite.widget.dialog.NoNetworkDialog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class AndroidxBaseActivity<VB extends ViewBinding> extends AppCompatActivity {

    protected VB viewBinding;
    private BroadcastManager broadcastManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Type type = getClass().getGenericSuperclass();
        if (type == null) {
            throw new NullPointerException();
        }
        Class<?> cls = (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
        try {
            Method method = cls.getDeclaredMethod("inflate", LayoutInflater.class);
            viewBinding = (VB) method.invoke(null, getLayoutInflater());
            if (viewBinding == null) {
                throw new NullPointerException();
            }
            setContentView(viewBinding.getRoot());
            setupTopBarLayout();
            initData();
            initEvent();
            PageNavigationManager.addActivity(this);
            broadcastManager = BroadcastManager.getInstance(this);
            broadcastManager.addAction(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (ContextUtil.isNetworkConnected(context)) {
                        try {
                            new NoNetworkDialog.Builder()
                                    .setContext(context)
                                    .setOnDialogButtonClickListener(new NoNetworkDialog.OnDialogButtonClickListener() {
                                        @Override
                                        public void onButtonClick() {
                                            startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
                                        }
                                    }).build().show();
                        } catch (WindowManager.BadTokenException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, ConnectivityManager.CONNECTIVITY_ACTION);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 定制沉浸式状态栏
     */
    protected abstract void setupTopBarLayout();

    /**
     * 初始化默认数据
     */
    protected abstract void initData();

    /**
     * 初始化业务逻辑
     */
    protected abstract void initEvent();

    @Override
    protected void onDestroy() {
        viewBinding = null;
        broadcastManager.destroy(ConnectivityManager.CONNECTIVITY_ACTION);
        super.onDestroy();
    }
}
