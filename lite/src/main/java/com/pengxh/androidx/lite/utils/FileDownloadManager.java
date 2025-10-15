package com.pengxh.androidx.lite.utils;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class FileDownloadManager {
    private final String mUrl;
    private final String mSuffix;
    private final File mDirectory;
    private final OkHttpClient mHttpClient;

    public static class Builder {
        private String mUrl;
        private String mSuffix;
        private File mDirectory;

        /**
         * 文件下载地址
         */
        public Builder setDownloadFileSource(String url) {
            this.mUrl = url;
            return this;
        }

        /**
         * 文件后缀
         * 如：apk等
         */
        public Builder setFileSuffix(String suffix) {
            if (suffix.contains(".")) {
                //去掉前缀的点
                this.mSuffix = suffix.substring(1);
            } else {
                this.mSuffix = suffix;
            }
            return this;
        }

        /**
         * 文件保存的地址
         */
        public Builder setFileSaveDirectory(File directory) {
            this.mDirectory = directory;
            return this;
        }

        public FileDownloadManager build() {
            return new FileDownloadManager(this);
        }
    }

    private FileDownloadManager(Builder builder) {
        this.mUrl = builder.mUrl;
        this.mSuffix = builder.mSuffix;
        this.mDirectory = builder.mDirectory;
        this.mHttpClient = new OkHttpClient();
    }

    /**
     * 开始下载
     */
    private Flowable<DownloadStatus> start() {
        return Flowable.create(new FlowableOnSubscribe<DownloadStatus>() {
            @Override
            public void subscribe(FlowableEmitter<DownloadStatus> emitter) {
                try {
                    Request request = new Request.Builder().url(mUrl).build();
                    Call call = mHttpClient.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            if (!emitter.isCancelled()) {
                                emitter.onError(e);
                            }
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (emitter.isCancelled()) return;

                            ResponseBody body = response.body();
                            if (body == null) {
                                emitter.onError(new Exception("ResponseBody is null"));
                                return;
                            }

                            InputStream inputStream = body.byteStream();
                            long fileSize = body.contentLength();

                            if (fileSize <= 0) {
                                emitter.onError(new Exception("Invalid file size"));
                                return;
                            }

                            emitter.onNext(new DownloadStatus(DownloadStatus.Status.STARTED, fileSize, 0, null));

                            File file = new File(mDirectory, System.currentTimeMillis() + "." + mSuffix);
                            FileOutputStream fos = new FileOutputStream(file);

                            byte[] buffer = new byte[2048];
                            long sum = 0L;
                            int read;
                            try {
                                while ((read = inputStream.read(buffer)) != -1 && !emitter.isCancelled()) {
                                    fos.write(buffer, 0, read);
                                    sum += read;

                                    long currentTime = System.currentTimeMillis();
                                    // 限制进度更新频率，避免背压问题
                                    emitter.onNext(new DownloadStatus(DownloadStatus.Status.PROGRESSING, fileSize, sum, null));
                                }

                                fos.close();
                                inputStream.close();

                                if (!emitter.isCancelled()) {
                                    // 确保最后发送一次完成状态
                                    emitter.onNext(new DownloadStatus(DownloadStatus.Status.PROGRESSING, fileSize, sum, null));
                                    emitter.onNext(new DownloadStatus(DownloadStatus.Status.COMPLETED, fileSize, sum, file));
                                    emitter.onComplete();
                                }
                            } catch (IOException e) {
                                if (!emitter.isCancelled()) {
                                    emitter.onError(e);
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    if (!emitter.isCancelled()) {
                        emitter.onError(e);
                    }
                }
            }
        }, BackpressureStrategy.LATEST).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static class DownloadStatus {
        public enum Status {STARTED, PROGRESSING, COMPLETED, FAILED}

        private final Status status;
        private final long totalBytes;
        private final long downloadedBytes;
        private final File file;
        private final Throwable error;

        DownloadStatus(Status status, long totalBytes, long downloadedBytes, File file) {
            this(status, totalBytes, downloadedBytes, file, null);
        }

        DownloadStatus(Status status, long totalBytes, long downloadedBytes, File file, Throwable error) {
            this.status = status;
            this.totalBytes = totalBytes;
            this.downloadedBytes = downloadedBytes;
            this.file = file;
            this.error = error;
        }

        public Status getStatus() {
            return status;
        }

        public long getTotalBytes() {
            return totalBytes;
        }

        public long getDownloadedBytes() {
            return downloadedBytes;
        }

        public File getFile() {
            return file;
        }

        public Throwable getError() {
            return error;
        }
    }

    /**
     * 订阅下载事件
     */
    public Disposable subscribe(Consumer<DownloadStatus> onNext, Consumer<Throwable> onError, Action onComplete) {
        return start().subscribe(onNext, onError, onComplete);
    }
}
