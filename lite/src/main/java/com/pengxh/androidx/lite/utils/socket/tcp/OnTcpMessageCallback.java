package com.pengxh.androidx.lite.utils.socket.tcp;

public interface OnTcpMessageCallback {
    /**
     * 当接收到系统消息
     */
    void onReceivedTcpMessage(byte[] data);

    /**
     * 当连接状态发生变化时调用
     */
    void onConnectStateChanged(ConnectState state);
}
