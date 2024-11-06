package com.pengxh.androidx.lite.widget;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;

import com.pengxh.androidx.lite.R;
import com.pengxh.androidx.lite.kit.IntKit;

import java.util.Objects;

public class DeleteEditText extends AppCompatEditText implements View.OnFocusChangeListener, TextWatcher {

    private static final String TAG = "DeleteEditText";
    private final Paint paint = new Paint();
    private final Rect bounds = new Rect();
    /**
     * 删除按钮的引用
     */
    private Drawable clearDrawable = getCompoundDrawables()[2];
    private boolean hasFocus;

    public DeleteEditText(Context context) {
        this(context, null);
    }

    public DeleteEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public DeleteEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (clearDrawable == null) {
            clearDrawable = IntKit.convertDrawable(context, R.drawable.ic_edit_text_delete);
        }

        paint.setTextSize(getTextSize());
        paint.setTypeface(getTypeface());

        setClearIconVisible(false);
        setOnFocusChangeListener(this);
        addTextChangedListener(this);
        //获取输入框文字的高度
        post(() -> {
            //定义一个默认文字适配用户自定义的EditText的文字大小
            String tempText = "DeleteEditText";
            paint.getTextBounds(tempText, 0, tempText.length(), bounds);

            //设置图标大小
            int iconSize = (int) (bounds.height() * 1.5);
            Log.d(TAG, "EditeText View Text Height: " + bounds.height() + ", Clear IconSize: " + iconSize);
            clearDrawable.setBounds(0, 0, iconSize, iconSize);
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getX() > (getWidth() - getTotalPaddingRight()) && event.getX() < (getWidth() - getPaddingRight())) {
                setText("");
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
            setClearIconVisible(s.length() > 0);
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
        if (hasFocus) {
            setClearIconVisible(Objects.requireNonNull(getText()).length() > 0);
        } else {
            setClearIconVisible(false);
        }
    }

    protected void setClearIconVisible(boolean visible) {
        Drawable rightIcon = visible ? clearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], rightIcon, getCompoundDrawables()[3]);
    }
}