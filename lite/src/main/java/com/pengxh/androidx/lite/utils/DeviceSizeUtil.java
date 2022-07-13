package com.pengxh.androidx.lite.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

public class DeviceSizeUtil {

    /**
     * 获取 DisplayMetrics
     *
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int obtainScreenWidth(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public static int obtainScreenHeight(Context context) {
        int height = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = context.getResources().getDimensionPixelSize(resourceId);
        }
        return getDisplayMetrics(context).heightPixels + height;
    }

    /**
     * 获取屏幕密度
     * <p>
     * Dpi（dots per inch 像素密度）
     * Density 密度
     */
    private static float obtainScreenDensity(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        return dm.density;
    }

    /**
     * px转dp
     */
    public static int px2dp(Context context, float pxValue) {
        float scale = obtainScreenDensity(context);
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
        float fontScale = obtainScreenDensity(context);
        return (int) (spValue * fontScale + 0.5f);
    }
}
