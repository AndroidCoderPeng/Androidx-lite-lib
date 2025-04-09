package com.pengxh.androidx.lite.utils.socket.tcp;

import android.util.Log;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
    private static final long RECONNECT_DELAY = 15L;
    private static final int MAX_RETRY_TIMES = 10; // 设置最大重连次数
    private final NioEventLoopGroup loopGroup = new NioEventLoopGroup();
    private final OnTcpConnectStateListener listener;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicInteger retryTimes = new AtomicInteger(0);
    private String host;
    private int port;
    private Channel channel;
    private boolean needReconnect = true;

    public TcpClient(OnTcpConnectStateListener listener) {
        this.listener = listener;
    }

    /**
     * TcpClient 是否正在运行
     */
    public boolean isRunning() {
        return isRunning.get();
    }

    public void start(String host, int port) {
        this.host = host;
        this.port = port;
        if (isRunning.get()) {
            Log.d(TAG, "start: TcpClient 正在运行");
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
                    .addLast(new IdleStateHandler(15, 15, 60, TimeUnit.SECONDS))//如果连接没有接收或发送数据超过60秒钟就发送一次心跳
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
                            if (needReconnect) {
                                reconnect();
                            }
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
                            isRunning.set(false);
                        }
                    });
        }
    }

    private synchronized void connect() {
        if (channel != null && channel.isActive()) {
            Log.d(TAG, "connect: TcpClient 正在运行");
            return;
        }
        new Thread(() -> {
            try {
                Log.d(TAG, "start connect: " + host + ":" + port);
                Bootstrap bootStrap = new Bootstrap();
                bootStrap.group(loopGroup)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY, true) //无阻塞
                        .option(ChannelOption.SO_KEEPALIVE, true) //长连接
                        .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(5000, 5000, 8000))
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                        .handler(new SimpleChannelInitializer());
                ChannelFuture channelFuture = bootStrap.connect(host, port).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            isRunning.set(true);
                            retryTimes.set(0);
                            channel = future.channel();
                        }
                    }
                }).sync();
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                reconnect();
            }
        }).start();
    }

    private void reconnect() {
        int currentRetryTimes = retryTimes.incrementAndGet();
        if (currentRetryTimes <= MAX_RETRY_TIMES) {
            Log.w(TAG, "开始第 " + currentRetryTimes + " 次重连");
            loopGroup.schedule(this::connect, RECONNECT_DELAY, TimeUnit.SECONDS);
        } else {
            Log.e(TAG, "达到最大重连次数，停止重连");
            listener.onConnectFailed();
        }
    }

    public void stop(boolean needReconnect) {
        this.needReconnect = needReconnect;
        isRunning.set(false);
        channel.close();
    }

    public void sendMessage(byte[] bytes) {
        if (!isRunning.get()) {
            return;
        }
        channel.writeAndFlush(bytes);
    }
}
