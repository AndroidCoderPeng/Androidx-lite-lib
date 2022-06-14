package com.pengxh.androidx.lite.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;

@SuppressLint("MissingPermission")
public class ContextUtil {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        } else {
            NetworkInfo netWorkInfo = manager.getActiveNetworkInfo();
            if (netWorkInfo != null) {
                return netWorkInfo.isAvailable();
            }
        }
        return false;
    }

    public static <T> void navigatePageTo(Context context, Class<T> t) {
        context.startActivity(new Intent(context, t));
    }

    public static <T> void navigatePageTo(Context context, Class<T> t, String value) {
        Intent intent = new Intent(context, t);
        intent.putExtra(Constant.INTENT_PARAM, value);
        context.startActivity(intent);
    }

    public static <T> void navigatePageTo(Context context, Class<T> t, ArrayList<String> values) {
        Intent intent = new Intent(context, t);
        intent.putStringArrayListExtra(Constant.INTENT_PARAM, values);
        context.startActivity(intent);
    }

    public static <T> void navigatePageTo(Context context, Class<T> t, int index, ArrayList<String> imageList) {
        Intent intent = new Intent(context, t);
        intent.putExtra(Constant.BIG_IMAGE_INTENT_INDEX_KEY, index);
        intent.putStringArrayListExtra(Constant.BIG_IMAGE_INTENT_DATA_KEY, imageList);
        context.startActivity(intent);
    }
}
