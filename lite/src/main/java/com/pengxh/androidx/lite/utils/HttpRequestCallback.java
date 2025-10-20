package com.pengxh.androidx.lite.utils;

import com.pengxh.androidx.lite.enums.HttpRequestState;

public interface HttpRequestCallback {
    void onStateChanged(HttpRequestState state);
}
