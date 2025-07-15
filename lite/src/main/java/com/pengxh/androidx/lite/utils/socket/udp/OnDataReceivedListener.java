package com.pengxh.androidx.lite.utils.socket.udp;

public interface OnDataReceivedListener {
    /**
     * 当接收到系统消息
     */
    void onReceivedData(byte[] data);
}
