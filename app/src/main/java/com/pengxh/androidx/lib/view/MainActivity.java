package com.pengxh.androidx.lib.view;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gyf.immersionbar.ImmersionBar;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.pengxh.androidx.lib.R;
import com.pengxh.androidx.lib.databinding.ActivityMainBinding;
import com.pengxh.androidx.lib.util.GlideLoadEngine;
import com.pengxh.androidx.lib.util.LoadingDialogHub;
import com.pengxh.androidx.lib.vm.NetworkViewModel;
import com.pengxh.androidx.lite.adapter.EditableImageAdapter;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.hub.IntHub;
import com.pengxh.androidx.lite.utils.ImmerseStatusBarManager;
import com.pengxh.androidx.lite.vm.LoadState;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AndroidxBaseActivity<ActivityMainBinding> {

    private static final String TAG = "MainActivity";
    private final Context context = MainActivity.this;
    private final List<String> recyclerViewImages = new ArrayList<>();
    private NetworkViewModel viewModel;

    @Override
    protected void setupTopBarLayout() {
        ImmerseStatusBarManager.setColor(this, IntHub.convertColor(this, R.color.white));
        ImmersionBar.with(this).statusBarDarkFont(true).init();
    }

    @Override
    protected void initData() {
        viewModel = new ViewModelProvider(this).get(NetworkViewModel.class);
//        viewModel.getImageList("头条", 0);
//        viewModel.newsResultModel.observe(this, new Observer<NewsDataModel>() {
//            @Override
//            public void onChanged(NewsDataModel newsDataModel) {
//                ArrayList<String> arrayList = new ArrayList<>();
//                for (NewsDataModel.X.ResultModel.ListModel model : newsDataModel.getResult().getResult().getList()) {
//                    arrayList.add(model.getPic());
//                }
//            }
//        });
    }


    @Override
    protected void initEvent() {
        EditableImageAdapter imageAdapter = new EditableImageAdapter(this, 9, 2f);
        viewBinding.imageGridView.setAdapter(imageAdapter);
        imageAdapter.setOnItemClickListener(new EditableImageAdapter.OnItemClickListener() {
            @Override
            public void onAddImageClick() {
                PictureSelector.create(MainActivity.this)
                        .openGallery(SelectMimeType.ofImage())
                        .isGif(false)
                        .isMaxSelectEnabledMask(true)
                        .setFilterMinFileSize(100)
                        .setMaxSelectNum(9)
                        .isDisplayCamera(false)
                        .setImageEngine(GlideLoadEngine.get())
                        .forResult(new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(ArrayList<LocalMedia> result) {
                                for (LocalMedia media : result) {
                                    recyclerViewImages.add(media.getRealPath());
                                }
                                imageAdapter.setupImage(recyclerViewImages);
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
            }

            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {
                imageAdapter.deleteImage(position);
            }
        });
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