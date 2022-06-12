package com.pengxh.androidx.lite.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/11/16.
 */

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
     * 获取本地Asserts文件内容
     */
    public static String readAssetsFile(Context context, String fileName) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(context.getAssets().open(fileName));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder data = new StringBuilder();
            String s;
            try {
                while ((s = bufferedReader.readLine()) != null) {
                    data.append(s);
                }
                Log.d(TAG, "readAssetsFile ===> " + data);
                return data.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
}
