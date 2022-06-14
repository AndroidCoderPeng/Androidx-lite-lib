package com.pengxh.androidx.lite.utils;

public class Constant {
    /**
     * 广播接收者消息Key
     */
    public static final String BROADCAST_INTENT_DATA_KEY = "DataMessageKey";
    /**
     * 查看大图Intent IndexKey
     */
    public static final String BIG_IMAGE_INTENT_INDEX_KEY = "IndexKey";

    /**
     * 查看大图Intent Data Key
     */
    public static final String BIG_IMAGE_INTENT_DATA_KEY = "ImageData";

    /**
     * 页面跳转Intent Data Key
     */
    public static final String INTENT_PARAM = "intentParam";

    /**
     * 最大录音时长5分钟
     */
    public static final int MAX_LENGTH = 1000 * 60 * 5;

    public static final long HTTP_TIMEOUT = 15;

    /**
     * 连接超时时间10s
     */
    public static final long MAX_CONNECT_TIME = 10000L;

    public static final String BLUETOOTH_STATE_CHANGED = "android.bluetooth.adapter.action.STATE_CHANGED";
    public static final int BLUETOOTH_ON = 20;
    public static final int BLUETOOTH_OFF = 21;
    public static final int CONNECT_SUCCESS = 22;
    public static final int CONNECT_FAILURE = 23;
    public static final int DISCONNECT_SUCCESS = 24;
    public static final int SEND_SUCCESS = 25;
    public static final int SEND_FAILURE = 26;
    public static final int RECEIVE_SUCCESS = 27;
    public static final int RECEIVE_FAILURE = 28;
    public static final int DISCOVERY_DEVICE = 29;
    public static final int DISCOVERY_OUT_TIME = 30;
}
