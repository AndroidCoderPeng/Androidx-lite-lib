package com.pengxh.androidx.lite.utils.socket.web;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketClient {
    private static final String TAG = "WebSocketClient";
    private static final long RECONNECT_DELAY_MS = 5000L;
    private static final int MAX_RETRY_TIMES = 10; // 设置最大重连次数
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicInteger retryTimes = new AtomicInteger(0);
    private final ReentrantLock lock = new ReentrantLock();
    private final OnWebSocketListener listener;
    private final OkHttpClient httpClient;
    private WebSocket webSocket;
    private String url;

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

    public void start(String url) {
        if (url.isEmpty() || !url.startsWith("ws://") && !url.startsWith("wss://")) {
            Log.e(TAG, "Invalid URL: " + url);
            listener.onFailure(null, null);
            return;
        }
        this.url = url;
        if (isRunning.get()) {
            return;
        }
        connect();
    }

    public void connect() {
        lock.lock();
        try {
            Log.d(TAG, "connect: " + url);
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
                    /**
                     * APP主动断开，code = 1000
                     * 服务器主动断开，code = 1001
                     * <p>
                     * APP主动断开，onClosing和onClosed都会走
                     * 服务器主动断开，只走onClosing
                     * */
                    if (code == 1001) {
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
        } finally {
            lock.unlock();
        }
    }

    private void reconnect() {
        new Thread(() -> {
            if (retryTimes.get() <= MAX_RETRY_TIMES) {
                int currentRetryTimes = retryTimes.incrementAndGet();
                Log.d(TAG, "开始第 " + currentRetryTimes + "次重连");
                try {
                    Thread.sleep(RECONNECT_DELAY_MS);
                    connect();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Log.e(TAG, "达到最大重连次数，停止重连");
                listener.onMaxRetryReached();
            }
        }).start();
    }

    public void stop() {
        if (webSocket != null) {
            Log.d(TAG, url + " 断开连接");
            webSocket.close(1000, "Application Request Close");
            isRunning.set(false);
            webSocket = null;
        }
    }
}
