package com.pengxh.androidx.lite.widget.audio;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.pengxh.androidx.lite.R;
import com.pengxh.androidx.lite.hub.ContextHub;

public class AudioPopupWindow {
    public static void create(Context context, IWindowListener listener) {
        View view = View.inflate(context, R.layout.popu_microphone, null);
        int popWidth = (int) (ContextHub.getScreenWidth(context) * 0.30);
        int popHeight = (int) (ContextHub.getScreenHeight(context) * 0.15);
        PopupWindow window = new PopupWindow(view, popWidth, popHeight, true);
        window.setAnimationStyle(R.style.PopupAnimation);

        ImageView recodeImageView = view.findViewById(R.id.recodeImageView);
        TextView recodeTextView = view.findViewById(R.id.recodeTextView);

        listener.onViewCreated(window, recodeImageView, recodeTextView);
    }

    public interface IWindowListener {
        void onViewCreated(PopupWindow window, ImageView imageView, TextView textView);
    }
}
