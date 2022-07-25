package com.pengxh.androidx.lite.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/11/16.
 */
@SuppressLint("MissingPermission")
public class StringUtil {
    private static final String TAG = "StringUtil";

    /**
     * 判断输入的是否是数字
     */
    public static boolean isNumber(String str) {
        boolean isDigit = false;
        for (int i = 0; i < str.length(); i++) {
            isDigit = Character.isDigit(str.charAt(i));
        }
        return isDigit;
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
            Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]+");
            return pattern.matcher(str).matches();
        }
        return false;
    }


    public static boolean isPhoneNumber(String number) {
        String regExp = "^[1](([3][0-9])|([4][5-9])|([5][0-3,5-9])|([6][5,6])|([7][0-8])|([8][0-9])|([9][1,8,9]))[0-9]{8}$";
        if (number.length() != 11) {
            Log.d(TAG, "手机号应为11位数");
            return false;
        } else {
            Pattern p = Pattern.compile(regExp);
            Matcher m = p.matcher(number);
            boolean isMatch = m.matches();
            if (!isMatch) {
                Log.d(TAG, "请填入正确的手机号");
            }
            return isMatch;
        }
    }

    /**
     * 匹配邮箱地址
     */
    public static boolean isEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            Log.e(TAG, "邮箱地址不能为空：" + email);
            return false;
        } else {
            String regExp = "^[a-z0-9]+([._\\\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$";
            Pattern pattern = Pattern.compile(regExp);
            return pattern.matcher(email).matches();
        }
    }

    /**
     * 过滤空格，回车
     */
    public static String filterString(String s) {
        if (TextUtils.isEmpty(s)) {
            return s;
        }
        //先过滤回车换行
        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(s);
        s = m.replaceAll("");
        //再过滤空格
        return s.trim().replace(" ", "");
    }

    //获取SimSerialNumber
    @SuppressLint({"HardwareIds"})
    public static String obtainSimCardSerialNumber(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //Android 10改为获取Android_ID
            return Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Class<?> telephonyClass;
            try {
                telephonyClass = Class.forName(telephony.getClass().getName());
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return "";
                }
                String imei = telephony.getDeviceId();
                if (TextUtils.isEmpty(imei)) {
                    Method m = telephonyClass.getMethod("getSimSerialNumber", int.class);
                    //主卡，卡1
                    String mainCard = (String) m.invoke(telephony, 0);
                    //副卡，卡2
                    String otherCard = (String) m.invoke(telephony, 1);
                    if (TextUtils.isEmpty(mainCard)) {
                        return otherCard;
                    } else {
                        return mainCard;
                    }
                } else {
                    return imei;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String toJson(Object o) {
        if (o == null) {
            return "";
        }
        return new Gson().toJson(o);
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
}
