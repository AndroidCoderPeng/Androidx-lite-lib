package com.pengxh.androidx.lite.kit;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/11/16.
 */
public class StringKit {
    private static SimpleDateFormat dateFormat;

    /**
     * 获取汉语拼音首字母
     * 如：汉语 ===> HY
     */
    public static String getHanYuPinyin(String str) {
        StringBuilder pinyinStr = new StringBuilder();
        char[] chars = str.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (char c : chars) {
            if (c > 128) {
                try {
                    String[] stringArray = PinyinHelper.toHanyuPinyinStringArray(c, defaultFormat);
                    pinyinStr.append(stringArray[0]);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinStr.append(c);
            }
        }
        return pinyinStr.toString();
    }

    /**
     * 手动换行
     */
    public static String wrapLine(String str, int length) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        // 确保 step 至少为 1
        int step = Math.max(length, 1);

        StringBuilder builder = new StringBuilder();
        int strLength = str.length();
        for (int i = 0; i < strLength; i += step) {
            int end = Math.min(i + step, strLength);
            builder.append(str.substring(i, end));
            if (end < strLength) {
                builder.append(System.lineSeparator());
            }
        }

        return builder.toString();
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

    /**
     * 判断是否已过时
     */
    public static boolean isEarlierThenCurrent(String date) {
        long t1 = dateToTimestamp(date);
        long t2 = System.currentTimeMillis();
        return (t1 - t2) < 0;
    }

    /**
     * 时间差-小时
     */
    public static int diffCurrentTime(String dateStr) {
        if (dateStr.isEmpty()) {
            return 0;
        }
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        try {
            Date date = dateFormat.parse(dateStr);
            long diff = Math.abs(System.currentTimeMillis() - date.getTime());
            return (int) (diff / (3600000));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * yyyy-MM-dd HH:mm:ss 转 yyyy-MM-dd
     */
    public static String formatToYearMonthDay(String dateStr) {
        if (dateStr.isEmpty()) {
            return dateStr;
        }
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        try {
            Date date = dateFormat.parse(dateStr);

            dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    /**
     * 判断输入的是否是数字
     */
    public static boolean isNumber(String str) {
        String regex = "([-+])?\\d+(\\.\\d+)?";
        return str.matches(regex);
    }

    /**
     * 判断输入的是否是数字和字母
     */
    public static boolean isLetterAndDigit(String str) {
        boolean isDigit = false;
        boolean isLetter = false;
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {
                isDigit = true;
            } else if (Character.isLetter(str.charAt(i))) {
                isLetter = true;
            }
        }
        return isDigit && isLetter;
    }

    /**
     * 判断是否为汉字
     */
    public static boolean isChinese(String str) {
        if (!str.isEmpty()) {
            String regex = "[\\u4e00-\\u9fa5]+";
            return str.matches(regex);
        }
        return false;
    }


    public static boolean isPhoneNumber(String number) {
        if (number == null) {
            return false;
        }

        if (number.length() != 11) {
            return false;
        }

        Pattern regExpPattern = Pattern.compile("^1[3-9]\\d{9}$");
        return regExpPattern.matcher(number).matches();
    }

    /**
     * 匹配邮箱地址
     */
    public static boolean isEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        } else {
            String regExp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
            return email.matches(regExp);
        }
    }

    /**
     * @param log 待写入的内容
     */
    public static void writeToFile(File file, String log) {
        try {
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write(log);
            writer.newLine(); //换行
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void show(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
