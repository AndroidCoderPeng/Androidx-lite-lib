package com.pengxh.androidx.lite.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

public class DrawableUtil {
    /**
     * res转Drawable
     */
    public static Drawable convertDrawable(Context context, @DrawableRes int res) {
        return ContextCompat.getDrawable(context, res);
    }
}
