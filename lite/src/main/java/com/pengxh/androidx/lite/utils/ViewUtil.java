package com.pengxh.androidx.lite.utils;

import android.graphics.Bitmap;
import android.view.View;

public class ViewUtil {

    /**
     * View转Bitmap
     */
    public static Bitmap toBitmap(View view) {
        Bitmap bitmap = null;
        try {
            view.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            view.buildDrawingCache();
            bitmap = view.getDrawingCache();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
