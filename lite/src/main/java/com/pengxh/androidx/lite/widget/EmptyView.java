package com.pengxh.androidx.lite.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.pengxh.androidx.lite.databinding.WidgetViewEmptyBinding;

public class EmptyView extends LinearLayout {

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        WidgetViewEmptyBinding binding = WidgetViewEmptyBinding.inflate(LayoutInflater.from(context), this, true);
        binding.reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onReloadButtonClicked();
                }
            }
        });
    }

    private OnClickListener listener;

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public interface OnClickListener {
        void onReloadButtonClicked();
    }
}
