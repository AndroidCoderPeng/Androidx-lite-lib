package com.pengxh.androidx.lite.hub;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class DrawableHub {

    private static final float BITMAP_SCALE = 0.4f;

    /**
     * radius 模糊半径，值越大越模糊
     * <p>
     * 取值区间[0,25]
     */
    public static Bitmap toBlurBitmap(Context context, Drawable drawable, float radius) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        // 计算图片缩小后的长宽
        int width = Math.round((bitmap.getWidth() * BITMAP_SCALE));
        int height = Math.round((bitmap.getHeight() * BITMAP_SCALE));

        // 将缩小后的图片做为预渲染的图片。
        Bitmap inputBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        // 创建一张渲染后的输出图片。
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        // 创建RenderScript内核对象
        RenderScript rs = RenderScript.create(context);
        // 创建一个模糊效果的RenderScript的工具对象
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        blurScript.setRadius(radius);
        blurScript.setInput(tmpIn);
        blurScript.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }
}
