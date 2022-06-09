package com.pengxh.lib.utils;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;

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
}
