package com.pengxh.androidx.lite.utils.socket.tcp;

import android.util.Log;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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
    private static final long INITIAL_IDLE_TIME = 15L;
    private static final int MAX_RETRY_TIMES = 10;
    private static final long RECONNECT_DELAY_SECONDS = 15L;
    private static final int RECEIVE_BUFFER_MIN = 5000;
    private static final int RECEIVE_BUFFER_MAX = 8000;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final NioEventLoopGroup loopGroup = new NioEventLoopGroup();
    private final OnStateChangedListener listener;
    private String host = "";
    private int port = 0;
    private boolean needReconnect = false;
    private Channel channel = null;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicInteger retryTimes = new AtomicInteger(0);

    public TcpClient(OnStateChangedListener listener) {
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
        connect();
    }

    public void start() {
        connect();
    }

    private class SimpleChannelInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) {
            ch.pipeline().addLast(new ByteArrayDecoder());
            ch.pipeline().addLast(new ByteArrayEncoder());
            ch.pipeline().addLast(new IdleStateHandler(INITIAL_IDLE_TIME, INITIAL_IDLE_TIME, 60, TimeUnit.SECONDS));
            ch.pipeline().addLast(new SimpleChannelInboundHandler<byte[]>() {
                @Override
                public void channelActive(ChannelHandlerContext ctx) {
                    InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                    Log.d(TAG, address.getAddress().getHostAddress() + " 已连接");
                    listener.onConnected();
                }

                @Override
                public void channelInactive(ChannelHandlerContext ctx) {
                    InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                    Log.d(TAG, address.getAddress().getHostAddress() + " 已断开");
                    listener.onDisconnected();
                    if (needReconnect) {
                        reconnect();
                    }
                }

                @Override
                protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) {
                    listener.onReceivedData(msg);
                }

                @Override
                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                    Log.d(TAG, "exceptionCaught: " + cause.getMessage());
                    listener.onConnectFailed();
                    ctx.close();
                    isRunning.set(false);
                }
            });
        }
    }

    private synchronized void connect() {
        if (isRunning()) {
            Log.d(TAG, "start: TcpClient 正在运行");
            return;
        }
        new Thread(() -> {
            try {
                Log.d(TAG, "开始连接: " + host + ":" + port);
                Bootstrap bootstrap = createBootstrap();
                ChannelFuture channelFuture = bootstrap.connect(host, port).addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        isRunning.set(true);
                        retryTimes.set(0);
                        channel = future.channel();
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

    private Bootstrap createBootstrap() {
        return new Bootstrap()
                .group(loopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.RCVBUF_ALLOCATOR,
                        new AdaptiveRecvByteBufAllocator(RECEIVE_BUFFER_MIN, RECEIVE_BUFFER_MIN, RECEIVE_BUFFER_MAX))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new SimpleChannelInitializer());
    }

    private void reconnect() {
        int currentRetryTimes = retryTimes.incrementAndGet();
        if (currentRetryTimes <= MAX_RETRY_TIMES) {
            Log.d(TAG, "开始第 " + currentRetryTimes + " 次重连");
            scheduler.schedule(this::connect, RECONNECT_DELAY_SECONDS, TimeUnit.SECONDS);
        } else {
            Log.e(TAG, "达到最大重连次数，停止重连");
            listener.onConnectFailed();
        }
    }

    public void stop(boolean needReconnect) {
        this.needReconnect = needReconnect;
        isRunning.set(false);
        if (channel != null && channel.isOpen()) {
            channel.close();
        }
    }

    public void send(Object msg) {
        if (!isRunning() || channel == null) return;
        if (msg instanceof String) {
            channel.writeAndFlush(((String) msg).getBytes(StandardCharsets.UTF_8));
        } else if (msg instanceof byte[]) {
            channel.writeAndFlush(msg);
        } else {
            throw new IllegalArgumentException("msg must be String or byte[]");
        }
    }
}
