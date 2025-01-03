package com.pengxh.androidx.lite.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {
    private static final String TAG = "RetrofitFactory";

    public static <T> T createRetrofit(String httpConfig, Class<T> clazz, boolean debug) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(LiteConstant.HTTP_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(LiteConstant.HTTP_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(LiteConstant.HTTP_TIMEOUT, TimeUnit.SECONDS);
        OkHttpClient httpClient;
        if (debug) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(@NonNull String s) {
                    Log.d(TAG, ">>>>> " + s);
                }
            });
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient = builder.addInterceptor(interceptor).build();
        } else {
            httpClient = builder.build();
        }
        return new Retrofit.Builder()
                .baseUrl(httpConfig)
                .addConverterFactory(GsonConverterFactory.create())//Gson转换器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(httpClient) //log拦截器
                .build()
                .create(clazz);
    }
}
