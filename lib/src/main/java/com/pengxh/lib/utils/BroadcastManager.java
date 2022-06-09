package com.pengxh.lib.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class BroadcastManager {
    private static final String TAG = "BroadcastManager";
    private static BroadcastManager broadcastManager;
    private final Context context;
    private final Map<String, BroadcastReceiver> receiverMap;

    private BroadcastManager(Context context) {
        this.context = context;
        receiverMap = new HashMap<>();
    }

    /**
     * 双重锁单例
     */
    public static BroadcastManager getInstance(Context context) {
        if (broadcastManager == null) {
            synchronized (BroadcastManager.class) {
                if (broadcastManager == null) {
                    broadcastManager = new BroadcastManager(context);
                }
            }
        }
        return broadcastManager;
    }

    /**
     * 添加单个Action,广播的初始化
     */
    public void addAction(BroadcastReceiver receiver, String action) {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(action);
            context.registerReceiver(receiver, filter);
            receiverMap.put(action, receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加多个Action,广播的初始化
     */
    public void addAction(BroadcastReceiver receiver, String... actions) {
        try {
            IntentFilter filter = new IntentFilter();
            for (String action : actions) {
                filter.addAction(action);
                receiverMap.put(action, receiver);
            }
            context.registerReceiver(receiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送广播
     *
     * @param action 唯一码
     * @param msg    参数
     */
    public void sendBroadcast(String action, String msg) {
        try {
            Intent intent = new Intent();
            intent.setAction(action);
            intent.putExtra("DataMessage", msg);
            Log.d(TAG, "BroadcastMessage ===> " + msg);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    context.unregisterReceiver(receiver);
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}