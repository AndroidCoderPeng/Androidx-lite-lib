package com.pengxh.androidx.lite.vm;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public abstract class BaseViewModel extends ViewModel {
    public MutableLiveData<LoadState> loadState = new MutableLiveData<>();
}
