package com.pengxh.androidx.lite.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.pengxh.androidx.lite.R;
import com.pengxh.androidx.lite.kit.IntKit;

import org.xml.sax.XMLReader;

import java.util.Locale;

public class HtmlRenderEngine implements Handler.Callback {
    private static final String TAG = "HtmlRenderEngine";
    private final Context context;
    private final String html;
    private final TextView textView;
    private final OnGetImageSourceListener listener;
    private final WeakReferenceHandler weakReferenceHandler = new WeakReferenceHandler(this);

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        return true;
    }

    public static class Builder {
        private Context context;
        private String html;
        private TextView textView;
        private OnGetImageSourceListener imageSourceListener;

        /**
         * 设置上下文
         */
        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        /**
         * 设置html格式的文本
         */
        public Builder setHtmlContent(String html) {
            this.html = html;
            return this;
        }

        /**
         * 设置显示html格式文本的View
         */
        public Builder setTargetView(TextView textView) {
            this.textView = textView;
            return this;
        }

        /**
         * 设置html里面图片地址回调监听
         */
        public Builder setOnGetImageSourceListener(OnGetImageSourceListener imageSourceListener) {
            this.imageSourceListener = imageSourceListener;
            return this;
        }

        public HtmlRenderEngine build() {
            return new HtmlRenderEngine(this);
        }
    }

    public HtmlRenderEngine(Builder builder) {
        this.context = builder.context;
        this.html = builder.html;
        this.textView = builder.textView;
        this.listener = builder.imageSourceListener;
    }

    public void load() {
        if (TextUtils.isEmpty(html)) {
            return;
        }
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        //默认不处理图片先这样简单设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
        } else {
            textView.setText(Html.fromHtml(html));
        }

        new Thread(renderRunnable).start();
    }

    private final Runnable renderRunnable = new Runnable() {
        @Override
        public void run() {
            Html.ImageGetter imageGetter = new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(String source) {
                    Drawable drawable;
                    try {
                        drawable = Glide.with(context).load(source).submit().get();
                    } catch (Exception e) {
                        e.printStackTrace();
                        drawable = IntKit.convertDrawable(context, R.mipmap.load_image_error);
                    }

                    int width = drawable.getIntrinsicWidth();
                    int height = drawable.getIntrinsicHeight();

                    //对图片按比例缩放尺寸
                    float scale = textView.getWidth() / (float) width;
                    width = (int) (scale * width);
                    height = (int) (scale * height);
                    drawable.setBounds(0, 0, width, height);
                    return drawable;
                }
            };
            Spanned htmlText;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                htmlText = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY, imageGetter, new Html.TagHandler() {
                    @Override
                    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
                        if (tag.toLowerCase(Locale.getDefault()).equals("img")) {
                            int length = output.length();
                            ImageSpan[] imageSpans = output.getSpans(length - 1, length, ImageSpan.class);
                            String imgSource = imageSpans[0].getSource();
                            if (imgSource == null) {
                                return;
                            }
                            output.setSpan(new ClickableSpan() {
                                @Override
                                public void onClick(@NonNull View widget) {
                                    listener.imageSource(imgSource);
                                }
                            }, length - 1, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                });
            } else {
                htmlText = Html.fromHtml(html, imageGetter, new Html.TagHandler() {
                    @Override
                    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
                        if (tag.toLowerCase(Locale.getDefault()).equals("img")) {
                            int length = output.length();
                            ImageSpan[] imageSpans = output.getSpans(length - 1, length, ImageSpan.class);
                            String imgSource = imageSpans[0].getSource();
                            if (imgSource == null) {
                                return;
                            }
                            output.setSpan(new ClickableSpan() {
                                @Override
                                public void onClick(@NonNull View widget) {
                                    listener.imageSource(imgSource);
                                }
                            }, length - 1, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                });
            }

            weakReferenceHandler.post(new Runnable() {
                @Override
                public void run() {
                    textView.setText(htmlText);
                }
            });
        }
    };

    public interface OnGetImageSourceListener {
        void imageSource(String url);
    }
}
