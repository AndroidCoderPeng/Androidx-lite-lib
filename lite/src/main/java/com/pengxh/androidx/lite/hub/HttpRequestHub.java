package com.pengxh.androidx.lite.hub;

import android.text.TextUtils;
import android.util.Log;

import com.pengxh.androidx.lite.utils.Constant;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

public class HttpRequestHub {
    private static final String TAG = "HttpRequestHub";
    private final String url;
    private final OnHttpRequestListener listener;

    public static class Builder {
        private String url;
        private OnHttpRequestListener listener;

        /**
         * 设置网络请求接口地址
         */
        public Builder setRequestTarget(String url) {
            this.url = url;
            return this;
        }

        /**
         * 设置网络请求回调监听
         */
        public Builder setOnHttpRequestListener(OnHttpRequestListener httpRequestListener) {
            this.listener = httpRequestListener;
            return this;
        }

        public HttpRequestHub build() {
            return new HttpRequestHub(this);
        }
    }

    private HttpRequestHub(Builder builder) {
        this.url = builder.url;
        this.listener = builder.listener;
    }

    /**
     * 发起网络请求
     */
    public void start() {
        if (TextUtils.isEmpty(url)) {
            listener.onFailure(new IllegalArgumentException("url is empty"));
            return;
        }

        //构建Request
        Request request = new Request.Builder().url(url).get().build();
        Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(@NotNull String message) {
                        Log.d(TAG, ">>>>> " + message);
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
            public void onNext(@Nullable Response response) {
                if (response == null) {
                    listener.onFailure(new NullPointerException());
                } else {
                    try {
                        if (response.body() != null) {
                            listener.onSuccess(response.body().string());
                        } else {
                            listener.onFailure(new NullPointerException());
                        }
                    } catch (IOException e) {
                        listener.onFailure(e);
                    }
                }
            }
        });
    }

    public interface OnHttpRequestListener {
        void onSuccess(String result);

        void onFailure(Throwable throwable);
    }
}
