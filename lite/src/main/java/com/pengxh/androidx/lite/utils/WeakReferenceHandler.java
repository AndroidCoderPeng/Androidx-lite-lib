package com.pengxh.androidx.lite.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

public class WeakReferenceHandler extends Handler {

    private final WeakReference<Callback> mWeakReference;

    public WeakReferenceHandler(Callback callback) {
        mWeakReference = new WeakReference<>(callback);
    }

    public WeakReferenceHandler(Callback callback, Looper looper) {
        super(looper);
        mWeakReference = new WeakReference<>(callback);
    }

    @Override
    public void handleMessage(Message msg) {
        if (mWeakReference != null && mWeakReference.get() != null) {
            mWeakReference.get().handleMessage(msg);
        }
    }
}
