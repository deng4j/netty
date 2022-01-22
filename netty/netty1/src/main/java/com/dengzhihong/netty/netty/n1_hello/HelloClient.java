package com.dengzhihong.netty.netty.n1_hello;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

/**
 * channel 的主要作用:
 *  1.close() 可以用来关闭 channel
 *  2.closeFuture() 用来处理 channel 的关闭
 *   > sync 方法作用是同步等待 channel 关闭
 *   > 而 addListener 方法是异步等待 channel 关闭
 *  3.pipeline() 方法添加处理器
 *  4.write() 方法将数据写入
 *  5.flush()将客户端数据刷出去
 *  6.writeAndFlush() 方法将数据写入并刷出
 */
public class HelloClient {

    public static void main(String[] args) throws InterruptedException {
        new Bootstrap()
            .group(new NioEventLoopGroup())
            .channel(NioSocketChannel.class)
            .handler(
                new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception { //连接建立后才调用
                    ch.pipeline().addLast(new StringEncoder());
                }
            })
            .connect(new InetSocketAddress("localhost",8080))
            .sync() //一个阻塞方法，直到连接建立后才能往下运行
            .channel()//代表连接对象，这里调用了它的写方法
            .writeAndFlush("你好，世界!");
    }
}
