package com.pengxh.androidx.lite.kit;

import android.content.Context;
import android.util.TypedValue;

public class FloatKit {

    /**
     * px转dp
     */
    public static float px2dp(Context context, float pxValue) {
        return pxValue / ContextKit.getScreenDensity(context);
    }

    /**
     * dp转px
     */
    public static float dp2px(Context context, float dpValue) {
        return dpValue * ContextKit.getScreenDensity(context);
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
