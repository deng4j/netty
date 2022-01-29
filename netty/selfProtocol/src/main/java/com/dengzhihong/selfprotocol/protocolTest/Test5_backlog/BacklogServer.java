package com.dengzhihong.selfprotocol.protocolTest.Test5_backlog;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 1.SO_BACKLOG:属于 ServerSocketChannal 参数
 * 2.backlog 大小包括了两个队列的大小:
 *   >sync queue - 半连接队列:
 *      >>在 `syncookies` 启用的情况下，逻辑上没有最大值限制，这个设置便被忽略
 *   >accept queue - 全连接队列:
 *      >>如果 accpet queue 队列满了，server 将发送一个拒绝连接的错误信息到 client
 * 3.在NioEventLoop.class中的493行设置断点，模拟队列满了
 * 4.ulimit -n属于操作系统参数
 * 5.TCP_NODELAY属于 SocketChannal 参数
 * 6.SO_SNDBUF 属于 SocketChannal 参数
 * 7.SO_RCVBUF 既可用于 SocketChannal 参数，也可以用于 ServerSocketChannal 参数（建议设置到 ServerSocketChannal 上）
 *
 */
@Slf4j
public class BacklogServer {

    public static void main(String[] args) {
        new ServerBootstrap()
            .group(new NioEventLoopGroup())
            .option(ChannelOption.SO_BACKLOG, 2)//全连接队列大小为2
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler());
                }
            })
            .bind(8080);
    }
}
