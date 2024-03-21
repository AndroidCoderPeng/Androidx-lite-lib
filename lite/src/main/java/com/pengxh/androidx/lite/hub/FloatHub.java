package com.pengxh.androidx.lite.hub;

import android.content.Context;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class FloatHub {
    /**
     * px转dp
     */
    public static float px2dp(Context context, float pxValue) {
        return pxValue / ContextHub.getScreenDensity(context);
    }

    /**
     * dp转px
     */
    public static float dp2px(Context context, float dpValue) {
        return dpValue * ContextHub.getScreenDensity(context);
    }

    /**
     * sp转换成px
     */
    public static float sp2px(Context context, float spValue) {
        DecimalFormat decimalFormat = new DecimalFormat("#");
        decimalFormat.setRoundingMode(RoundingMode.CEILING);
        String result = decimalFormat.format(spValue / ContextHub.getScreenDensity(context));
        return Float.parseFloat(result);
    }
}
