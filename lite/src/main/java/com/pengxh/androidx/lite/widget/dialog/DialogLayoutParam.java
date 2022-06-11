package com.pengxh.androidx.lite.widget.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.StyleRes;

import com.pengxh.androidx.lite.utils.DeviceSizeUtil;

public class DialogLayoutParam {
    public static void resetParams(Dialog dialog, double ratio) {
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getDecorView().setBackgroundColor(Color.TRANSPARENT);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = (int) (DeviceSizeUtil.getScreenWidth(dialog.getContext()) * ratio);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
    }

    public static void resetBottomParams(Dialog dialog, @StyleRes int resId, double ratio) {
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getDecorView().setBackgroundColor(Color.TRANSPARENT);
        window.setGravity(Gravity.BOTTOM);
        //设置Dialog出现的动画
        window.setWindowAnimations(resId);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = (int) (DeviceSizeUtil.getScreenWidth(dialog.getContext()) * ratio);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
    }
}
