package com.pengxh.androidx.lite.utils.socket.udp;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

public class UdpClient {

    private final Bootstrap bootStrap = new Bootstrap();
    private final NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    private final OnUdpMessageListener listener;
    private InetSocketAddress socketAddress;
    private Channel channel;

    public UdpClient(OnUdpMessageListener listener) {
        this.listener = listener;
        bootStrap.group(eventLoopGroup);
        bootStrap.channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_RCVBUF, 1024)
                .option(ChannelOption.SO_SNDBUF, 1024)
                .handler(new SimpleChannelInitializer());
    }

    private class SimpleChannelInitializer extends ChannelInitializer<DatagramChannel> {

        @Override
        protected void initChannel(DatagramChannel dc) throws Exception {
            ChannelPipeline pipeline = dc.pipeline();
            pipeline.addLast(new IdleStateHandler(60, 10, 0))
                    .addLast(new SimpleChannelInboundHandler<DatagramPacket>() {
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
                ChannelFuture channelFuture = bootStrap.bind(port).sync();
                channel = channelFuture.channel();
                channel.closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
                release();
            }
        }).start();
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

    public void release() {
        eventLoopGroup.shutdownGracefully();
    }
}
