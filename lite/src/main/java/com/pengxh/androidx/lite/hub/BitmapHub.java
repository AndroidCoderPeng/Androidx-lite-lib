package com.pengxh.androidx.lite.hub;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Base64;

import androidx.annotation.ColorInt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Bitmap 工具相关
 */
public class BitmapHub {
    /**
     * 保存图片，不压缩
     */
    public static void saveImage(Bitmap bitmap, String imagePath) {
        try {
            File imageFile = new File(imagePath);
            FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
     *
     * 如果是上传到服务器，编码格式为：Base64.NO_WRAP
     *
     * 如果是本地使用，编码格式为：Base64.DEFAULT
     *
     * 默认：Base64.NO_WRAP
     */
    public static String getBase64(Bitmap bitmap) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            byte[] bitmapBytes = outputStream.toByteArray();
            outputStream.flush();
            outputStream.close();
            return Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 圆形或者圆角图片
     */
    public static Bitmap createRoundDrawable(Context context, Bitmap bitmap, int borderStroke, @ColorInt int color) {
        //转换为正方形后的宽高。以最短边为正方形边长，也是圆形图像的直径
        int squareBitmapBorderLength = Math.min(bitmap.getWidth(), bitmap.getHeight());

        Bitmap roundedBitmap = Bitmap.createBitmap(
                squareBitmapBorderLength, squareBitmapBorderLength, Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(roundedBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        //清屏
        canvas.drawARGB(0, 0, 0, 0);
        //画圆角
        Rect rect = new Rect(0, 0, squareBitmapBorderLength, squareBitmapBorderLength);
        RectF rectF = new RectF(rect);
        canvas.drawRoundRect(
                rectF, squareBitmapBorderLength / 2f, squareBitmapBorderLength / 2f, paint
        );

        // 取两层绘制，显示上层
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setAntiAlias(true);
        borderPaint.setStrokeWidth(FloatHub.dp2px(context, borderStroke));
        borderPaint.setColor(color);

        //添加边框
        canvas.drawCircle(
                squareBitmapBorderLength / 2f,
                squareBitmapBorderLength / 2f,
                (squareBitmapBorderLength - FloatHub.dp2px(context, borderStroke)) / 2f,
                borderPaint
        );
        return roundedBitmap;
    }
}
