package com.pengxh.androidx.lib.vm;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pengxh.androidx.lib.model.NewsDataModel;
import com.pengxh.androidx.lib.util.RetrofitServiceManager;
import com.pengxh.androidx.lib.util.StringHelper;
import com.pengxh.androidx.lite.callback.OnObserverCallback;
import com.pengxh.androidx.lite.vm.BaseViewModel;
import com.pengxh.androidx.lite.vm.LoadState;
import com.pengxh.androidx.lite.vm.ObserverSubscriber;

import java.io.IOException;

import okhttp3.ResponseBody;
import rx.Observable;

public class NetworkViewModel extends BaseViewModel {
    private final Gson gson = new Gson();
    public MutableLiveData<NewsDataModel> newsResultModel = new MutableLiveData<>();

    public void getImageList(String channel, int start) {
        loadState.setValue(LoadState.Loading);
        Observable<ResponseBody> dataObservable = RetrofitServiceManager.getImageList(channel, start);
        ObserverSubscriber.addSubscribe(dataObservable, new OnObserverCallback() {
            @Override
            public void onCompleted() {
                loadState.setValue(LoadState.Success);
            }

            @Override
            public void onError(Throwable e) {
                loadState.setValue(LoadState.Fail);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String response = responseBody.string();
                    int responseCode = StringHelper.separateResponseCode(response);
                    if (responseCode == 10000) {
                        NewsDataModel resultModel = gson.fromJson(response, new TypeToken<NewsDataModel>() {
                        }.getType());
                        newsResultModel.setValue(resultModel);
                    } else {
                        loadState.setValue(LoadState.Fail);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    loadState.setValue(LoadState.Fail);
                }
            }
        });
    }
}
