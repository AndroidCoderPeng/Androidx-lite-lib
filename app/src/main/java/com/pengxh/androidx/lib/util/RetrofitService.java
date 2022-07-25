package com.pengxh.androidx.lib.util;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface RetrofitService {
    /**
     * https://way.jd.com/jisuapi/get?channel=头条&num=10&start=0&appkey=e957ed7ad90436a57e604127d9d8fa32
     */
    @GET("/jisuapi/get")
    Observable<ResponseBody> obtainImageList(
            @Query("appkey") String appkey,
            @Query("channel") String channel,
            @Query("num") int num,
            @Query("start") int start
    );
}
