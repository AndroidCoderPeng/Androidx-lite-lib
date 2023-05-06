package com.pengxh.androidx.lite.hub;

import android.content.Context;
import android.util.TypedValue;

public class FloatHub {
    /**
     * px转dp
     */
    public static int px2dp(Context context, float pxValue) {
        float scale = ContextHub.getScreenDensity(context);
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * dp转px
     */
    public static int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转换成px
     */
    public static int sp2px(Context context, float spValue) {
        float fontScale = ContextHub.getScreenDensity(context);
        return (int) (spValue * fontScale + 0.5f);
    }
}
