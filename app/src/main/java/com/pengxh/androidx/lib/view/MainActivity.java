package com.pengxh.androidx.lib.view;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.pengxh.androidx.lib.databinding.ActivityMainBinding;
import com.pengxh.androidx.lib.model.ImageListModel;
import com.pengxh.androidx.lib.vm.NetworkViewModel;
import com.pengxh.androidx.lite.adapter.EditableImageAdapter;
import com.pengxh.androidx.lite.adapter.ReadOnlyImageAdapter;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.hub.ContextHub;

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
        Log.d(TAG, "setupTopBarLayout: " + ContextHub.getStatusBarHeight(this));
    }

    @Override
    protected void initData() {
        ReadOnlyImageAdapter readOnlyImageAdapter = new ReadOnlyImageAdapter(this);
        EditableImageAdapter editableImageAdapter = new EditableImageAdapter(this, 9, 3f);

        NetworkViewModel viewModel = new ViewModelProvider(this).get(NetworkViewModel.class);
        viewModel.obtainImageList("头条", 0);
        viewModel.imageResultModel.observe(this, new Observer<ImageListModel>() {
            @Override
            public void onChanged(ImageListModel imageListModel) {
                readOnlyImageAdapter.setImageList(imageListModel.getImages());
                viewBinding.readonlyGridView.setAdapter(readOnlyImageAdapter);

                editableImageAdapter.setupImage(imageListModel.getImages());
                viewBinding.editableGridView.setAdapter(editableImageAdapter);
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