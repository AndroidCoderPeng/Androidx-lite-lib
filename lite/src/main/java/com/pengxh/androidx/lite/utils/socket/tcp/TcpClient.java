package com.pengxh.androidx.lite.utils.socket.tcp;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;

import com.pengxh.androidx.lite.utils.WeakReferenceHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class TcpClient implements Handler.Callback {

    private static final String TAG = "TcpClient";
    private String host = "";
    private int port = 0;
    private boolean isConnected = false;
    private final NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    private final Bootstrap bootstrap = new Bootstrap();
    private Channel channel;
    private final OnTcpMessageCallback messageCallback;

    /*************************************/
    //默认重连3次，每次间隔10s
    private int retryTimes = 3;
    private long retryInterval = 5 * 1000L;

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public void setRetryInterval(long retryInterval) {
        this.retryInterval = retryInterval;
    }

    private final WeakReferenceHandler weakReferenceHandler = new WeakReferenceHandler(this);

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        return true;
    }

    public void connectServer(String host, int port) {
        this.host = host;
        this.port = port;
        if (!isConnected) {
            /**
             * 使用线程连接TCP Server
             * */
            connect();
        } else {
            release();
        }
    }

    /*************************************/

    public TcpClient(OnTcpMessageCallback messageCallback) {
        this.messageCallback = messageCallback;
    }

    public void connect() {
        Log.d(TAG, "connect: connect: 连接TCP服务器");
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true) //无阻塞
                .option(ChannelOption.SO_KEEPALIVE, true) //长连接
                .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(5000, 5000, 8000))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        /**
                         * 参数3：将在未执行读取或写入时触发超时回调，0代表不处理;
                         *
                         * 读超时尽量设置大于写超时，代表多次写超时时写心跳包，多次写了心跳数据仍然读超时代表当前连接错误，即可断开连接重新连接
                         * */
                        ch.pipeline().addLast(new IdleStateHandler(60, 10, 0))
                                .addLast(new ByteArrayDecoder())
                                .addLast(new ByteArrayEncoder())
                                .addLast(new TcpChannelHandler(messageCallback));
                    }
                });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ChannelFuture channelFuture = bootstrap.connect(host, port).addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture future) {
                                    isConnected = future.isSuccess();
                                    if (isConnected) {
                                        channel = future.channel();
                                    }
                                }
                            })
                            .sync();
                    // 等待连接关闭
                    channelFuture.channel().closeFuture().sync();
                } catch (Exception e) {
                    e.printStackTrace();
                    weakReferenceHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            eventLoopGroup.shutdownGracefully();
                            isConnected = false;

                            retryConnect();
                        }
                    });
                }
            }
        }).start();
    }

    //重新连接
    private void retryConnect() {
        if (isConnected) {
            return;
        }
        if (retryTimes > 0) {
            retryTimes--;
            SystemClock.sleep(retryInterval);
            Log.d(TAG, "retryConnect ===> retryTimes = $retryTimes");
            connect();
        }
    }

    public void sendMessage(byte[] bytes) {
        if (!isConnected) {
            return;
        }
        channel.writeAndFlush(bytes).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    future.channel().close();
                    eventLoopGroup.shutdownGracefully();
                }
            }
        });
    }

    public void release(){
        eventLoopGroup.shutdownGracefully();
        isConnected = false;
        retryTimes = 0;
    }
}
