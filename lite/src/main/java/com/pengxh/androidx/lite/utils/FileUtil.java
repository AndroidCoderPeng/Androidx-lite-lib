package com.pengxh.androidx.lite.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtil {
    private static final String TAG = "FileUtil";

    /**
     * 递归计算文件夹大小
     */
    public static long calculateFileSize(File file) {
        long size = 0L;
        if (file != null) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        calculateFileSize(f);
                    } else {
                        size += f.length();
                    }
                }
            }
        }
        return size;
    }

    public static String formatFileSize(long size) {
        String fileSize;
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.HALF_UP);
        if (size < 1024) {
            fileSize = df.format(size) + "B";
        } else if (size < 1048576) {
            fileSize = df.format(((double) size / 1024)) + "K";
        } else if (size < 1073741824) {
            fileSize = df.format(((double) size / 1048576)) + "M";
        } else {
            fileSize = df.format(((double) size / 1073741824)) + "G";
        }
        return fileSize;
    }

    /**
     * 递归删除文件
     */
    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    deleteFile(f);
                }
            }
        } else if (file.exists()) {
            file.delete();
        }
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
}
