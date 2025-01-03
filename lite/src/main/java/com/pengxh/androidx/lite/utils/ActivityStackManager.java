package com.pengxh.androidx.lite.utils;

import android.app.Activity;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class ActivityStackManager {
    private static final ArrayDeque<Activity> activityStack = new ArrayDeque<>();
    private static final ReentrantLock lock = new ReentrantLock();

    /**
     * 添加Activity到堆栈
     */
    public static void addActivity(Activity activity) {
        if (activity == null) {
            return;
        }
        lock.lock();
        try {
            activityStack.addLast(activity);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public static Activity currentActivity() {
        lock.lock();
        try {
            return activityStack.getLast();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public static void finishCurrentActivity() {
        lock.lock();
        try {
            Activity activity = activityStack.pollLast();
            if (activity == null) {
                return;
            }
            finishActivityInternal(activity);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 结束指定的Activity
     */
    public static void finishActivity(Activity activity) {
        if (activity == null) {
            return;
        }
        lock.lock();
        try {
            activityStack.remove(activity);
            finishActivityInternal(activity);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public static <T> void finishActivity(Class<T> clazz) {
        lock.lock();
        try {
            List<Activity> activitiesToRemove = new ArrayList<>();
            for (Activity activity : activityStack) {
                if (activity.getClass().equals(clazz)) {
                    activitiesToRemove.add(activity);
                }
            }
            for (Activity activity : activitiesToRemove) {
                finishActivityInternal(activity);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 结束所有Activity
     */
    public static void finishAllActivity() {
        lock.lock();
        try {
            for (Activity activity : activityStack) {
                finishActivityInternal(activity);
            }
            activityStack.clear();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 内部方法，结束Activity
     */
    private static void finishActivityInternal(Activity activity) {
        if (!activity.isFinishing()) {
            activity.finish();
        }
    }
}
