package com.dengzhihong.netty.netty.n3_channel.channelFuture2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * 多线程解决的是提高cup和io操作的利用率。
 * 异步并没有缩短响应时间，反而有所增加。他提高的是单位时间内的吞吐量。
 */
@Slf4j
public class CloseFutureClient {

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
            .group(group)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception { //连接建立后才调用
                    ch.pipeline().addLast(new StringEncoder());
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                }
            })
            .connect(new InetSocketAddress("localhost", 8080));
        Channel channel =channelFuture.sync().channel();

        new Thread(()->{
            Scanner scanner = new Scanner(System.in);
            while (true){
                String line = scanner.nextLine();
                if ("q".equals(line)){
                    channel.close();
                    System.err.println("不能在这执行关闭Future后的操作，因为close()操作另一个线程执行是异步的");
                    break;
                }
                channel.writeAndFlush(line);
            }
        },"input").start();
        //获取CloseFuture对象，优雅的关闭ChannelFuture
        ChannelFuture closeFuture = channel.closeFuture();

        addListener(closeFuture,group);
    }


    /**
     *处理关闭后的操作二：使用异步监听器
     */
    private static void addListener(ChannelFuture closeFuture, NioEventLoopGroup group) {
        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                log.info("处理关闭后的操作，nio线程执行");
                //优雅地关闭事件循环组，会先拒绝新任务，等待正在执行的任务执行完毕
                group.shutdownGracefully();
            }
        });
    }

    /**
     *处理关闭后的操作一：使用同步等待
     */
    private static void sync(ChannelFuture closeFuture) throws InterruptedException {
        System.out.println("waiting close...");
        closeFuture.sync();
        log.info("处理关闭后的操作，主线程执行");
    }
}
