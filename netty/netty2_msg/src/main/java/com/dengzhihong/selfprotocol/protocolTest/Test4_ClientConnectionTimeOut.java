package com.dengzhihong.selfprotocol.protocolTest;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * 客户端通过.option(参数名称,值)方法给SocketChannel配置参数
 * 服务端的.option()是给ServerSocketChannel配置参数
 * 服务端的.childOption()是给SocketChannel配置参数
 *
 * 参数调优：
 * 1. .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,3000)//3000ms未建立连接，抛异常
 * 2.SO_TIMEOUT 主要用在阻塞 IO，阻塞 IO 中 accept，read 等都是无限等待的，如果不希望永远阻塞，使用它调整超时时间
 *
 */
@Slf4j
public class Test4_ClientConnectionTimeOut {

    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)//3000ms未建立连接，抛异常
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {

                    }
                });
            ChannelFuture future = bootstrap.connect("127.0.0.1", 8080);

            future.sync().channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("timeout");
        }finally {
            group.shutdownGracefully();
        }
    }
}
