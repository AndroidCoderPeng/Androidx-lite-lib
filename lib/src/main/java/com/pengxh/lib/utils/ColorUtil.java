package com.pengxh.lib.utils;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import java.util.Random;

public class ColorUtil {
    /**
     * 随机颜色
     */
    public static int randomColor() {
        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        return Color.rgb(red, green, blue);
    }

    /**
     * 获取xml颜色值
     */
    public static int convertColor(Context context, @ColorRes int res) {
        return ContextCompat.getColor(context, res);
    }
}
