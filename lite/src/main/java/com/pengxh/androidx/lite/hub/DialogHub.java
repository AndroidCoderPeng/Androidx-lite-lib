package com.pengxh.androidx.lite.hub;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.StyleRes;

public class DialogHub {
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
        params.width = (int) (ContextHub.getScreenWidth(dialog.getContext()) * r);
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
        //设置Dialog出现的动画
        window.setWindowAnimations(resId);
        WindowManager.LayoutParams params = window.getAttributes();
        double r = ratio;
        if (r >= 1) {
            r = 1f;
        }
        params.width = (int) (ContextHub.getScreenWidth(dialog.getContext()) * r);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
    }
}
