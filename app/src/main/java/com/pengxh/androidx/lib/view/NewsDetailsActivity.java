package com.pengxh.androidx.lib.view;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.pengxh.androidx.lib.databinding.ActivityNewsDetailsBinding;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.utils.Constant;
import com.pengxh.androidx.lite.utils.HtmlRenderEngine;

import java.util.ArrayList;

public class NewsDetailsActivity extends AndroidxBaseActivity<ActivityNewsDetailsBinding> {
    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    protected void initOnCreate(@Nullable Bundle savedInstanceState) {
        ArrayList<String> params = getIntent().getStringArrayListExtra(Constant.INTENT_PARAM_KEY);

        binding.newsTitle.setText(params.get(0));
        binding.newsSrc.setText(params.get(1));
        binding.newsTime.setText(params.get(2));

        new HtmlRenderEngine.Builder()
                .setContext(this)
                .setTargetView(binding.newsContent)
                .setHtmlContent(params.get(3))
                .setOnGetImageSourceListener(new HtmlRenderEngine.OnGetImageSourceListener() {
                    @Override
                    public void imageSource(String url) {

                    }
                }).build().load();
    }

    @Override
    protected void observeRequestState() {

    }

    @Override
    protected void initEvent() {

    }
}
