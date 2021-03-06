package com.pengxh.androidx.lite.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.pengxh.androidx.lite.activity.BigImageActivity;

import org.xml.sax.XMLReader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ImageUtil {

    private static final String TAG = "ImageUtil";

    /**
     * @param context
     * @param index     展示图片的角标，从0开始
     * @param imageList
     */
    public static void showBigImage(Context context, int index, ArrayList<String> imageList) {
        Intent intent = new Intent(context, BigImageActivity.class);
        intent.putExtra(Constant.BIG_IMAGE_INTENT_INDEX_KEY, index);
        intent.putStringArrayListExtra(Constant.BIG_IMAGE_INTENT_DATA_KEY, imageList);
        context.startActivity(intent);
    }

    /**
     * Camera
     */
    public static Bitmap nv21ToBitmap(byte[] nv21, int width, int height) {
        Bitmap bitmap = null;
        try {
            final YuvImage image = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            image.compressToJpeg(new Rect(0, 0, width, height), 80, outputStream);
            final Bitmap bmp = BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size());
            bitmap = rotateImage(-90, bmp);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * CameraX 原始预览Image数据（imageProxy.format == ImageFormat.YUV_420_888）转Bitmap
     * CameraX 预览Image数据（imageProxy.format == ImageFormat.JPEG）转Bitmap
     */
    public static Bitmap imageToBitmap(Image image, int format) {
        if (format == ImageFormat.YUV_420_888) {
            Image.Plane[] planes = image.getPlanes();

            ByteBuffer yBuffer = planes[0].getBuffer();
            ByteBuffer uBuffer = planes[1].getBuffer();
            ByteBuffer vBuffer = planes[2].getBuffer();

            int ySize = yBuffer.remaining();
            int uSize = uBuffer.remaining();
            int vSize = vBuffer.remaining();

            byte[] nv21 = new byte[ySize + uSize + vSize];
            //U and V are swapped
            yBuffer.get(nv21, 0, ySize);
            vBuffer.get(nv21, ySize, vSize);
            uBuffer.get(nv21, ySize + vSize, uSize);

            YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);

            byte[] imageBytes = out.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            return rotateImage(-90, bitmap);
        } else if (format == ImageFormat.JPEG) {
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
        }
        Log.e(TAG, "ImageToBitmap: ImageFormat error");
        return null;
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
     */
    public static String imageToBase64(File file) {
        if (file == null) {
            return null;
        }
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();

            byte[] imgBytes = bos.toByteArray();
            String result = Base64.encodeToString(imgBytes, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
            return result.replace("-", "+")
                    .replace("_", "/");
        } catch (Exception e) {
            return null;
        }
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
     * 将html字符串中的图片加载出来 设置点击事件 然后TextView进行显示
     *
     * @param activity
     * @param textView
     * @param sources  需要显示的带有html标签的文字
     * @param width    设备屏幕像素宽度
     */
    public static void setTextFromHtml(final Activity activity, final TextView textView, final String sources, final float width, final int rightPadding) {
        if (activity == null || textView == null || TextUtils.isEmpty(sources)) {
            return;
        }
        synchronized (ImageUtil.class) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setText(Html.fromHtml(sources));//默认不处理图片先这样简单设置

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Html.ImageGetter imageGetter = new Html.ImageGetter() {
                        @Override
                        public Drawable getDrawable(String source) {
                            try {
                                Drawable drawable = Glide.with(activity).asDrawable().load(source).into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                                if (drawable == null) {
                                    return null;
                                }
                                int w = drawable.getIntrinsicWidth();
                                int h = drawable.getIntrinsicHeight();
                                //对图片改变尺寸
                                float scale = width / w;
                                w = (int) (scale * w - ((DeviceSizeUtil.dp2px(activity, rightPadding))));
                                h = (int) (scale * h);
                                drawable.setBounds(0, 0, w, h);
                                return drawable;
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    };
                    final CharSequence charSequence = Html.fromHtml(sources, imageGetter, new ImageClickHandler(activity));
                    activity.runOnUiThread(() -> textView.setText(charSequence));
                }
            }).start();
        }
    }

    private static class ImageClickHandler implements Html.TagHandler {

        private final Activity mActivity;

        ImageClickHandler(Activity activity) {
            this.mActivity = activity;
        }

        @Override
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
            //获取传入html文本里面包含的所有Tag，然后取出img开头的
            if (tag.toLowerCase(Locale.getDefault()).equals("img")) {
                int len = output.length();
                // 获取图片地址
                ImageSpan[] images = output.getSpans(len - 1, len, ImageSpan.class);
                String imgURL = images[0].getSource();
                // 使图片可点击并监听点击事件
                output.setSpan(new ClickableImage(mActivity, imgURL), len - 1, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        private static class ClickableImage extends ClickableSpan {

            private final String imageURL;
            private final Activity mActivity;

            ClickableImage(Activity activity, String url) {
                this.mActivity = activity;
                this.imageURL = url;
            }

            @Override
            public void onClick(@NonNull View widget) {
                //查看大图
                ArrayList<String> urls = new ArrayList<>();
                urls.add(imageURL);
                showBigImage(mActivity, 0, urls);
            }
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
