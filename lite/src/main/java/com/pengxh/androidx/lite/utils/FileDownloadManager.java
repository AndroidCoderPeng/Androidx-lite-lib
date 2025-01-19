package com.pengxh.androidx.lite.utils;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class FileDownloadManager implements Handler.Callback {
    private static final String TAG = "FileDownloadManager";
    private final String url;
    private final String suffix;
    private final File directory;
    private final OnFileDownloadListener listener;
    private final OkHttpClient httpClient;
    private final WeakReferenceHandler weakReferenceHandler;

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
        this.weakReferenceHandler = new WeakReferenceHandler(this);
    }

    /**
     * 开始下载
     */
    public void start() {
        Request request = new Request.Builder().get().url(url).build();
        Call newCall = httpClient.newCall(request);
        AtomicBoolean isExecuting = new AtomicBoolean(false);

        /**
         * 如果已被加入下载队列，则取消之前的，重新下载
         */
        if (isExecuting.getAndSet(true)) {
            newCall.cancel();
        }

        newCall.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                weakReferenceHandler.sendEmptyMessage(DOWNLOAD_FAILED_CODE);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                ResponseBody body = response.body();
                if (body == null) {
                    weakReferenceHandler.sendEmptyMessage(DOWNLOAD_FAILED_CODE);
                    return;
                }

                InputStream inputStream = body.byteStream();
                long fileSize = body.contentLength();
                if (fileSize <= 0) {
                    weakReferenceHandler.sendEmptyMessage(DOWNLOAD_FAILED_CODE);
                    return;
                }

                Message message = weakReferenceHandler.obtainMessage();
                message.what = DOWNLOAD_START_CODE;
                message.obj = fileSize;
                weakReferenceHandler.sendMessage(message);

                File file = new File(directory, System.currentTimeMillis() + "." + suffix);
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    byte[] buffer = new byte[2048];
                    long sum = 0L;
                    int read;
                    while ((read = inputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, read);
                        sum += read;
                        Message msg = weakReferenceHandler.obtainMessage();
                        msg.what = PROGRESS_CHANGED_CODE;
                        msg.obj = sum;
                        weakReferenceHandler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    weakReferenceHandler.sendEmptyMessage(DOWNLOAD_FAILED_CODE);
                }

                Message fileMessage = weakReferenceHandler.obtainMessage();
                fileMessage.what = DOWNLOAD_END_CODE;
                fileMessage.obj = file;
                weakReferenceHandler.sendMessage(fileMessage);
            }
        });
    }

    private final int DOWNLOAD_START_CODE = 1;
    private final int PROGRESS_CHANGED_CODE = 2;
    private final int DOWNLOAD_END_CODE = 3;
    private final int DOWNLOAD_FAILED_CODE = 4;

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case DOWNLOAD_START_CODE:
                listener.onDownloadStart((long) msg.obj);
                break;
            case PROGRESS_CHANGED_CODE:
                listener.onProgressChanged((long) msg.obj);
                break;
            case DOWNLOAD_END_CODE:
                listener.onDownloadEnd((File) msg.obj);
                break;
            case DOWNLOAD_FAILED_CODE:
                listener.onDownloadFailed(new Exception("下载失败"));
                break;
        }
        return true;
    }

    public interface OnFileDownloadListener {
        void onDownloadStart(long total);

        void onProgressChanged(long progress);

        void onDownloadEnd(File file);

        void onDownloadFailed(Throwable throwable);
    }
}
