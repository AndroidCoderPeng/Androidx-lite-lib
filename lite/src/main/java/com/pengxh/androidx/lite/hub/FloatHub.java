package com.pengxh.androidx.lite.hub;

import android.content.Context;
import android.util.TypedValue;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class FloatHub {
    private static final DecimalFormat decimalFormat = new DecimalFormat("#");

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
        float floatValue = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics()
        );
        decimalFormat.setRoundingMode(RoundingMode.CEILING);
        String result = decimalFormat.format(floatValue);
        return Float.parseFloat(result);
    }
}
