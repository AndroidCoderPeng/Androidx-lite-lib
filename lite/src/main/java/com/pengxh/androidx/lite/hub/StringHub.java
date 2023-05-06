package com.pengxh.androidx.lite.hub;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.pengxh.androidx.lite.R;
import com.pengxh.androidx.lite.callback.OnDownloadListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/11/16.
 */
@SuppressLint("MissingPermission")
public class StringHub {
    private static final String TAG = "StringHub";
    private static SimpleDateFormat dateFormat;

    /**
     * 手动换行
     */
    public static String breakLine(String str, int length) {
        int step;
        if (length <= 0) {
            step = 15;
        } else {
            step = length;
        }

        if (str.isEmpty()) {
            return str;
        }

        int lines = str.length() / step;

        if (str.length() <= step) {
            return str;
        } else {
            if (str.length() % step == 0) {
                //整除
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < lines; i++) {
                    if (i == lines - 1) {
                        //最后一段文字
                        builder.append(str.substring(i * step));
                    } else {
                        String s = str.substring(i * step, (i + 1) * step);
                        builder.append(s).append("\r\n");
                    }
                }
                return builder.toString();
            } else {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i <= lines; i++) {
                    if (i == lines) {
                        //最后一段文字
                        builder.append(str.substring(i * step));
                    } else {
                        String s = str.substring(i * step, (i + 1) * step);
                        builder.append(s).append("\r\n");
                    }
                }
                return builder.toString();
            }
        }
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
    public static String filterSpaceOrEnter(String s) {
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

    public static void downloadFile(String url, String downloadDir, OnDownloadListener listener) {
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().get().url(url).build();
        Call newCall = httpClient.newCall(request);
        /**
         * 如果已被加入下载队列，则取消之前的，重新下载
         * 断点下载以后再考虑
         */
        if (newCall.isExecuted()) {
            newCall.cancel();
        }
        newCall.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                call.cancel();
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                InputStream stream = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    ResponseBody fileBody = response.body();
                    if (fileBody != null) {
                        stream = fileBody.byteStream();
                        long total = fileBody.contentLength();
                        listener.onDownloadStart(total);
                        File file = new File(downloadDir, url.substring(url.lastIndexOf("/") + 1));
                        fos = new FileOutputStream(file);
                        long current = 0;
                        while ((len = stream.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                            current += len;
                            listener.onProgressChanged(current);
                        }
                        fos.flush();
                        listener.onDownloadEnd(file);
                    }
                } catch (Exception e) {
                    call.cancel();
                    e.printStackTrace();
                } finally {
                    try {
                        Objects.requireNonNull(stream).close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Objects.requireNonNull(fos).close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static void show(Context context, String message) {
        Toast toast = new Toast(context);
        TextView textView = new TextView(context);
        textView.setBackgroundResource(R.drawable.toast_bg_layout);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(16);
        textView.setText(message);
        textView.setPadding(
                FloatHub.dp2px(context, 20), FloatHub.dp2px(context, 10),
                FloatHub.dp2px(context, 20), FloatHub.dp2px(context, 10)
        );
        toast.setGravity(Gravity.BOTTOM, 0, FloatHub.dp2px(context, 90));
        toast.setView(textView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
}
