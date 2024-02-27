package com.pengxh.androidx.lite.base;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pengxh.androidx.lite.utils.LoadState;

public abstract class BaseViewModel extends ViewModel {
    public MutableLiveData<LoadState> loadState = new MutableLiveData<>();
}
