package com.pengxh.androidx.lite.kit;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

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
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
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
            return Base64.getEncoder().encodeToString(bitmapBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
