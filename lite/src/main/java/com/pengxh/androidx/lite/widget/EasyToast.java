package com.pengxh.androidx.lite.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.pengxh.androidx.lite.R;
import com.pengxh.androidx.lite.utils.DeviceSizeUtil;

public class EasyToast {
    public static void show(Context context, String message) {
        Toast toast = new Toast(context);
        TextView textView = new TextView(context);
        textView.setBackgroundResource(R.drawable.toast_bg_layout);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(16);
        textView.setText(message);
        textView.setPadding(
                DeviceSizeUtil.dp2px(context, 20), DeviceSizeUtil.dp2px(context, 10),
                DeviceSizeUtil.dp2px(context, 20), DeviceSizeUtil.dp2px(context, 10)
        );
        toast.setGravity(Gravity.BOTTOM, 0, DeviceSizeUtil.dp2px(context, 90));
        toast.setView(textView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
}
