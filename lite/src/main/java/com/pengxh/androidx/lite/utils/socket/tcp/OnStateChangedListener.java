package com.pengxh.androidx.lite.utils.socket.tcp;

public interface OnStateChangedListener {
    void onConnected();

    void onDisconnected();

    void onConnectFailed();

    void onReceivedData(byte[] bytes);
}
