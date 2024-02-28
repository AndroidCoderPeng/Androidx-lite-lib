package com.pengxh.androidx.lite.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.WindowManager;

public class LoadingDialogHub {

    private static ProgressDialog loadingDialog;

    public static void show(Activity activity, String message) {
        if (!activity.isDestroyed()) {
            try {
                loadingDialog = ProgressDialog.show(activity, "", message);
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
            }
        }
    }

    public static void show(Activity activity, String title, String message) {
        if (!activity.isDestroyed()) {
            try {
                loadingDialog = ProgressDialog.show(activity, title, message);
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
            }
        }
    }

    public static void dismiss() {
        if (loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}
