package com.pengxh.androidx.lite.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface WaterMarkPosition {
    /**
     * 左上
     */
    int LEFT_TOP = 1;

    /**
     * 右上
     */
    int RIGHT_TOP = 2;

    /**
     * 左下
     */
    int LEFT_BOTTOM = 3;

    /**
     * 右下
     */
    int RIGHT_BOTTOM = 4;

    /**
     * 中间
     */
    int CENTER = 0;
}
