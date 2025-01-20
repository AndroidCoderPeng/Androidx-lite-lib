package com.pengxh.androidx.lite.kit;

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
public class BitmapKit {
    /**
     * 保存图片，不压缩
     */
    public static void saveImage(Bitmap bitmap, String imagePath, int quality) {
        File imageFile = new File(imagePath);
        try (FileOutputStream fileOutputStream = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 旋转图片
     */
    public static Bitmap rotateImage(float angle, Bitmap bitmap) {
        // 确保在0到360度之间
        float rotatedAngle = (angle % 360 + 360) % 360;
        if (rotatedAngle == 0f) {
            return bitmap;
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(rotatedAngle);
        // 创建新的图片
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        // 尝试复用原Bitmap对象以节省内存
        if (rotatedBitmap != bitmap) {
            bitmap.recycle();
        }

        return rotatedBitmap;
    }

    /**
     * 获取图片base64编码
     * <p>
     * 如果是上传到服务器，编码格式为：Base64.NO_WRAP
     * <p>
     * 如果是本地使用，编码格式为：Base64.DEFAULT
     * <p>
     * 默认：Base64.NO_WRAP
     */
    public static String getBase64(Bitmap bitmap) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); // 压缩质量
            byte[] bitmapBytes = outputStream.toByteArray();
            return Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 圆形或者圆角图片
     * <p>
     * 也可以用 {@link com.google.android.material.imageview.ShapeableImageView} 代替
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
        borderPaint.setStrokeWidth(FloatKit.dp2px(context, borderStroke));
        borderPaint.setColor(color);

        //添加边框
        canvas.drawCircle(
                squareBitmapBorderLength / 2f,
                squareBitmapBorderLength / 2f,
                (squareBitmapBorderLength - FloatKit.dp2px(context, borderStroke)) / 2f,
                borderPaint
        );
        return roundedBitmap;
    }
}
