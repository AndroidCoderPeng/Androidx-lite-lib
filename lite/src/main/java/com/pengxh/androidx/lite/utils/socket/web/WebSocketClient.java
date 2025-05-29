package com.pengxh.androidx.lite.utils.socket.web;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketClient {
    private static final String TAG = "WebSocketClient";
    private static final int MAX_RETRY_TIMES = 10;
    private static final long RECONNECT_DELAY_SECONDS = 15L;
    private static final int NORMAL_CLOSE = 1000;
    private static final int SERVER_CLOSE = 1001;
    private final OnWebSocketListener listener;
    private final OkHttpClient httpClient;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private String url;
    private WebSocket webSocket;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicInteger retryTimes = new AtomicInteger(0);

    public WebSocketClient(OnWebSocketListener listener) {
        this.listener = listener;
        this.httpClient = new OkHttpClient.Builder().readTimeout(0, TimeUnit.MILLISECONDS).build();
    }

    /**
     * WebSocketClient 是否正在运行
     */
    public boolean isRunning() {
        return isRunning.get();
    }

    private boolean isValidWebSocketUrl(String url) {
        return url != null && !url.isEmpty() && (url.startsWith("ws://") || url.startsWith("wss://"));
    }

    public void start(String url) {
        this.url = url;
        connect();
    }

    public void start() {
        connect();
    }

    public void connect() {
        if (!isValidWebSocketUrl(url)) {
            Log.e(TAG, "Invalid URL: " + url);
            return;
        }

        if (isRunning()) {
            Log.d(TAG, "connect: WebSocketClient 正在运行");
            return;
        }

        try {
            Log.d(TAG, "开始连接: " + url);
            Request request = new Request.Builder().url(url).build();
            webSocket = httpClient.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                    super.onOpen(webSocket, response);
                    listener.onOpen(webSocket, response);
                    isRunning.set(true);
                    retryTimes.set(0);
                }

                @Override
                public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                    super.onMessage(webSocket, text);
                    listener.onMessageResponse(webSocket, text);
                }

                @Override
                public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
                    super.onMessage(webSocket, bytes);
                    listener.onMessageResponse(webSocket, bytes);
                }

                @Override
                public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                    super.onClosing(webSocket, code, reason);
                    listener.onServerDisconnected(webSocket, code, reason);
                    if (code == SERVER_CLOSE) {
                        Log.d(TAG, code + ", " + reason);
                        reconnect();
                    }
                }

                //无论是由于服务端主动断开连接还是由于其他原因，都会走这个回调
                @Override
                public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                    super.onClosed(webSocket, code, reason);
                    Log.d(TAG, code + ", " + reason);
                    listener.onClientDisconnected(webSocket, code, reason);
                    reconnect();
                }

                @Override
                public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
                    super.onFailure(webSocket, t, response);
                    Log.d(TAG, "onFailure: " + t.getMessage());
                    listener.onFailure(webSocket, t);
                    reconnect();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "WebSocket 连接异常", e);
            listener.onFailure(null, e);
            reconnect();
        }
    }

    private void reconnect() {
        if (retryTimes.get() >= MAX_RETRY_TIMES) {
            Log.e(TAG, "达到最大重连次数，停止重连");
            runOnUiThread(listener::onMaxRetryReached);
            return;
        }

        int currentRetry = retryTimes.incrementAndGet();
        Log.d(TAG, "开始第 " + currentRetry + " 次重连");

        executor.submit(() -> {
            try {
                Thread.sleep(RECONNECT_DELAY_SECONDS);
                runOnUiThread(this::connect);
            } catch (InterruptedException ignored) {
            }
        });
    }

    public void stop() {
        Log.d(TAG, url + " 断开连接");
        if (webSocket != null) {
            try {
                webSocket.close(NORMAL_CLOSE, "Application Request Close");
            } catch (Exception ignored) {
            }
        }
        isRunning.set(false);
    }

    private void runOnUiThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            mainHandler.post(runnable);
        }
    }
}
