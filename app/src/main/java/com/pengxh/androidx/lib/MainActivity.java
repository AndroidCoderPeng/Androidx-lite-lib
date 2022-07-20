package com.pengxh.androidx.lib;

import android.content.Context;

import com.pengxh.androidx.lib.databinding.ActivityMainBinding;
import com.pengxh.androidx.lite.adapter.EditableImageAdapter;
import com.pengxh.androidx.lite.adapter.ReadOnlyImageAdapter;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;

import java.util.Arrays;
import java.util.List;

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

    }

    @Override
    protected void initData() {
        ReadOnlyImageAdapter readOnlyImageAdapter = new ReadOnlyImageAdapter(this);
        readOnlyImageAdapter.setImageList(images);
        viewBinding.readonlyGridView.setAdapter(readOnlyImageAdapter);
    }

    @Override
    protected void initEvent() {
        EditableImageAdapter editableImageAdapter = new EditableImageAdapter(this, 9, 3f);
        editableImageAdapter.setupImage(images);
        viewBinding.editableGridView.setAdapter(editableImageAdapter);
    }
}