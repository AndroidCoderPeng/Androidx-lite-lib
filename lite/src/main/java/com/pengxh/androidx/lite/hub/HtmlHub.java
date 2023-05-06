package com.pengxh.androidx.lite.hub;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import org.xml.sax.XMLReader;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class HtmlHub {
    private static final String TAG = "HtmlHub";

    /**
     * 将html字符串中的图片加载出来 设置点击事件 然后TextView进行显示
     *
     * @param activity
     * @param textView
     * @param sources  需要显示的带有html标签的文字
     * @param width    设备屏幕像素宽度
     */
    public static void setTextFromHtml(final Activity activity, final TextView textView, final String sources, final float width, final int rightPadding, Class<Activity> bigImageActivity) {
        if (activity == null || textView == null || TextUtils.isEmpty(sources)) {
            return;
        }
        synchronized (ImageHub.class) {
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
                                w = (int) (scale * w - ((FloatHub.dp2px(activity, rightPadding))));
                                h = (int) (scale * h);
                                drawable.setBounds(0, 0, w, h);
                                return drawable;
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    };
                    final CharSequence charSequence = Html.fromHtml(sources, imageGetter, new ImageClickHandler(activity, bigImageActivity));
                    activity.runOnUiThread(() -> textView.setText(charSequence));
                }
            }).start();
        }
    }

    private static class ImageClickHandler implements Html.TagHandler {

        private final Activity mActivity;
        private final Class<Activity> bigImageActivity;

        ImageClickHandler(Activity activity, Class<Activity> bigImageActivity) {
            this.mActivity = activity;
            this.bigImageActivity = bigImageActivity;
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
                output.setSpan(new ClickableImage(mActivity, imgURL, bigImageActivity), len - 1, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        private static class ClickableImage extends ClickableSpan {

            private final String imageURL;
            private final Activity mActivity;
            private final Class<Activity> bigImageActivity;

            ClickableImage(Activity activity, String url, Class<Activity> bigImageActivity) {
                this.mActivity = activity;
                this.imageURL = url;
                this.bigImageActivity = bigImageActivity;
            }

            @Override
            public void onClick(@NonNull View widget) {
                //查看大图
                ArrayList<String> urls = new ArrayList<>();
                urls.add(imageURL);
                if (bigImageActivity == null) {
                    Log.w(TAG, "ClickableImage onClick: ", new IllegalArgumentException("BigImageActivity not config"));
                } else {
                    ContextHub.navigatePageTo(mActivity, bigImageActivity, 0, urls);
                }
            }
        }
    }
}
