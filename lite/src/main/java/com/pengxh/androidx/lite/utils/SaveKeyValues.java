package com.pengxh.androidx.lite.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveKeyValues {
    private static SharedPreferences sp;

    public static void initSharedPreferences(Context context) {
        String packageName = context.getPackageName();
        //获取到的包名带有“.”方便命名，取最后一个作为sp文件名
        String[] split = packageName.split("\\.");
        String fileName = split[split.length - 1];
        sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    /**
     * 存储
     */
    public static void putValue(String key, Object object) {
        if (key.isEmpty()) {
            return;
        }
        if (object == null) {
            removeKey(key);
            return;
        }
        SharedPreferences.Editor editor = sp.edit();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        try {
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取保存的数据
     */
    public static Object getValue(String key, Object defaultObject) {
        if (key.isEmpty()) {
            return null;
        }
        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        } else {
            return defaultObject;
        }
    }

    /**
     * 移除某个key值已经对应的值
     */
    public static void removeKey(String key) {
        if (key.isEmpty()) {
            return;
        }
        try {
            sp.edit().remove(key).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除所有数据
     */
    public static void clearAll() {
        try {
            sp.edit().clear().apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询某个key是否存在
     */
    public static boolean containsKey(String key) {
        if (key.isEmpty()) {
            return false;
        }
        return sp.contains(key);
    }
}