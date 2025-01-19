package com.pengxh.androidx.lite.kit;

import android.text.TextUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LongKit {
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
     * 时间戳转分秒
     */
    public static String millsToTime(long millSeconds) {
        dateFormat = new SimpleDateFormat("mm:ss", Locale.CHINA);
        return dateFormat.format(new Date(millSeconds));
    }

    /**
     * 根据时间戳得到上个月的日期
     */
    public static String timestampToLastMonthDate(long timestamp) {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp));
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 29);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * 根据时间戳得到上周的日期
     */
    public static String timestampToLastWeekDate(long timestamp) {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp));
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 6);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * 根据时间戳得到上周的时间
     */
    public static String timestampToLastWeekTime(long timestamp) {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp));
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 6);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * 获取当前月份所在季度
     */
    public static int obtainQuarterOfYear(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp));
        return calendar.get(Calendar.MONTH) / 3 + 1;
    }

    /**
     * 判断时间是否早于当前时间
     */
    public static boolean isEarlierThanStart(long timestamp, String date) {
        if (TextUtils.isEmpty(date)) {
            return false;
        }
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        try {
            return timestamp < dateFormat.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    private static final long KB = 1024;
    private static final long MB = KB * 1024;
    private static final long GB = MB * 1024;

    public static String formatFileSize(long size) {
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.HALF_UP);
        if (size < 0) {
            throw new IllegalArgumentException("文件大小不能为负数");
        }

        if (size < KB) {
            return df.format(size) + "B";
        } else if (size < MB) {
            return df.format(((double) size / KB)) + " KB";
        } else if (size < GB) {
            return df.format(((double) size / MB)) + " MB";
        } else {
            return df.format(((double) size / GB)) + " GB";
        }
    }
}
