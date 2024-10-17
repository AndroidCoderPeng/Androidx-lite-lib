package com.pengxh.androidx.lite.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.pengxh.androidx.lite.R;
import com.pengxh.androidx.lite.databinding.WidgetViewTitleBarBinding;

/**
 * 界面顶部标题栏
 */
public class TitleBarView extends LinearLayout {

    private final WidgetViewTitleBarBinding binding;

    public TitleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        binding = WidgetViewTitleBarBinding.inflate(LayoutInflater.from(context), this, true);

        TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.TitleBarView);
        int leftImageRes = type.getResourceId(R.styleable.TitleBarView_tbv_left_image, R.drawable.ic_title_left);
        boolean isShowLeft = type.getBoolean(R.styleable.TitleBarView_tbv_show_left_image, true);
        int rightImageRes = type.getResourceId(R.styleable.TitleBarView_tbv_right_image, R.drawable.ic_title_right);
        boolean isShowRight = type.getBoolean(R.styleable.TitleBarView_tbv_show_right_image, false);
        CharSequence title = type.getText(R.styleable.TitleBarView_tbv_text);
        int titleColor = type.getColor(R.styleable.TitleBarView_tbv_text_color, Color.WHITE);
        boolean isSmallerTitle = type.getBoolean(R.styleable.TitleBarView_tbv_smaller_title, false);
        type.recycle();

        //左边图标
        if (isShowLeft) {
            binding.leftButton.setImageResource(leftImageRes);
            binding.leftButton.setOnClickListener(v -> {
                if (listener == null) {
                    throw new NullPointerException("listener is null");
                }
                listener.onLeftClick();
            });
        }

        //文字
        binding.titleView.setText(title);
        float textSize;
        if (isSmallerTitle) {
            textSize = 16f;
        } else {
            textSize = 18f;
        }
        binding.titleView.setTextSize(textSize);
        binding.titleView.setTextColor(titleColor);

        //右边图标
        if (isShowRight) {
            binding.rightButton.setImageResource(rightImageRes);
            binding.rightButton.setOnClickListener(v -> {
                if (listener == null) {
                    throw new NullPointerException("listener is null");
                }
                listener.onRightClick();
            });
        }
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
        binding.titleView.setText(title);
        invalidate();
    }

    /**
     * 获取当前显示标题文字
     */
    public String getTitle() {
        return binding.titleView.getText().toString();
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
