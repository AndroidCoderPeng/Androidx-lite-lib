package com.pengxh.androidx.lite.hub;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

public class FileHub {
    private static final String TAG = "FileHub";

    public static String read(File file) {
        StringBuilder builder;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line = bufferedReader.readLine();
            builder = new StringBuilder();
            while (line != null) {
                builder.append(line);
                builder.append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取图片base64编码
     */
    public static String imageFileToBase64(File file) {
        if (file == null) {
            return null;
        }
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();

            byte[] imgBytes = bos.toByteArray();
            String result = Base64.encodeToString(imgBytes, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
            return result.replace("-", "+")
                    .replace("_", "/");
        } catch (Exception e) {
            return null;
        }
    }

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
