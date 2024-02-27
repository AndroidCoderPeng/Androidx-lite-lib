package com.pengxh.androidx.lib.view;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gyf.immersionbar.ImmersionBar;
import com.pengxh.androidx.lib.R;
import com.pengxh.androidx.lib.databinding.ActivityMainBinding;
import com.pengxh.androidx.lib.model.NewsDataModel;
import com.pengxh.androidx.lib.util.LoadingDialogHub;
import com.pengxh.androidx.lib.vm.NetworkViewModel;
import com.pengxh.androidx.lite.adapter.SingleChoiceAdapter;
import com.pengxh.androidx.lite.adapter.ViewHolder;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.hub.IntHub;
import com.pengxh.androidx.lite.utils.FileDownloadManager;
import com.pengxh.androidx.lite.utils.ImmerseStatusBarManager;
import com.pengxh.androidx.lite.utils.LoadState;

import java.io.File;
import java.util.Objects;

public class MainActivity extends AndroidxBaseActivity<ActivityMainBinding> {

    private static final String TAG = "MainActivity";
    private NetworkViewModel viewModel;

    @Override
    protected void setupTopBarLayout() {
        ImmerseStatusBarManager.setColor(this, IntHub.convertColor(this, R.color.white));
        ImmersionBar.with(this).statusBarDarkFont(true).init();
    }

    @Override
    protected void initOnCreate(@Nullable Bundle savedInstanceState) {
        File downloadDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "");
        if (!downloadDir.exists()) {
            downloadDir.mkdir();
        }
        new FileDownloadManager.Builder()
                .setDownloadFileSource("http://111.198.10.15:21709/ems/xls/marker1.xls")
                .setFileSuffix(".xls")
                .setFileSaveDirectory(downloadDir)
                .setOnFileDownloadListener(new FileDownloadManager.OnFileDownloadListener() {
                    @Override
                    public void onProgressChanged(long progress) {
                        Log.d(TAG, "onProgressChanged: " + progress);
                    }

                    @Override
                    public void onDownloadEnd(File file) {
                        Log.d(TAG, "onDownloadEnd: " + file.getAbsolutePath());
                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                }).build().start();


        viewModel = new ViewModelProvider(this).get(NetworkViewModel.class);
        viewModel.getImageList("头条", 0);
        viewModel.newsResultModel.observe(this, new Observer<NewsDataModel>() {
            @Override
            public void onChanged(NewsDataModel newsDataModel) {
                SingleChoiceAdapter<NewsDataModel.X.ResultModel.ListModel> singleChoiceAdapter = new SingleChoiceAdapter<NewsDataModel.X.ResultModel.ListModel>(R.layout.item_select_sample_lv, newsDataModel.getResult().getResult().getList()) {

                    @Override
                    public void convertView(ViewHolder viewHolder, int position, NewsDataModel.X.ResultModel.ListModel item) {
                        String img = item.getPic();
                        if (Objects.equals(img, "") || img.endsWith(".gif")) {
                            viewHolder.setVisibility(R.id.newsPicture, View.GONE);
                        } else {
                            viewHolder.setImageResource(R.id.newsPicture, img);
                        }

                        viewHolder.setText(R.id.newsTitle, item.getTitle()).setText(R.id.newsSrc, item.getSrc()).setText(R.id.newsTime, item.getTime());
                    }
                };
                binding.recyclerView.setAdapter(singleChoiceAdapter);
                singleChoiceAdapter.setOnItemCheckedListener(new SingleChoiceAdapter.OnItemCheckedListener<NewsDataModel.X.ResultModel.ListModel>() {
                    @Override
                    public void onItemChecked(int position, NewsDataModel.X.ResultModel.ListModel listModel) {
                        Log.d(TAG, "onItemChecked: " + listModel.getTitle());
                    }
                });
            }
        });
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void observeRequestState() {
        viewModel.loadState.observe(this, new Observer<LoadState>() {
            @Override
            public void onChanged(LoadState loadState) {
                if (loadState == LoadState.Loading) {
                    LoadingDialogHub.show(MainActivity.this, "数据加载中，请稍后");
                } else {
                    LoadingDialogHub.dismiss();
                }
            }
        });
    }
}