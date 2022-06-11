package com.pengxh.androidx.lite.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeOrDateUtil {
    private static final String TAG = "TimeOrDateUtil";
    private static SimpleDateFormat dateFormat;

    /**
     * 判断时间是否在本月之内
     */
    public static boolean isInCurrentMonth(long millSeconds) {
        //所选时间对应的月份
        dateFormat = new SimpleDateFormat("MM", Locale.CHINA);
        String selectedMonth = dateFormat.format(new Date(millSeconds));
        //系统时间对应的月份
        String systemMonth = dateFormat.format(new Date());
        return selectedMonth.equals(systemMonth);
    }

    /**
     * 时间戳转年月日时分秒
     */
    public static String timestampToCompleteDate(long millSeconds) {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return dateFormat.format(new Date(millSeconds));
    }

    /**
     * 时间戳转年月日
     */
    public static String timestampToDate(long millSeconds) {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return dateFormat.format(new Date(millSeconds));
    }

    /**
     * 时间戳转时分秒
     */
    public static String timestampToTime(long millSeconds) {
        dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
        return dateFormat.format(new Date(millSeconds));
    }

    /**
     * 时间转时间戳
     */
    public static long dateToTimestamp(String dateStr) {
        try {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            Date date = dateFormat.parse(dateStr);
            if (date != null) {
                return date.getTime();
            } else {
                return 0;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
