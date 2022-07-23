package com.pengxh.androidx.lite.vm;

import com.pengxh.androidx.lite.callback.OnObserverCallback;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 同比Kotlin的ViewModel扩展函数
 */
public class ObserverSubscriber {
    public static void addSubscribe(Observable<ResponseBody> observable, OnObserverCallback observerCallback) {
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseBody>() {
            @Override
            public void onCompleted() {
                observerCallback.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                observerCallback.onError(e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                observerCallback.onNext(responseBody);
            }
        });
    }
}
