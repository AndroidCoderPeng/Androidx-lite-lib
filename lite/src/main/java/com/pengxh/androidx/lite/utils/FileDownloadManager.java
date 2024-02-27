package com.pengxh.androidx.lite.utils;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class FileDownloadManager {
    private static final String TAG = "FileDownloadManager";
    private final String url;
    private final String suffix;
    private final File directory;
    private final OnFileDownloadListener listener;
    private final OkHttpClient httpClient;

    public static class Builder {
        private String url;
        private String suffix;
        private File directory;
        private OnFileDownloadListener listener;

        /**
         * 文件下载地址
         */
        public Builder setDownloadFileSource(String url) {
            this.url = url;
            return this;
        }

        /**
         * 文件后缀
         * 如：apk等
         */
        public Builder setFileSuffix(String suffix) {
            if (suffix.contains(".")) {
                //去掉前缀的点
                this.suffix = suffix.substring(1);
            } else {
                this.suffix = suffix;
            }
            return this;
        }

        /**
         * 文件保存的地址
         */
        public Builder setFileSaveDirectory(File directory) {
            this.directory = directory;
            return this;
        }

        /**
         * 设置文件下载回调监听
         */
        public Builder setOnFileDownloadListener(OnFileDownloadListener downloadListener) {
            this.listener = downloadListener;
            return this;
        }

        public FileDownloadManager build() {
            return new FileDownloadManager(this);
        }
    }

    private FileDownloadManager(Builder builder) {
        this.url = builder.url;
        this.suffix = builder.suffix;
        this.directory = builder.directory;
        this.listener = builder.listener;
        this.httpClient = new OkHttpClient();
    }

    /**
     * 开始下载
     */
    public void start() {
        if (TextUtils.isEmpty(url)) {
            listener.onFailure(new IllegalArgumentException("url is empty"));
            return;
        }

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
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @Nullable Response response) {
                if (response == null) {
                    listener.onFailure(new NullPointerException());
                } else {
                    try {
                        ResponseBody responseBody = response.body();
                        if (responseBody != null) {
                            InputStream inputStream = responseBody.byteStream();
                            long fileSize = responseBody.contentLength();

                            File file = new File(directory, System.currentTimeMillis() + "." + suffix);
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            byte[] buffer = new byte[2048];
                            int read;
                            long sum = 0L;
                            while ((read = inputStream.read(buffer)) != -1) {
                                fileOutputStream.write(buffer, 0, read);
                                sum += read;

                                int progress = (int) (sum * 1.0 / fileSize * 100);
                                listener.onProgressChanged(progress);
                            }
                            listener.onDownloadEnd(file);
                            fileOutputStream.flush();
                            //关闭流
                            fileOutputStream.close();
                            inputStream.close();
                        } else {
                            listener.onFailure(new NullPointerException());
                        }
                    } catch (IOException e) {
                        listener.onFailure(e);
                    }
                }
            }
        });
    }

    public interface OnFileDownloadListener {
        void onProgressChanged(long progress);

        void onDownloadEnd(File file);

        void onFailure(Throwable throwable);
    }
}
