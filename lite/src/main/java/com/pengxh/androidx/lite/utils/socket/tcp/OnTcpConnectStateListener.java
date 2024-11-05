package com.pengxh.androidx.lite.utils.socket.tcp;

public interface OnTcpConnectStateListener {
    void onConnected();

    void onDisconnected();

    void onConnectFailed();

    void onMessageReceived(byte[] bytes);
}
