package com.pengxh.androidx.lite.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.pengxh.androidx.lite.R;


public class KeyBoardView extends LinearLayout implements View.OnClickListener {

    private static final String TAG = "KeyBoardView";
    private KeyboardClickListener listener = null;

    public KeyBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.widget_view_keyboard, this);
        setChildViewOnclick(this);
    }

    /**
     * 设置键盘子View的点击事件
     */
    private void setChildViewOnclick(ViewGroup parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            if (view instanceof ViewGroup) {
                setChildViewOnclick((ViewGroup) view);
                continue;
            }
            view.setOnClickListener(this);
        }
    }

    public boolean dispatchKeyEventInFullScreen(KeyEvent event) {
        if (event == null) {
            return false;
        }
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (isShown()) {
                this.setVisibility(View.GONE);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (listener == null) {
            Log.e(TAG, "onClick: ", new NullPointerException("listener is null"));
            return;
        }
        String value = ((Button) v).getText().toString();
        if (value.equals("DEL")) {
            listener.onDelete();
        } else {
            listener.onClick(value);
        }
    }

    public void setKeyboardClickListener(KeyboardClickListener keyboardClickListener) {
        this.listener = keyboardClickListener;
    }

    public interface KeyboardClickListener {
        void onClick(String value);

        void onDelete();
    }
}
