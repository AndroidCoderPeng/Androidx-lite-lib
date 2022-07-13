package com.pengxh.androidx.lite.utils;

import android.util.Log;

import com.pengxh.androidx.lite.callback.OnHttpRequestListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HttpRequestUtil {
    private static final String TAG = "HttpRequestHelper";

    public static void doRequest(Request request, OnHttpRequestListener listener) {
        Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(@NotNull String message) {
                        Log.d(TAG, "log ===> " + message);
                    }
                });
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                OkHttpClient client = new OkHttpClient.Builder()
                        .addInterceptor(interceptor)
                        .readTimeout(Constant.HTTP_TIMEOUT, TimeUnit.SECONDS)
                        .connectTimeout(Constant.HTTP_TIMEOUT, TimeUnit.SECONDS)
                        .writeTimeout(Constant.HTTP_TIMEOUT, TimeUnit.SECONDS)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    subscriber.onNext(response);
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                listener.onFailure(e);
            }

            @Override
            public void onNext(Response response) {
                listener.onSuccess(response);
            }
        });
    }
}
