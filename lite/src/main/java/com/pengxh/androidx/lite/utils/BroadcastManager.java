package com.pengxh.androidx.lite.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class BroadcastManager {
    private static final String TAG = "BroadcastManager";
    /**
     * 解决双重锁单例Context导致内存泄漏的问题
     */
    private static WeakReference<Context> weakReferenceContext;
    private final Map<String, BroadcastReceiver> receiverMap = new HashMap<>();

    public BroadcastManager() {

    }

    private static class BroadcastManagerHolder {
        private static final BroadcastManager INSTANCE = new BroadcastManager();
    }

    /**
     * 双重锁单例
     */
    public static BroadcastManager get(Context context) {
        weakReferenceContext = new WeakReference<>(context);
        return BroadcastManagerHolder.INSTANCE;
    }

    /**
     * 添加单个Action,广播的初始化
     */
    public void addAction(BroadcastReceiver receiver, String action) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(action);
        weakReferenceContext.get().registerReceiver(receiver, filter);
        receiverMap.put(action, receiver);
    }

    /**
     * 添加多个Action,广播的初始化
     */
    public void addAction(BroadcastReceiver receiver, String... actions) {
        IntentFilter filter = new IntentFilter();
        for (String action : actions) {
            filter.addAction(action);
            receiverMap.put(action, receiver);
        }
        weakReferenceContext.get().registerReceiver(receiver, filter);
    }

    /**
     * 发送广播
     *
     * @param action 唯一码
     * @param msg    参数
     */
    public void sendBroadcast(String action, String msg) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(Constant.BROADCAST_INTENT_DATA_KEY, msg);
        weakReferenceContext.get().sendBroadcast(intent);
    }

    /**
     * 销毁广播
     *
     * @param actions action集合
     */
    public void destroy(String... actions) {
        try {
            for (String action : actions) {
                BroadcastReceiver receiver = receiverMap.get(action);
                if (receiver != null) {
                    weakReferenceContext.get().unregisterReceiver(receiver);
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}