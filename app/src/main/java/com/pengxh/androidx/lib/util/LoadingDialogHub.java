package com.pengxh.androidx.lib.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.WindowManager;

public class LoadingDialogHub {

    private static ProgressDialog dialog;

    public static void show(Activity activity, String message) {
        if (dialog == null) {
            dialog = new ProgressDialog(activity);
            dialog.setMessage(message);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setIndeterminate(true);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        if (!activity.isDestroyed()) {
            try {
                dialog.show();
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
            }
        }
    }

    public static void dismiss() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
