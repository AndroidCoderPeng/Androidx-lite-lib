package com.pengxh.androidx.lite.callback;

import okhttp3.Response;

public interface OnHttpRequestListener {
    void onSuccess(Response response);

    void onFailure(Throwable throwable);
}
