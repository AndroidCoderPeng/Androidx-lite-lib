package com.pengxh.androidx.lite.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pengxh.androidx.lite.R;
import com.pengxh.androidx.lite.hub.FloatHub;
import com.pengxh.androidx.lite.hub.IntHub;

/**
 * 界面顶部标题栏
 */
public class TitleBarView extends RelativeLayout {

    private final int titleHeight = IntHub.dp2px(getContext(), 45);
    private final TextView textView;

    public TitleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.TitleBarView);
        int leftImageRes = type.getResourceId(R.styleable.TitleBarView_tbv_left_image, R.drawable.ic_title_left);
        boolean isShowLeft = type.getBoolean(R.styleable.TitleBarView_tbv_show_left_image, true);
        int rightImageRes = type.getResourceId(R.styleable.TitleBarView_tbv_right_image, R.drawable.ic_title_right);
        boolean isShowRight = type.getBoolean(R.styleable.TitleBarView_tbv_show_right_image, false);
        CharSequence title = type.getText(R.styleable.TitleBarView_tbv_text);
        int titleColor = type.getColor(R.styleable.TitleBarView_tbv_text_color, Color.WHITE);
        float titleSize = type.getDimension(R.styleable.TitleBarView_tbv_text_size, 18f);
        boolean onlyShowTitle = type.getBoolean(R.styleable.TitleBarView_tbv_only_show_title, false);
        type.recycle();

        if (onlyShowTitle) {
            //文字
            RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            titleParams.height = titleHeight;
            textView = new TextView(context);
            textView.setText(title);
            textView.setSingleLine(true);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setTextSize(FloatHub.sp2px(context, titleSize));
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(titleColor);
            titleParams.addRule(CENTER_IN_PARENT, TRUE);
            textView.setLayoutParams(titleParams);
            addView(textView);
        } else {
            int iconSize = IntHub.dp2px(getContext(), 25);
            int textMargin = IntHub.dp2px(getContext(), 10);

            //左边图标
            if (isShowLeft) {
                RelativeLayout.LayoutParams leftImageParams = new RelativeLayout.LayoutParams(iconSize, iconSize);
                ImageView leftImageView = new ImageView(context);
                leftImageView.setImageResource(leftImageRes);
                leftImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                leftImageParams.setMarginStart(textMargin);
                leftImageParams.addRule(CENTER_VERTICAL, TRUE);
                addView(leftImageView, leftImageParams);
                leftImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            listener.onLeftClick();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            //文字
            RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            titleParams.height = titleHeight;
            textView = new TextView(context);
            textView.setText(title);
            textView.setSingleLine(true);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setTextSize(FloatHub.sp2px(context, titleSize));
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(titleColor);
            titleParams.addRule(CENTER_IN_PARENT, TRUE);
            textView.setLayoutParams(titleParams);
            addView(textView);

            //右边图标
            if (isShowRight) {
                RelativeLayout.LayoutParams rightImageParams = new RelativeLayout.LayoutParams(iconSize, iconSize);
                ImageView rightImageView = new ImageView(context);
                rightImageView.setImageResource(rightImageRes);
                rightImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                rightImageParams.setMarginEnd(textMargin);
                rightImageParams.addRule(CENTER_VERTICAL, TRUE);
                rightImageParams.addRule(ALIGN_PARENT_END, TRUE);
                addView(rightImageView, rightImageParams);
                rightImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            listener.onRightClick();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(widthSpecSize, titleHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //渲染背景
        canvas.drawColor(this.getSolidColor());
    }

    /**
     * 动态设置标题
     */
    public void setTitle(String title) {
        textView.setText(title);
        invalidate();
    }

    private OnClickListener listener;

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public interface OnClickListener {
        void onLeftClick();

        void onRightClick();
    }
}
