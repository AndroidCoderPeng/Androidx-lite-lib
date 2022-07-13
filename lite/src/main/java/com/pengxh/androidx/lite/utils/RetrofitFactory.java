package com.pengxh.androidx.lite.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitFactory {
    private static final String TAG = "RetrofitFactory";

    public static <T> T createRetrofit(String httpConfig, Class<T> tClass) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NonNull String s) {
                Log.d(TAG, ">>>>> " + s);
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS);
        OkHttpClient httpClient = builder.addInterceptor(interceptor).build();
        return new Retrofit.Builder()
                .baseUrl(httpConfig)
                .addConverterFactory(ScalarsConverterFactory.create())          //字符串转换器
                .addConverterFactory(GsonConverterFactory.create())             //Gson转换器
                .addCallAdapterFactory(CoroutineCallAdapterFactory.create())    //协程请求适配器
                .client(httpClient) //log拦截器
                .build().create(tClass);
    }
}
