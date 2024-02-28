package com.pengxh.androidx.lite.utils.socket.tcp;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.pengxh.androidx.lite.utils.WeakReferenceHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TcpChannelHandler extends SimpleChannelInboundHandler<byte[]> implements Handler.Callback {

    private static final String TAG = "TcpChannelHandler";
    private final OnTcpMessageCallback messageCallback;
    private final WeakReferenceHandler weakReferenceHandler = new WeakReferenceHandler(this);

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        return true;
    }

    public TcpChannelHandler(OnTcpMessageCallback messageCallback) {
        this.messageCallback = messageCallback;
    }

    private boolean isOnMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        if (isOnMainThread()) {
            messageCallback.onConnectStateChanged(ConnectState.SUCCESS);
        } else {
            weakReferenceHandler.post(new Runnable() {
                @Override
                public void run() {
                    messageCallback.onConnectStateChanged(ConnectState.SUCCESS);
                }
            });
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        if (isOnMainThread()) {
            messageCallback.onConnectStateChanged(ConnectState.CLOSED);
        } else {
            weakReferenceHandler.post(new Runnable() {
                @Override
                public void run() {
                    messageCallback.onConnectStateChanged(ConnectState.CLOSED);
                }
            });
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, byte[] data) {
        if (isOnMainThread()) {
            messageCallback.onReceivedTcpMessage(data);
        } else {
            weakReferenceHandler.post(new Runnable() {
                @Override
                public void run() {
                    messageCallback.onReceivedTcpMessage(data);
                }
            });
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        if (isOnMainThread()) {
            messageCallback.onConnectStateChanged(ConnectState.ERROR);
            ctx.close();
        } else {
            weakReferenceHandler.post(new Runnable() {
                @Override
                public void run() {
                    messageCallback.onConnectStateChanged(ConnectState.ERROR);
                    ctx.close();
                }
            });
        }
    }
}
