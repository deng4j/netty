package com.dengzhihong.netty_rpc.server;

import com.dengzhihong.netty_rpc.protocol.MessageCodecSharale;
import com.dengzhihong.netty_rpc.protocol.ProtocolFrameDecoder;
import com.dengzhihong.netty_rpc.server.handler.RpcRequestMessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcServer {

    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler logHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharale messageCodec = new MessageCodecSharale();
        //rpc请求消息处理器
        RpcRequestMessageHandler rpcHandler = new RpcRequestMessageHandler();
        try {
            ChannelFuture channelFuture = new ServerBootstrap()
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ProtocolFrameDecoder());
                        ch.pipeline().addLast(logHandler);
                        ch.pipeline().addLast(messageCodec);
                        ch.pipeline().addLast(rpcHandler);
                    }
                })
                .bind(8080);
            channelFuture.sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error",e);
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
