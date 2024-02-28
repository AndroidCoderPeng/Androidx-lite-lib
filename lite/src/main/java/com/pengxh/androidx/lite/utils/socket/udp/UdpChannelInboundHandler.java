package com.pengxh.androidx.lite.utils.socket.udp;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.pengxh.androidx.lite.utils.WeakReferenceHandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

public class UdpChannelInboundHandler extends SimpleChannelInboundHandler<DatagramPacket> implements Handler.Callback {

    private static final String TAG = "UdpChannelInboundHandler";
    private final OnUdpMessageCallback messageCallback;
    private final WeakReferenceHandler weakReferenceHandler = new WeakReferenceHandler(this);

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        return true;
    }

    public UdpChannelInboundHandler(OnUdpMessageCallback messageCallback) {
        this.messageCallback = messageCallback;
    }

    private boolean isOnMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ctx.channel();
        Log.d(TAG, "channelInactive: 连接关闭");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket datagramPacket) throws Exception {
        ByteBuf byteBuf = datagramPacket.content();
        byte[] byteArray = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(byteArray);
        if (isOnMainThread()) {
            messageCallback.onReceivedUdpMessage(byteArray);
        } else {
            weakReferenceHandler.post(new Runnable() {
                @Override
                public void run() {
                    messageCallback.onReceivedUdpMessage(byteArray);
                }
            });
        }
    }
}
