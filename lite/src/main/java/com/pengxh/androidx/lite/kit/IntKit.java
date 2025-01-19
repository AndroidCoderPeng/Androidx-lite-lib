package com.pengxh.androidx.lite.kit;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Random;

public class IntKit {
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
     * 小于10首位补〇
     */
    public static String appendZero(int value) {
        if (value < 10) {
            return String.format(Locale.getDefault(), "0%d", value);
        } else {
            return String.valueOf(value);
        }
    }

    /**
     * 获取xml颜色值
     */
    public static int convertColor(Context context, @ColorRes int res) {
        return ContextCompat.getColor(context, res);
    }

    /**
     * res转Drawable
     */
    public static Drawable convertDrawable(Context context, @DrawableRes int res) {
        return ContextCompat.getDrawable(context, res);
    }

    private static final DecimalFormat decimalFormat = new DecimalFormat("#");

    /**
     * px转dp
     */
    public static int px2dp(Context context, int value) {
        decimalFormat.setRoundingMode(RoundingMode.CEILING);
        String result = decimalFormat.format(value / ContextKit.getScreenDensity(context));
        return Integer.parseInt(result);
    }

    /**
     * dp转px
     */
    public static int dp2px(Context context, int value) {
        decimalFormat.setRoundingMode(RoundingMode.CEILING);
        String result = decimalFormat.format(value * ContextKit.getScreenDensity(context));
        return Integer.parseInt(result);
    }

    /**
     * sp转px
     */
    public static int sp2px(Context context, int value) {
        float floatValue = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, value, context.getResources().getDisplayMetrics()
        );
        decimalFormat.setRoundingMode(RoundingMode.CEILING);
        String result = decimalFormat.format(floatValue);
        return Integer.parseInt(result);
    }
}
