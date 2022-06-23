package com.pengxh.androidx.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;

import com.pengxh.androidx.lib.databinding.ActivityMainBinding;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.utils.ImageUtil;
import com.pengxh.androidx.lite.widget.EasyPopupWindow;

public class MainActivity extends AndroidxBaseActivity<ActivityMainBinding> {

    private static final String TAG = "MainActivity";
    private final Context context = MainActivity.this;
    private final int[] POPUP_IMAGES = new int[]{R.drawable.ic_menu_map, R.drawable.ic_satellite, R.drawable.ic_3d};
    private final String[] POPUP_TITLES = new String[]{"标准地图", "卫星地图", "3D地图"};

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    protected void initData() {
        //原型图片
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test);
        viewBinding.imageView.setImageDrawable(ImageUtil.createRoundDrawable(this, bitmap, 0, 0));
    }

    @Override
    protected void initEvent() {
        EasyPopupWindow easyPopupWindow = new EasyPopupWindow(context);
        easyPopupWindow.setPopupMenuItem(POPUP_IMAGES, POPUP_TITLES);
        easyPopupWindow.setOnPopupWindowClickListener(new EasyPopupWindow.OnPopupWindowClickListener() {
            @Override
            public void onPopupItemClicked(int position) {
                Log.d(TAG, "onPopupItemClicked: " + POPUP_TITLES[position]);
            }
        });
        viewBinding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = viewBinding.button.getWidth() - easyPopupWindow.getWidth();
                easyPopupWindow.showAsDropDown(viewBinding.button, x, 0);
            }
        });
    }
}