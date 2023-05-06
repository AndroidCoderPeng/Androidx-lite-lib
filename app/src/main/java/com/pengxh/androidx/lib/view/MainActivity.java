package com.pengxh.androidx.lib.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;
import com.pengxh.androidx.lib.R;
import com.pengxh.androidx.lib.databinding.ActivityMainBinding;
import com.pengxh.androidx.lib.model.NewsDataModel;
import com.pengxh.androidx.lib.vm.NetworkViewModel;
import com.pengxh.androidx.lite.adapter.MultipleChoiceAdapter;
import com.pengxh.androidx.lite.adapter.ViewHolder;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.divider.VerticalMarginItemDecoration;
import com.pengxh.androidx.lite.hub.IntHub;
import com.pengxh.androidx.lite.utils.ImmerseStatusBarManager;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AndroidxBaseActivity<ActivityMainBinding> {

    private static final String TAG = "MainActivity";
    private final Context context = MainActivity.this;
    private final List<String> images = Arrays.asList(
            "https://images.pexels.com/photos/3052361/pexels-photo-3052361.jpeg",
            "https://images.pexels.com/photos/3565742/pexels-photo-3565742.jpeg",
            "https://images.pexels.com/photos/2931915/pexels-photo-2931915.jpeg",
            "https://images.pexels.com/photos/6592658/pexels-photo-6592658.jpeg",
            "https://images.pexels.com/photos/5611139/pexels-photo-5611139.jpeg",
            "https://images.pexels.com/photos/2931915/pexels-photo-2931915.jpeg",
            "https://images.pexels.com/photos/6592658/pexels-photo-6592658.jpeg",
            "https://images.pexels.com/photos/5611139/pexels-photo-5611139.jpeg",
            "https://images.pexels.com/photos/6612350/pexels-photo-6612350.jpeg"
    );

    @Override
    protected void setupTopBarLayout() {
        ImmerseStatusBarManager.setColor(this, IntHub.convertColor(this, R.color.white));
        ImmersionBar.with(this).statusBarDarkFont(true).init();
    }

    private MultipleChoiceAdapter<NewsDataModel.X.ResultModel.ListModel> multipleChoiceAdapter;

    @Override
    protected void initData() {
        NetworkViewModel viewModel = new ViewModelProvider(this).get(NetworkViewModel.class);
        viewModel.getImageList("头条", 0);
        viewModel.newsResultModel.observe(this, new Observer<NewsDataModel>() {
            @Override
            public void onChanged(NewsDataModel newsDataModel) {
                multipleChoiceAdapter = new MultipleChoiceAdapter<NewsDataModel.X.ResultModel.ListModel>(R.layout.item_select_sample_lv, newsDataModel.getResult().getResult().getList()) {

                    @Override
                    public void convertView(ViewHolder viewHolder, int position, NewsDataModel.X.ResultModel.ListModel item) {
                        String img = item.getPic();
                        if (Objects.equals(img, "") || img.endsWith(".gif")) {
                            viewHolder.setVisibility(R.id.newsPicture, View.GONE);
                        } else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Drawable drawable = Glide.with(context).load(img).submit().get();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                viewHolder.setImageResource(R.id.newsPicture, drawable);
                                            }
                                        });
                                    } catch (ExecutionException | InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }

                        viewHolder.setText(R.id.newsTitle, item.getTitle())
                                .setText(R.id.newsSrc, item.getSrc())
                                .setText(R.id.newsTime, item.getTime());
                    }
                };
                viewBinding.recyclerView.addItemDecoration(new VerticalMarginItemDecoration(0, 1));
                DefaultItemAnimator itemAnimator = (DefaultItemAnimator) viewBinding.recyclerView.getItemAnimator();
                itemAnimator.setSupportsChangeAnimations(false);
                viewBinding.recyclerView.setAdapter(multipleChoiceAdapter);
                multipleChoiceAdapter.setOnItemCheckedListener(new MultipleChoiceAdapter.OnItemCheckedListener<NewsDataModel.X.ResultModel.ListModel>() {
                    @Override
                    public void onItemChecked(List<NewsDataModel.X.ResultModel.ListModel> items) {

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

    }
}