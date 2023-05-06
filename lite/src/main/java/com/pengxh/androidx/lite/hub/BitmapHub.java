package com.pengxh.androidx.lite.hub;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.Gravity;

import androidx.annotation.ColorInt;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Bitmap 工具相关
 */
public class BitmapHub {
    /**
     * 旋转图片
     */
    public static Bitmap rotateImage(int angle, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 获取图片base64编码
     */
    public static String imageToBase64(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);//压缩质量

            outputStream.flush();
            outputStream.close();

            byte[] bitmapBytes = outputStream.toByteArray();
            String result = Base64.encodeToString(bitmapBytes, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
            return result.replace("-", "+")
                    .replace("_", "/");
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 圆形或者圆角图片
     */
    public static Drawable createRoundDrawable(Context context, Bitmap bitmap, int borderWidth, @ColorInt int color) {
        //原图宽度
        int bitmapWidth = bitmap.getWidth();
        //原图高度
        int bitmapHeight = bitmap.getHeight();

        //转换为正方形后的宽高
        int bitmapSquareWidth = Math.min(bitmapWidth, bitmapHeight);

        //最终图像的宽高
        int newBitmapSquareWidth = bitmapSquareWidth + borderWidth;

        Bitmap roundedBitmap = Bitmap.createBitmap(newBitmapSquareWidth, newBitmapSquareWidth, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundedBitmap);
        int x = borderWidth + bitmapSquareWidth - bitmapWidth;
        int y = borderWidth + bitmapSquareWidth - bitmapHeight;

        //裁剪后图像,注意X,Y要除以2 来进行一个中心裁剪
        canvas.drawBitmap(bitmap, x >> 1, y >> 1, null);
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setAntiAlias(true);
        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setColor(color);

        //添加边框
        canvas.drawCircle(canvas.getWidth() >> 1, canvas.getWidth() >> 1, newBitmapSquareWidth >> 1, borderPaint);

        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), roundedBitmap);
        roundedBitmapDrawable.setGravity(Gravity.CENTER);
        roundedBitmapDrawable.setCircular(true);
        return roundedBitmapDrawable;
    }
}
