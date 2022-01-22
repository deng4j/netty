package com.dengzhihong.netty.netty.n3_channel;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class ChannelFutureClient {

    public static void main(String[] args) throws InterruptedException {
        //带有future、promise的类型，都是和异步方法配套使用的，处理结果
        ChannelFuture channelFuture = new Bootstrap().group(new NioEventLoopGroup())
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception { //连接建立后才调用
                    ch.pipeline().addLast(new StringEncoder());
                }
            })
            //异步非阻塞方法，主线程发起调用，真正执行connect()的是nio线程
            .connect(new InetSocketAddress("localhost", 8080));

        addListener(channelFuture);
    }

    /**
     * 处理channelFuture方式一
     */
    private static void sync(ChannelFuture channelFuture) throws InterruptedException {
        //sync方法同步处理结果，等待连接建立才能操作channel
        channelFuture.sync();
        Channel channel = channelFuture.channel();
        channel.writeAndFlush("你好，世界!");
    }

    /**
     * 处理channelFuture方式二
     */
    private static void addListener(ChannelFuture channelFuture) {
        channelFuture.addListener(new ChannelFutureListener() {
            //在nio线程连接建立后会回调operationComplete()方法
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Channel channel = future.channel();
                channel.writeAndFlush("你好，java");
                log.info("{}",channel);
            }
        });
    }

}
