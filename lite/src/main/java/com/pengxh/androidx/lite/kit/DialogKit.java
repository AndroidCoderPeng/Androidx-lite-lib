package com.pengxh.androidx.lite.kit;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.StyleRes;

public class DialogKit {
    public static void resetParams(Dialog dialog, double ratio) {
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getDecorView().setBackgroundColor(Color.TRANSPARENT);
        WindowManager.LayoutParams params = window.getAttributes();
        double r = ratio;
        if (r >= 1) {
            r = 1f;
        }
        params.width = (int) (ContextKit.getScreenWidth(dialog.getContext()) * r);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
    }

    public static void resetParams(Dialog dialog, int gravity, @StyleRes int resId, double ratio) {
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getDecorView().setBackgroundColor(Color.TRANSPARENT);
        window.setGravity(gravity);
        if (resId != 0) {
            window.setWindowAnimations(resId);
        }
        WindowManager.LayoutParams params = window.getAttributes();
        double r = ratio;
        if (r >= 1) {
            r = 1f;
        }
        params.width = (int) (ContextKit.getScreenWidth(dialog.getContext()) * r);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
    }
}
