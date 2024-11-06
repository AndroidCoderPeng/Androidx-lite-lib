package com.pengxh.androidx.lite.utils.socket.tcp;

import android.util.Log;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class TcpClient {

    private static final String TAG = "TcpClient";
    private static final long RECONNECT_DELAY = 5L;
    private final Bootstrap bootStrap = new Bootstrap();
    private final NioEventLoopGroup loopGroup = new NioEventLoopGroup();
    private final String host;
    private final int port;
    private final OnTcpConnectStateListener listener;
    private Channel channel;
    private boolean isRunning = false;
    private int retryTimes = 0;

    public TcpClient(String host, int port, OnTcpConnectStateListener listener) {
        this.host = host;
        this.port = port;
        this.listener = listener;
        bootStrap.group(loopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true) //无阻塞
                .option(ChannelOption.SO_KEEPALIVE, true) //长连接
                .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(5000, 5000, 8000))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new SimpleChannelInitializer());
    }

    /**
     * TcpClient 是否正在运行
     */
    public boolean isRunning() {
        return isRunning;
    }

    public void start() {
        if (isRunning) {
            return;
        }
        connect();
    }

    private class SimpleChannelInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline()
                    .addLast(new ByteArrayDecoder())
                    .addLast(new ByteArrayEncoder())
                    .addLast(new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS))//如果连接没有接收或发送数据超过60秒钟就发送一次心跳
                    .addLast(new SimpleChannelInboundHandler<byte[]>() {
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                            Log.d(TAG, address.getAddress().getHostAddress() + " 已连接");
                            listener.onConnected();
                        }

                        @Override
                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                            InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                            Log.d(TAG, address.getAddress().getHostAddress() + " 已断开");
                            listener.onDisconnected();
                            reconnect();
                        }

                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
                            listener.onMessageReceived(msg);
                        }

                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            Log.d(TAG, "exceptionCaught: " + cause.getMessage());
                            listener.onConnectFailed();
                            ctx.close();
                        }
                    });
        }
    }

    private synchronized void connect() {
        if (channel != null && channel.isActive()) {
            return;
        }
        new Thread(() -> {
            try {
                ChannelFuture channelFuture = bootStrap.connect(host, port).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            isRunning = true;
                            retryTimes = 0;
                            channel = future.channel();
                        }
                    }
                }).sync();
                channelFuture.channel().closeFuture().sync();
            } catch (Exception e) {
                Log.d(TAG, "连接失败: " + e.getMessage());
                reconnect();
            }
        }).start();
    }

    private void reconnect() {
        retryTimes++;
        Log.d(TAG, "开始第 " + retryTimes + " 次重连");
        loopGroup.schedule(this::connect, RECONNECT_DELAY, TimeUnit.SECONDS);
    }

    public void stop() {
        isRunning = false;
        channel.close();
    }

    public void sendMessage(byte[] bytes) {
        if (!isRunning) {
            return;
        }
        channel.writeAndFlush(bytes);
    }
}
