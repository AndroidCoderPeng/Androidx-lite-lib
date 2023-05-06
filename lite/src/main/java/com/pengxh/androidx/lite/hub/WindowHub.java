package com.pengxh.androidx.lite.hub;

import android.view.Window;
import android.view.WindowManager;

public class WindowHub {
    public static void setScreenBrightness(Window window, float brightness) {
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = brightness;
        window.setAttributes(layoutParams);
    }
}
