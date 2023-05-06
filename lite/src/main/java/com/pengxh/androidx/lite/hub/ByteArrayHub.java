package com.pengxh.androidx.lite.hub;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ByteArrayHub {
    /**
     * Camera data
     */
    public static Bitmap nv21ToBitmap(byte[] nv21, int width, int height) {
        Bitmap bitmap = null;
        try {
            final YuvImage image = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            image.compressToJpeg(new Rect(0, 0, width, height), 80, outputStream);
            final Bitmap bmp = BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size());
            bitmap = BitmapHub.rotateImage(-90, bmp);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
