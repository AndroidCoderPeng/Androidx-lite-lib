package com.pengxh.androidx.lib.vm;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pengxh.androidx.lib.model.ImageListModel;
import com.pengxh.androidx.lib.model.NewsDataModel;
import com.pengxh.androidx.lib.util.RetrofitServiceManager;
import com.pengxh.androidx.lib.util.StringHelper;
import com.pengxh.androidx.lite.callback.OnObserverCallback;
import com.pengxh.androidx.lite.vm.BaseViewModel;
import com.pengxh.androidx.lite.vm.ObserverSubscriber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Observable;

public class NetworkViewModel extends BaseViewModel {
    private final Gson gson = new Gson();
    public MutableLiveData<ImageListModel> imageResultModel = new MutableLiveData<>();

    public void obtainImageList(String channel, int start) {
        Observable<ResponseBody> dataObservable = RetrofitServiceManager.obtainImageList(channel, start);
        ObserverSubscriber.addSubscribe(dataObservable, new OnObserverCallback() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String response = responseBody.string();
                    int responseCode = StringHelper.separateResponseCode(response);
                    if (responseCode == 10000) {
                        NewsDataModel resultModel = gson.fromJson(response, new TypeToken<NewsDataModel>() {
                        }.getType());
                        ImageListModel listModel = new ImageListModel();
                        List<String> images = new ArrayList<>();
                        for (int i = 0; i < 9; i++) {
                            images.add(resultModel.getResult().getResult().getList().get(i).getPic());
                        }
                        listModel.setImages(images);
                        imageResultModel.setValue(listModel);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
