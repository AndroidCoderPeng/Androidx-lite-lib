package com.pengxh.androidx.lite.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class AndroidxBaseFragment<VB extends ViewBinding> extends Fragment {

    private VB _binding;

    public VB getBinding() {
        return _binding;
    }

    public void setBinding(VB _binding) {
        this._binding = _binding;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Type type = getClass().getGenericSuperclass();
        if (type == null) {
            throw new NullPointerException();
        }
        Class<?> cls = (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
        try {
            Method method = cls.getDeclaredMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
            _binding = (VB) method.invoke(null, getLayoutInflater(), container, false);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        if (_binding == null) {
            throw new NullPointerException();
        }
        return _binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initOnCreate(savedInstanceState);
        setupTopBarLayout();
        observeRequestState();
        initEvent();
    }

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
    public void onDestroyView() {
        setBinding(null);
        super.onDestroyView();
    }
}
