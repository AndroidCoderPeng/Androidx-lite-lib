package com.pengxh.androidx.lite.kit;

import android.view.Window;
import android.view.WindowManager;

public class WindowKit {
    public static void setScreenBrightness(Window window, float brightness) {
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = brightness;
        window.setAttributes(layoutParams);
    }
}
