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
import com.pengxh.androidx.lite.adapter.SingleChoiceAdapter;
import com.pengxh.androidx.lite.adapter.ViewHolder;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.divider.VerticalMarginItemDecoration;
import com.pengxh.androidx.lite.hub.ContextHub;
import com.pengxh.androidx.lite.hub.IntHub;
import com.pengxh.androidx.lite.hub.StringHub;
import com.pengxh.androidx.lite.utils.ImmerseStatusBarManager;
import com.pengxh.androidx.lite.vm.LoadState;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AndroidxBaseActivity<ActivityMainBinding> {

    private static final String TAG = "MainActivity";
    private final Context context = MainActivity.this;

    @Override
    protected void setupTopBarLayout() {
        ImmerseStatusBarManager.setColor(this, IntHub.convertColor(this, R.color.white));
        ImmersionBar.with(this).statusBarDarkFont(true).init();
    }

    private NetworkViewModel viewModel;
    private SingleChoiceAdapter<NewsDataModel.X.ResultModel.ListModel> singleChoiceAdapter;

    @Override
    protected void initData() {
        viewModel = new ViewModelProvider(this).get(NetworkViewModel.class);
        viewModel.getImageList("头条", 0);
        viewModel.newsResultModel.observe(this, new Observer<NewsDataModel>() {
            @Override
            public void onChanged(NewsDataModel newsDataModel) {
                singleChoiceAdapter = new SingleChoiceAdapter<NewsDataModel.X.ResultModel.ListModel>(R.layout.item_select_sample_lv, newsDataModel.getResult().getResult().getList()) {
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
                viewBinding.recyclerView.addItemDecoration(new VerticalMarginItemDecoration(0, 10));
                DefaultItemAnimator itemAnimator = (DefaultItemAnimator) viewBinding.recyclerView.getItemAnimator();
                itemAnimator.setSupportsChangeAnimations(false);
                viewBinding.recyclerView.setAdapter(singleChoiceAdapter);
                singleChoiceAdapter.setOnItemCheckedListener(new SingleChoiceAdapter.OnItemCheckedListener<NewsDataModel.X.ResultModel.ListModel>() {
                    @Override
                    public void onItemChecked(int position, NewsDataModel.X.ResultModel.ListModel listModel) {
                        ArrayList<String> params = new ArrayList<>();
                        params.add(listModel.getTitle());
                        params.add(listModel.getSrc());
                        params.add(listModel.getTime());
                        params.add(listModel.getContent());
                        ContextHub.navigatePageTo(context, NewsDetailsActivity.class, params);
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
                    StringHub.show(context, "数据加载中，请稍后");
                }
            }
        });
    }
}