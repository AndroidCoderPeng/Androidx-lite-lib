package com.pengxh.androidx.lite.activity;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.pengxh.androidx.lite.R;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.databinding.ActivityBigImageBinding;
import com.pengxh.androidx.lite.utils.Constant;
import com.pengxh.androidx.lite.utils.ImmerseStatusBarUtil;

import java.util.ArrayList;

public class BigImageActivity extends AndroidxBaseActivity<ActivityBigImageBinding> {

    @Override
    public void setupTopBarLayout() {
        ImmerseStatusBarUtil.setColor(this, Color.BLACK);
        viewBinding.leftBackView.setOnClickListener(view -> finish());
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {
        int index = getIntent().getIntExtra(Constant.BIG_IMAGE_INTENT_INDEX_KEY, 0);
        ArrayList<String> urls = getIntent().getStringArrayListExtra(Constant.BIG_IMAGE_INTENT_DATA_KEY);
        if (urls == null || urls.size() == 0) {
            return;
        }
        int imageSize = urls.size();
        viewBinding.pageNumberView.setText(String.format("(" + (index + 1) + "/" + imageSize + ")"));
        viewBinding.imagePagerView.setAdapter(new BigImageAdapter(this, urls));
        viewBinding.imagePagerView.setCurrentItem(index);
        viewBinding.imagePagerView.setOffscreenPageLimit(imageSize);
        viewBinding.imagePagerView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewBinding.pageNumberView.setText(String.format("(" + (position + 1) + "/" + imageSize + ")"));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class BigImageAdapter extends PagerAdapter {

        private final Context context;
        private final ArrayList<String> data;

        public BigImageAdapter(Context context, ArrayList<String> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_big_picture, container, false);
            PhotoView photoView = view.findViewById(R.id.photoView);
            Glide.with(context).load(data.get(position)).into(photoView);
            photoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            container.addView(view);
            //点击大图取消预览
            photoView.setOnClickListener(v -> finish());
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}