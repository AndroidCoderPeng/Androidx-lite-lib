package com.pengxh.androidx.lite.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 广播管理器
 * 提供统一的广播注册、注销、发送功能
 */
public class BroadcastManager {
    private static volatile BroadcastManager INSTANCE;

    public static BroadcastManager getDefault() {
        if (INSTANCE == null) {
            synchronized (BroadcastManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BroadcastManager();
                }
            }
        }
        return INSTANCE;
    }

    private final String kTag = "BroadcastManager";
    private final Map<String, BroadcastReceiver> mReceivers = new HashMap<>();

    private BroadcastManager() {
    }

    /**
     * 注册广播接收器
     */
    public void registerReceiver(Context context, String action, BroadcastReceiver receiver) {
        synchronized (mReceivers) {
            if (mReceivers.containsKey(action)) {
                // 先注销已存在的
                unregisterReceiver(context, action);
            }

            IntentFilter filter = new IntentFilter(action);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED);
            } else {
                context.registerReceiver(receiver, filter);
            }
            mReceivers.put(action, receiver);
        }
    }

    /**
     * 批量注册广播接收器
     */
    public void registerReceivers(Context context, List<String> actions, BroadcastReceiver receiver) {
        for (String action : actions) {
            registerReceiver(context, action, receiver);
        }
    }

    /**
     * 注销广播接收器
     */
    public void unregisterReceiver(Context context, String action) {
        synchronized (mReceivers) {
            BroadcastReceiver receiver = mReceivers.get(action);
            if (receiver != null) {
                try {
                    context.unregisterReceiver(receiver);
                } catch (IllegalArgumentException e) {
                    Log.w(kTag, "unregisterReceiver: ", e);
                }
                mReceivers.remove(action);
            }
        }
    }

    /**
     * 注销所有广播接收器
     */
    public void unregisterAll(Context context) {
        synchronized (mReceivers) {
            for (BroadcastReceiver receiver : mReceivers.values()) {
                try {
                    context.unregisterReceiver(receiver);
                } catch (IllegalArgumentException e) {
                    Log.w(kTag, "unregisterAll: ", e);
                }
            }
            mReceivers.clear();
        }
    }

    /**
     * 发送广播
     */
    public void sendBroadcast(Context context, String action, Map<String, Object> extras) {
        Intent intent = new Intent(action);
        if (extras != null) {
            for (Map.Entry<String, Object> entry : extras.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof String) {
                    intent.putExtra(key, (String) value);
                } else if (value instanceof Integer) {
                    intent.putExtra(key, (Integer) value);
                } else if (value instanceof Long) {
                    intent.putExtra(key, (Long) value);
                } else if (value instanceof Float) {
                    intent.putExtra(key, (Float) value);
                } else if (value instanceof Double) {
                    intent.putExtra(key, (Double) value);
                } else if (value instanceof Boolean) {
                    intent.putExtra(key, (Boolean) value);
                } else if (value instanceof Serializable) {
                    intent.putExtra(key, (Serializable) value);
                }
            }
        }
        context.sendBroadcast(intent);
    }

    /**
     * 发送有序广播
     */
    public void sendOrderedBroadcast(Context context, String action, Map<String, Object> extras, String receiverPermission) {
        Intent intent = new Intent(action);
        if (extras != null) {
            for (Map.Entry<String, Object> entry : extras.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof String) {
                    intent.putExtra(key, (String) value);
                } else if (value instanceof Integer) {
                    intent.putExtra(key, (Integer) value);
                } else if (value instanceof Long) {
                    intent.putExtra(key, (Long) value);
                } else if (value instanceof Float) {
                    intent.putExtra(key, (Float) value);
                } else if (value instanceof Double) {
                    intent.putExtra(key, (Double) value);
                } else if (value instanceof Boolean) {
                    intent.putExtra(key, (Boolean) value);
                } else if (value instanceof Serializable) {
                    intent.putExtra(key, (Serializable) value);
                }
            }
        }
        context.sendOrderedBroadcast(intent, receiverPermission);
    }
}