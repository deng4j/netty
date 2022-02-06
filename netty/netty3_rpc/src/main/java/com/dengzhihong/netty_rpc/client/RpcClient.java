package com.dengzhihong.netty_rpc.client;

import com.dengzhihong.netty_rpc.message.RpcRequestMessage;
import com.dengzhihong.netty_rpc.protocol.MessageCodecSharale;
import com.dengzhihong.netty_rpc.protocol.ProtocolFrameDecoder;
import com.dengzhihong.netty_rpc.server.handler.RpcResponseMessageHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler logHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharale messageCodec = new MessageCodecSharale();
        //rpc响应消息处理器
        RpcResponseMessageHandler rpcHandler = new RpcResponseMessageHandler();
        try {
            Channel channel = new Bootstrap().channel(NioSocketChannel.class).group(group).handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
                    ch.pipeline().addLast(logHandler);
                    ch.pipeline().addLast(messageCodec);
                    ch.pipeline().addLast(rpcHandler);
                }
            }).connect("localhost", 8080).sync().channel();

            ChannelFuture future = channel.writeAndFlush(
                new RpcRequestMessage(1, "com.dengzhihong.netty_rpc.server.service.HelloService", "sayHello", String.class,
                    new Class[] {String.class}, new Object[] {"张三"}));
            //监听成功或失败
            future.addListener(fu->{
                if (!fu.isSuccess()) {
                    log.error("error",fu.cause());
                }
            });
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }
}