package com.pengxh.androidx.lite.utils;

import android.app.Activity;
import android.app.ProgressDialog;

public class LoadingDialog {

    private static ProgressDialog loadingDialog;

    public static void show(Activity activity, String message) {
        dismiss(); // 先关闭已有的dialog
        if (!activity.isDestroyed() && !activity.isFinishing()) {
            try {
                loadingDialog = new ProgressDialog(activity);
                loadingDialog.setMessage(message);
                loadingDialog.setCancelable(false);
                loadingDialog.setCanceledOnTouchOutside(false);
                loadingDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void show(Activity activity, String title, String message) {
        dismiss(); // 先关闭已有的dialog
        if (!activity.isDestroyed() && !activity.isFinishing()) {
            try {
                loadingDialog = new ProgressDialog(activity);
                loadingDialog.setTitle(title);
                loadingDialog.setMessage(message);
                loadingDialog.setCancelable(false);
                loadingDialog.setCanceledOnTouchOutside(false);
                loadingDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void dismiss() {
        if (loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }
}
