package com.pengxh.androidx.lite.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BroadcastManager {
    private static final String TAG = "BroadcastManager";
    /**
     * 解决双重锁单例Context导致内存泄漏的问题
     */
    private static WeakReference<Context> weakReferenceContext;
    private final Map<String, BroadcastReceiver> receiverMap = new ConcurrentHashMap<>();

    public BroadcastManager() {

    }

    private static class BroadcastManagerHolder {
        private static final BroadcastManager INSTANCE = new BroadcastManager();
    }

    /**
     * 双重锁单例
     */
    public static BroadcastManager get(Context context) {
        if (context != null) {
            weakReferenceContext = new WeakReference<>(context.getApplicationContext());
        }
        return BroadcastManagerHolder.INSTANCE;
    }

    /**
     * 添加多个Action,广播的初始化
     */
    public void addAction(BroadcastReceiver receiver, String... actions) {
        if (actions == null || actions.length == 0) {
            Log.w(TAG, "addAction: Actions array is null or empty");
            return;
        }
        IntentFilter filter = new IntentFilter();
        for (String action : actions) {
            if (action == null || action.isEmpty()) {
                Log.w(TAG, "addAction: Invalid action: " + action);
                continue;
            }
            filter.addAction(action);
            receiverMap.put(action, receiver);
        }
        Context context = weakReferenceContext.get();
        if (context != null) {
            context.registerReceiver(receiver, filter);
        } else {
            Log.e(TAG, "addAction: Context is null");
        }
    }

    /**
     * 发送广播
     *
     * @param action 唯一码
     * @param msg    参数
     */
    public void sendBroadcast(String action, String msg) {
        if (action == null || action.isEmpty()) {
            Log.w(TAG, "sendBroadcast: Invalid action: " + action);
            return;
        }
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(LiteConstant.BROADCAST_INTENT_DATA_KEY, msg);
        Context context = weakReferenceContext.get();
        if (context != null) {
            context.sendBroadcast(intent);
        } else {
            Log.e(TAG, "sendBroadcast: Context is null");
        }
    }

    /**
     * 销毁广播
     *
     * @param actions action集合
     */
    public void destroy(String... actions) {
        try {
            if (actions == null || actions.length == 0) {
                Log.w(TAG, "destroy: Actions array is null or empty");
                return;
            }
            Context context = weakReferenceContext.get();
            if (context == null) {
                Log.e(TAG, "destroy: Context is null");
                return;
            }
            for (String action : actions) {
                if (action == null || action.isEmpty()) {
                    Log.w(TAG, "destroy: Invalid action: " + action);
                    continue;
                }
                BroadcastReceiver receiver = receiverMap.remove(action);
                if (receiver != null) {
                    try {
                        context.unregisterReceiver(receiver);
                    } catch (Exception e) {
                        Log.e(TAG, "Error unregistering receiver for action: " + action, e);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "destroy: Error during destroy", e);
        }
    }
}