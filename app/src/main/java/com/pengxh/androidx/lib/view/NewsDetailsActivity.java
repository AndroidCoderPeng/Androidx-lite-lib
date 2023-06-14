package com.pengxh.androidx.lib.view;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.pengxh.androidx.lib.databinding.ActivityNewsDetailsBinding;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.hub.ContextHub;
import com.pengxh.androidx.lite.hub.HtmlHub;
import com.pengxh.androidx.lite.utils.Constant;

import java.util.ArrayList;

public class NewsDetailsActivity extends AndroidxBaseActivity<ActivityNewsDetailsBinding> {
    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    protected void initData(@Nullable Bundle savedInstanceState) {
        ArrayList<String> params = getIntent().getStringArrayListExtra(Constant.INTENT_PARAM);

        viewBinding.newsTitle.setText(params.get(0));
        viewBinding.newsSrc.setText(params.get(1));
        viewBinding.newsTime.setText(params.get(2));

        HtmlHub.setTextFromHtml(this,
                viewBinding.newsContent,
                params.get(3),
                ContextHub.getScreenWidth(this),
                0,
                null
        );
    }

    @Override
    protected void observeRequestState() {

    }

    @Override
    protected void initEvent() {

    }
}
