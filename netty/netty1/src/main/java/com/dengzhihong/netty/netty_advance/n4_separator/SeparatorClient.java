package com.dengzhihong.netty.netty_advance.n4_separator;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

/**
 * 分隔符解决粘包半包
 */
public class SeparatorClient {

    public static void main(String[] args) throws InterruptedException {
        send();
    }

    private static void send() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        new Bootstrap()
            .group(group)
            .channel(NioSocketChannel.class)
            .handler(
                new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                        //channel连接建立后触发active事件
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            ByteBuf buf = ctx.alloc().buffer();
                            char c='0';
                            for (int i = 0; i < 10; i++) {
                                StringBuilder sb = makeString(c, (int)(Math.random() * 256 + 1));
                                buf.writeBytes(sb.toString().getBytes());
                                c++;
                            }
                            ctx.writeAndFlush(buf);
                        }
                    });

                }
            })
            .connect(new InetSocketAddress("localhost",8080))
            .sync()
            .channel();
    }

    /**
     * 末尾加换行符的字符串
     */
    private static StringBuilder makeString(char c,int len){
        StringBuilder sb = new StringBuilder(len + 2);
        for (int i = 0; i < len; i++) {
            sb.append(c);
        }
        sb.append('\n');
        return sb;
    }
}
