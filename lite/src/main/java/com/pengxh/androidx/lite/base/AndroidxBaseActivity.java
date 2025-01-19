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

    /**
     * binding 是在 onCreate 方法中初始化的，并且它绑定的是 Activity 的生命周期。当 Activity 销毁时，binding 也会随之被销毁。
     * 在大多数情况下，ViewBinding 的生命周期与 Activity 或 Fragment 的生命周期一致，因此不需要手动释放。
     * */
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
                throw new IllegalStateException("Binding inflated to null");
            }
            setContentView(binding.getRoot());
            setupTopBarLayout();
            initOnCreate(savedInstanceState);
            observeRequestState();
            initEvent();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to find inflate method", e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to invoke inflate method", e);
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
}
