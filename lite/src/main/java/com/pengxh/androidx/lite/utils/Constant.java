package com.pengxh.androidx.lite.utils;

import com.pengxh.androidx.lite.R;

import java.util.Arrays;
import java.util.List;

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
    public static final String BIG_IMAGE_INTENT_DATA_KEY = "ImageDataKey";

    /**
     * 页面跳转Intent Data Key
     */
    public static final String INTENT_PARAM_KEY = "IntentParamKey";

    /**
     * 最大录音时长5分钟
     */
    public static final int MAX_LENGTH = 1000 * 60 * 5;

    public static final long HTTP_TIMEOUT = 15;

    /**
     * 录音动画图标
     */
    public static List<Integer> AUDIO_DRAWABLES = Arrays.asList(
            R.drawable.ic_audio_icon1, R.drawable.ic_audio_icon2, R.drawable.ic_audio_icon3
    );
}
