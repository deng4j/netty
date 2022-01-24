package com.dengzhihong.netty.netty_advance.n2_shortConnect;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * 短链接解决黏包半包
 */
@Slf4j
public class ShortConnectClient {

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            send();
        }
    }

    private static void send() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(
                    new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            //channel连接建立后触发active事件
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                ByteBuf buf = ctx.alloc().buffer(16);
                                buf.writeBytes("123456789abcdefjhnioilk".getBytes());
                                ctx.writeAndFlush(buf);
                                ctx.channel().close();
                            }
                        });

                    }
                })
                .connect(new InetSocketAddress("localhost",8080))
                .sync()
                .channel();
        } catch (InterruptedException e) {
            log.info("client error",e);
        }finally {
            group.shutdownGracefully();
        }
    }
}
