package com.pengxh.androidx.lite.utils.socket.web;

import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;

public interface OnWebSocketListener {
    void onOpen(WebSocket webSocket, Response response);

    void onDataReceived(WebSocket webSocket, String message);

    void onDataReceived(WebSocket webSocket, ByteString bytes);

    void onDisconnected(WebSocket webSocket, int code, String reason);

    void onFailure(WebSocket webSocket, Throwable throwable);

    void onMaxRetryReached();
}
