package com.pengxh.androidx.lite.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

public class WeakReferenceHandler extends Handler {
    private final WeakReference<Callback> weakReference;

    public WeakReferenceHandler(Callback callback) {
        super(Looper.getMainLooper());
        weakReference = new WeakReference<>(callback);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        if (weakReference != null && weakReference.get() != null) {
            weakReference.get().handleMessage(msg);
        }
    }
}
