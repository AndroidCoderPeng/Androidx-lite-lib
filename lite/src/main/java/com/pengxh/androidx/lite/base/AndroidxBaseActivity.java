package com.pengxh.androidx.lite.base;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class AndroidxBaseActivity<VB extends ViewBinding> extends AppCompatActivity {

    protected VB binding;

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
            binding = (VB) method.invoke(null, getLayoutInflater());
            if (binding == null) {
                throw new NullPointerException();
            }
            setContentView(binding.getRoot());
            setupTopBarLayout();
            initOnCreate(savedInstanceState);
            observeRequestState();
            initEvent();
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
    protected abstract void initOnCreate(@Nullable Bundle savedInstanceState);

    /**
     * 数据请求状态监听
     */
    protected abstract void observeRequestState();

    /**
     * 初始化业务逻辑
     */
    protected abstract void initEvent();

    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();
    }
}
