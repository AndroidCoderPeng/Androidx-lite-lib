package com.pengxh.androidx.lite.widget.audio;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.pengxh.androidx.lite.R;
import com.pengxh.androidx.lite.kit.ContextKit;

public class AudioPopupWindow {

    private final Context context;
    private final OnAudioPopupCallback callback;

    public AudioPopupWindow(Builder builder) {
        this.context = builder.context;
        this.callback = builder.callback;
    }

    public static class Builder {
        private Context context;
        private OnAudioPopupCallback callback;

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setOnAudioPopupCallback(OnAudioPopupCallback callback) {
            this.callback = callback;
            return this;
        }

        public AudioPopupWindow build() {
            return new AudioPopupWindow(this);
        }
    }

    public void create() {
        View view = View.inflate(context, R.layout.popu_microphone, null);
        int popWidth = (int) (ContextKit.getScreenWidth(context) * 0.30);
        int popHeight = (int) (ContextKit.getScreenHeight(context) * 0.15);
        PopupWindow window = new PopupWindow(view, popWidth, popHeight, true);
        window.setAnimationStyle(R.style.PopupAnimation);
        ImageView recodeImageView = view.findViewById(R.id.recodeImageView);
        TextView recodeTextView = view.findViewById(R.id.recodeTextView);
        callback.onViewCreated(window, recodeImageView, recodeTextView);
    }

    public interface OnAudioPopupCallback {
        void onViewCreated(PopupWindow window, ImageView imageView, TextView textView);
    }
}
