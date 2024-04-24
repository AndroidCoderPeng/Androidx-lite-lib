package com.pengxh.androidx.lib.util;

import com.pengxh.androidx.lite.utils.RetrofitFactory;

import okhttp3.ResponseBody;
import rx.Observable;

public class RetrofitServiceManager {

    private static final String TAG = "RetrofitServiceManager";

    private static final RetrofitService api = RetrofitFactory.createRetrofit(
            "https://way.jd.com", RetrofitService.class, true
    );

    public static Observable<ResponseBody> getImageList(String channel, int start) {
        return api.getImageList("e957ed7ad90436a57e604127d9d8fa32", channel, 15, start);
    }
}
