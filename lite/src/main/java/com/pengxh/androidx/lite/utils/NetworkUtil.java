package com.pengxh.androidx.lite.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
    private static final String TAG = "NetworkUtil";

    public static boolean isNetworkConnected(Context context) { //true是连接，false是没连接
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
}
