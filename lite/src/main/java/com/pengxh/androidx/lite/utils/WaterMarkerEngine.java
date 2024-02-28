package com.pengxh.androidx.lite.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;

import androidx.annotation.NonNull;

import com.pengxh.androidx.lite.annotations.WaterMarkPosition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WaterMarkerEngine implements Handler.Callback {

    private final Bitmap originalBitmap;
    private final String marker;
    private final int textColor;
    private final float textSize;
    private final float textMargin;
    private final int position;
    private final String fileName;
    private final OnWaterMarkerAddedListener listener;
    private final WeakReferenceHandler weakReferenceHandler = new WeakReferenceHandler(this);

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        return true;
    }

    public static class Builder {
        private Bitmap originalBitmap;
        private String marker;
        private int textColor = Color.WHITE;
        private float textSize = 16f;
        private float textMargin = 10f;
        private int position = WaterMarkPosition.RIGHT_BOTTOM;
        private String fileName;
        private OnWaterMarkerAddedListener addedListener;

        /**
         * 设置原始Bitmap
         */
        public Builder setOriginalBitmap(Bitmap bitmap) {
            this.originalBitmap = bitmap;
            return this;
        }

        /**
         * 设置水印文字
         */
        public Builder setTextMaker(String marker) {
            this.marker = marker;
            return this;
        }

        /**
         * 设置水印文字颜色
         */
        public Builder setTextColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        /**
         * 设置水印文字大小
         */
        public Builder setTextSize(float textSize) {
            this.textSize = textSize;
            return this;
        }

        /**
         * 设置水印文字距离Bitmap内边距
         */
        public Builder setTextMargin(float textMargin) {
            this.textMargin = textMargin;
            return this;
        }

        /**
         * 设置水印文字位置
         */
        public Builder setMarkerPosition(@WaterMarkPosition int position) {
            this.position = position;
            return this;
        }

        /**
         * 设置水印图片保存路径
         */
        public Builder setMarkedSavePath(String fileName) {
            this.fileName = fileName;
            return this;
        }

        /**
         * 设置水印图片回调监听
         */
        public Builder setOnWaterMarkerAddedListener(OnWaterMarkerAddedListener addedListener) {
            this.addedListener = addedListener;
            return this;
        }

        public WaterMarkerEngine build() {
            return new WaterMarkerEngine(this);
        }
    }

    public WaterMarkerEngine(Builder builder) {
        this.originalBitmap = builder.originalBitmap;
        this.marker = builder.marker;
        this.textColor = builder.textColor;
        this.textSize = builder.textSize;
        this.textMargin = builder.textMargin;
        this.position = builder.position;
        this.fileName = builder.fileName;
        this.listener = builder.addedListener;
    }

    /**
     * 开始添加水印
     */
    public void start() {
        listener.onStart();
        //初始化画笔
        TextPaint textPaint = new TextPaint();
        Rect textRect = new Rect();
        textPaint.setColor(textColor);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        // 获取清晰的图像采样
        textPaint.setDither(true);
        textPaint.setFilterBitmap(true);
        textPaint.setTextSize(textSize);
        textPaint.getTextBounds(marker, 0, marker.length(), textRect);

        //添加水印
        Bitmap.Config bitmapConfig = originalBitmap.getConfig();
        Bitmap copyBitmap = originalBitmap.copy(bitmapConfig, true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Canvas canvas = new Canvas(copyBitmap);
                int bitmapWidth = copyBitmap.getWidth();
                int bitmapHeight = copyBitmap.getHeight();
                switch (position) {
                    case WaterMarkPosition.LEFT_TOP:
                        canvas.drawText(marker, textMargin, textMargin, textPaint);
                        break;
                    case WaterMarkPosition.RIGHT_TOP:
                        canvas.drawText(marker, bitmapWidth - textRect.width() - textMargin, textMargin, textPaint);
                        break;
                    case WaterMarkPosition.LEFT_BOTTOM:
                        canvas.drawText(marker, textMargin, bitmapHeight - textMargin, textPaint);
                        break;
                    case WaterMarkPosition.RIGHT_BOTTOM:
                        canvas.drawText(marker, bitmapWidth - textRect.width() - textMargin, bitmapHeight - textMargin, textPaint);
                        break;
                    case WaterMarkPosition.CENTER:
                        canvas.drawText(marker, (bitmapWidth - textRect.width()) / 2f, bitmapHeight / 2f, textPaint);
                        break;
                }
                //编码照片是耗时操作，需要在子线程或者协程里面
                File file = new File(fileName);
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    /**
                     * 第一个参数如果是Bitmap.CompressFormat.PNG,那不管第二个值如何变化，图片大小都不会变化，不支持png图片的压缩
                     * 第二个参数是压缩比重，图片存储在磁盘上的大小会根据这个值变化。值越小存储在磁盘的图片文件越小
                     * */
                    copyBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();

                    weakReferenceHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onMarkAdded(file);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public interface OnWaterMarkerAddedListener {
        void onStart();

        void onMarkAdded(File file);
    }
}
