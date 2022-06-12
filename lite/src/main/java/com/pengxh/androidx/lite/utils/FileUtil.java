package com.pengxh.androidx.lite.utils;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

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

    public static File downloadFilePath(Context context) {
        File downloadDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "");
        if (!downloadDir.exists()) {
            downloadDir.mkdir();
        }
        return downloadDir;
    }

    public static void downloadFile(String url, String downloadDir, IDownloadListener listener) {
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().get().url(url).build();
        Call newCall = httpClient.newCall(request);
        /**
         * 如果已被加入下载队列，则取消之前的，重新下载
         * 断点下载以后再考虑
         * */
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
                int len;
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
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public interface IDownloadListener {
        void onDownloadStart(long totalBytes);

        void onProgressChanged(long currentBytes);

        void onDownloadEnd(File file);
    }
}
