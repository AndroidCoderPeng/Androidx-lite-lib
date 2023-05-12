package com.pengxh.androidx.lite.hub;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Insets;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;

import androidx.core.app.ActivityCompat;

import com.pengxh.androidx.lite.utils.Constant;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

@SuppressLint("MissingPermission")
public class ContextHub {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        } else {
            NetworkInfo netWorkInfo = manager.getActiveNetworkInfo();
            if (netWorkInfo != null) {
                return netWorkInfo.isAvailable();
            }
        }
        return false;
    }

    public static <T> void navigatePageTo(Context context, Class<T> t) {
        context.startActivity(new Intent(context, t));
    }

    public static <T> void navigatePageTo(Context context, Class<T> t, String value) {
        Intent intent = new Intent(context, t);
        intent.putExtra(Constant.INTENT_PARAM, value);
        context.startActivity(intent);
    }

    public static <T> void navigatePageTo(Context context, Class<T> t, ArrayList<String> values) {
        Intent intent = new Intent(context, t);
        intent.putStringArrayListExtra(Constant.INTENT_PARAM, values);
        context.startActivity(intent);
    }

    public static <T> void navigatePageTo(Context context, Class<T> t, int index, ArrayList<String> imageList) {
        Intent intent = new Intent(context, t);
        intent.putExtra(Constant.BIG_IMAGE_INTENT_INDEX_KEY, index);
        intent.putStringArrayListExtra(Constant.BIG_IMAGE_INTENT_DATA_KEY, imageList);
        context.startActivity(intent);
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
                return data.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取SimSerialNumber
    @SuppressLint({"HardwareIds"})
    public static String getSimCardSerialNumber(Context context) {
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

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度，兼容Android 11+
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels + getStatusBarHeight(context);
    }

    /**
     * 获取状态栏高度，兼容Android 11+
     */
    public static int getStatusBarHeight(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            WindowMetrics windowMetrics = windowManager.getCurrentWindowMetrics();
            WindowInsets windowInsets = windowMetrics.getWindowInsets();

            Insets insets = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.statusBars());
            return insets.top;
        } else {
            if (Build.MANUFACTURER.toLowerCase(Locale.ROOT).equals("xiaomi")) {
                int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    return context.getResources().getDimensionPixelSize(resourceId);
                } else {
                    return 0;
                }
            } else {
                try {
                    Class<?> clazz = Class.forName("com.android.internal.R$dimen");
                    Object obj = clazz.newInstance();
                    Field field = clazz.getField("status_bar_height");
                    if (field.get(obj) == null) {
                        return 0;
                    }
                    int x = Integer.parseInt(Objects.requireNonNull(field.get(obj)).toString());
                    if (x > 0) {
                        return context.getResources().getDimensionPixelSize(x);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        }
    }

    /**
     * 获取屏幕密度
     * <p>
     * Dpi（dots per inch 像素密度）
     * Density 密度
     */
    public static Float getScreenDensity(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(android.content.Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display = context.getDisplay();
        } else {
            display = windowManager.getDefaultDisplay();
        }
        if (display == null) {
            return 0f;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return context.getResources().getDisplayMetrics().density;
        } else {
            display.getMetrics(displayMetrics);
            return displayMetrics.density;
        }
    }

    public static File createLogFile(Context context) {
        File documentDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "");
        String timeStamp = new SimpleDateFormat("yyyyMMdd", Locale.CHINA).format(new Date());
        File logFile = new File(documentDir + File.separator + "Log_" + timeStamp + ".txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return logFile;
    }

    public static File createAudioFile(Context context) {
        File audioDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        File audioFile = new File(audioDir + File.separator + "AUD_" + timeStamp + ".m4a");
        if (!audioFile.exists()) {
            try {
                audioFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return audioFile;
    }

    public static File createDownloadFileDir(Context context) {
        File downloadDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "");
        if (!downloadDir.exists()) {
            downloadDir.mkdir();
        }
        return downloadDir;
    }

    public static File createImageFileDir(Context context) {
        File imageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "");
        if (!imageDir.exists()) {
            imageDir.mkdir();
        }
        return imageDir;
    }

    public static File createCompressImageDir(Context context) {
        File imageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CompressImage");
        if (!imageDir.exists()) {
            imageDir.mkdir();
        }
        return imageDir;
    }
}
