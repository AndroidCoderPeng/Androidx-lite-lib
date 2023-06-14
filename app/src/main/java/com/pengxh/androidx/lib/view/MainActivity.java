package com.pengxh.androidx.lib.view;

import android.content.Context;
import android.os.Bundle;
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
import com.pengxh.androidx.lite.utils.ImmerseStatusBarManager;
import com.pengxh.androidx.lite.vm.LoadState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    protected void initData(@Nullable Bundle savedInstanceState) {
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

                        viewHolder.setText(R.id.newsTitle, item.getTitle())
                                .setText(R.id.newsSrc, item.getSrc())
                                .setText(R.id.newsTime, item.getTime());
                    }
                };
                viewBinding.recyclerView.setAdapter(singleChoiceAdapter);
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
//        EditableImageAdapter imageAdapter = new EditableImageAdapter(this, 9, 2f);
//        viewBinding.imageGridView.setAdapter(imageAdapter);
//        imageAdapter.setOnItemClickListener(new EditableImageAdapter.OnItemClickListener() {
//            @Override
//            public void onAddImageClick() {
//                PictureSelector.create(MainActivity.this)
//                        .openGallery(SelectMimeType.ofImage())
//                        .isGif(false)
//                        .isMaxSelectEnabledMask(true)
//                        .setFilterMinFileSize(100)
//                        .setMaxSelectNum(9)
//                        .isDisplayCamera(false)
//                        .setImageEngine(GlideLoadEngine.get())
//                        .forResult(new OnResultCallbackListener<LocalMedia>() {
//                            @Override
//                            public void onResult(ArrayList<LocalMedia> result) {
//                                for (LocalMedia media : result) {
//                                    recyclerViewImages.add(media.getRealPath());
//                                }
//                                imageAdapter.setupImage(recyclerViewImages);
//                            }
//
//                            @Override
//                            public void onCancel() {
//
//                            }
//                        });
//            }
//
//            @Override
//            public void onItemClick(int position) {
//
//            }
//
//            @Override
//            public void onItemLongClick(View view, int position) {
//                imageAdapter.deleteImage(position);
//            }
//        });
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