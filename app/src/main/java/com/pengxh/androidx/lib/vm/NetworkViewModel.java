package com.pengxh.androidx.lib.vm;

import android.util.Pair;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pengxh.androidx.lib.model.NewsDataModel;
import com.pengxh.androidx.lib.util.RetrofitServiceManager;
import com.pengxh.androidx.lib.util.StringHelper;
import com.pengxh.androidx.lite.utils.HttpResponseState;
import com.pengxh.androidx.lite.utils.ObserverSubscriber;

import java.io.IOException;

import okhttp3.ResponseBody;
import rx.Observable;

public class NetworkViewModel extends ViewModel {
    private final Gson gson = new Gson();
    public MutableLiveData<HttpResponseState<NewsDataModel>> newsListData = new MutableLiveData<>();

    public void getNewsByPage(String channel, int start) {
        newsListData.setValue(new HttpResponseState.Loading<>());
        Observable<ResponseBody> dataObservable = RetrofitServiceManager.getImageList(channel, start);
        ObserverSubscriber.addSubscribe(dataObservable, new ObserverSubscriber.OnObserverCallback() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                newsListData.setValue(new HttpResponseState.Error<>(500, e.getMessage(), e));
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String response = responseBody.string();
                    Pair<Integer, String> pair = StringHelper.getResponseHeader(response);
                    if (pair.first == 10000) {
                        NewsDataModel result = gson.fromJson(response, new TypeToken<NewsDataModel>() {
                        }.getType());
                        newsListData.setValue(new HttpResponseState.Success<>(result));
                    } else {
                        newsListData.setValue(new HttpResponseState.Error<>(pair.first, pair.second, null));
                    }
                } catch (IOException e) {
                    newsListData.setValue(new HttpResponseState.Error<>(500, e.getMessage(), e));
                }
            }
        });
    }
}
