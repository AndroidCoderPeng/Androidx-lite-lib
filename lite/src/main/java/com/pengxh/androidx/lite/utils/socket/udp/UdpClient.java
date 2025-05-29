package com.pengxh.androidx.lite.utils.socket.udp;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

public class UdpClient {

    private static final int RECEIVE_BUFFER_SIZE = 1024;
    private static final int SEND_BUFFER_SIZE = 1024;
    private final NioEventLoopGroup loopGroup = new NioEventLoopGroup();
    private final OnUdpMessageListener listener;
    private InetSocketAddress socketAddress;
    private Channel channel = null;

    public UdpClient(OnUdpMessageListener listener) {
        this.listener = listener;
    }

    private class SimpleChannelInitializer extends ChannelInitializer<DatagramChannel> {

        @Override
        protected void initChannel(DatagramChannel dc) throws Exception {
            dc.pipeline().addLast(new SimpleChannelInboundHandler<DatagramPacket>() {
                @Override
                protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
                    ByteBuf byteBuf = msg.content();
                    byte[] bytes = new byte[byteBuf.readableBytes()];
                    byteBuf.readBytes(bytes);
                    listener.onReceivedUdpMessage(bytes);
                }
            });
        }
    }

    public void bind(String remote, int port) {
        this.socketAddress = new InetSocketAddress(remote, port);
        new Thread(() -> {
            try {
                Bootstrap bootstrap = createBootstrap();
                ChannelFuture channelFuture = bootstrap.bind(port).addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        channel = future.channel();
                    }
                }).sync();
                channelFuture.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
                loopGroup.shutdownGracefully();
            }
        }).start();
    }

    private Bootstrap createBootstrap() {
        return new Bootstrap()
                .group(loopGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_RCVBUF, RECEIVE_BUFFER_SIZE)
                .option(ChannelOption.SO_SNDBUF, SEND_BUFFER_SIZE)
                .handler(new SimpleChannelInitializer());
    }

    public void sendMessage(String value) {
        ByteBuf byteBuf = Unpooled.copiedBuffer(value, CharsetUtil.UTF_8);
        DatagramPacket datagramPacket = new DatagramPacket(byteBuf, socketAddress);
        channel.writeAndFlush(datagramPacket);
    }

    public void sendMessage(byte[] value) {
        DatagramPacket datagramPacket = new DatagramPacket(Unpooled.copiedBuffer(value), socketAddress);
        channel.writeAndFlush(datagramPacket);
    }

    public void sendMessage(ByteBuf value) {
        DatagramPacket datagramPacket = new DatagramPacket(Unpooled.copiedBuffer(value), socketAddress);
        channel.writeAndFlush(datagramPacket);
    }
}
