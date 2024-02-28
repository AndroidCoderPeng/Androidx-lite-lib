package com.pengxh.androidx.lite.hub;

import android.content.Context;
import android.util.TypedValue;

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
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics()
        );
    }
}
