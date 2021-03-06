package com.pengxh.androidx.lite.callback;

import okhttp3.ResponseBody;

public interface OnObserverCallback {
    void onCompleted();

    void onError(Throwable e);

    void onNext(ResponseBody responseBody);
}
