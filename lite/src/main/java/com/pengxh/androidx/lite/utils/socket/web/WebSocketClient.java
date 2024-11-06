package com.pengxh.androidx.lite.utils.socket.web;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketClient {
    private static final String TAG = "WebSocketClient";
    private static final long RECONNECT_DELAY_MS = 5000;
    private final OnWebSocketListener listener;
    private final OkHttpClient httpClient;
    private WebSocket webSocket;
    private String url;
    private boolean isRunning = false;
    private int retryTimes = 0;

    public WebSocketClient(OnWebSocketListener listener) {
        this.listener = listener;
        this.httpClient = new OkHttpClient.Builder().readTimeout(0, TimeUnit.MILLISECONDS).build();
    }

    public void start(String url) {
        this.url = url;
        if (isRunning) {
            return;
        }
        connect();
    }

    public void connect() {
        Log.d(TAG, "connect: " + url);
        Request request = new Request.Builder().url(url).build();
        webSocket = httpClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                super.onOpen(webSocket, response);
                listener.onOpen(webSocket, response);
                isRunning = true;
                retryTimes = 0;
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
                listener.onFailure(webSocket, t, response);
                reconnect();
            }
        });
    }

    private void reconnect() {
        isRunning = false;
        new Thread(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(RECONNECT_DELAY_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            retryTimes++;
            Log.d(TAG, "开始第 " + retryTimes + " 次重连");
            connect();
        }).start();
    }

    public void stop() {
        if (webSocket != null) {
            Log.d(TAG, url + " 断开连接");
            webSocket.close(1000, "Application Request Close");
            isRunning = false;
            webSocket = null;
        }
    }
}
