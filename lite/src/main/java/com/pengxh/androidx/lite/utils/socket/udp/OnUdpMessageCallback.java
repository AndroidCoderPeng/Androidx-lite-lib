package com.pengxh.androidx.lite.utils.socket.udp;

public interface OnUdpMessageCallback {
    /**
     * 当接收到系统消息
     */
    void onReceivedUdpMessage(byte[] data);
}
