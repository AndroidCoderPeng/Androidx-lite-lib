package com.pengxh.androidx.lite.utils;

import android.content.Context;
import android.content.Intent;

import com.pengxh.androidx.lite.activity.BigImageActivity;

import java.util.ArrayList;

public class ImageUtil {

    /**
     * @param context
     * @param index     展示图片的角标，从0开始
     * @param imageList
     */
    public static void showBigImage(Context context, int index, ArrayList<String> imageList) {
        Intent intent = new Intent(context, BigImageActivity.class);
        intent.putExtra(Constant.BIG_IMAGE_INTENT_INDEX_KEY, index);
        intent.putStringArrayListExtra(Constant.BIG_IMAGE_INTENT_DATA_KEY, imageList);
        context.startActivity(intent);
    }
}
