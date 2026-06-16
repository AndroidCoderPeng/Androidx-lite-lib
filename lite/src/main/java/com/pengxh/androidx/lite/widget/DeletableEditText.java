package com.pengxh.androidx.lite.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import com.pengxh.androidx.lite.R;

public class DeletableEditText extends AppCompatEditText implements View.OnFocusChangeListener, TextWatcher {

    private static final String TAG = "DeletableEditText";
    /**
     * 删除按钮的引用
     */
    private Drawable clearDrawable = getCompoundDrawablesRelative()[2];
    private boolean hasFocus;

    public DeletableEditText(Context context) {
        this(context, null);
    }

    public DeletableEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public DeletableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (clearDrawable == null) {
            clearDrawable = ContextCompat.getDrawable(context, R.drawable.ic_edit_text_delete);
        }

        setClearIconVisible(false);
        setOnFocusChangeListener(this);
        addTextChangedListener(this);

        addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                removeOnLayoutChangeListener(this);
                int iconSize = (int) (getTextSize() * 1.2);
                Log.d(TAG, "Clear IconSize: " + iconSize);
                clearDrawable.setBounds(0, 0, iconSize, iconSize);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Drawable drawable = getCompoundDrawablesRelative()[2];
            if (drawable != null) {
                int drawableStart = getWidth() - getPaddingRight();
                int drawableEnd = getWidth() - getPaddingRight();
                if (event.getX() > drawableStart && event.getX() < drawableEnd) {
                    removeTextChangedListener(this);
                    setText("");
                    addTextChangedListener(this);
                    performHapticFeedback(android.view.HapticFeedbackConstants.CONTEXT_CLICK);
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (hasFocus) {
            setClearIconVisible(!TextUtils.isEmpty(s));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 当EditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFocus = hasFocus;
        setClearIconVisible(hasFocus && !TextUtils.isEmpty(getText()));
    }

    protected void setClearIconVisible(boolean visible) {
        Drawable rightIcon = visible ? clearDrawable : null;
        setCompoundDrawablesRelative(
                getCompoundDrawablesRelative()[0],
                getCompoundDrawablesRelative()[1],
                rightIcon,
                getCompoundDrawablesRelative()[3]
        );
    }

    public void shakeIfEmpty() {
        if (TextUtils.isEmpty(getText())) {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.shake_animation);
            startAnimation(animation);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeTextChangedListener(this);
    }
}