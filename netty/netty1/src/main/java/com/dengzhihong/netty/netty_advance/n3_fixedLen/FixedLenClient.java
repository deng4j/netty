package com.dengzhihong.netty.netty_advance.n3_fixedLen;

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
 * 定长解码器
 */
public class FixedLenClient {

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
                                byte[] bytes = fill10Bytes(c, (int)(Math.random() * 10 + 1));
                                buf.writeBytes(bytes);
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
     * 固定长度10，不足的以‘_’补齐
     */
    private static byte[] fill10Bytes(char c,int len){
        byte[] bytes = new byte[10];
        if (len>10) len=10;
        for (int i = 0; i < 10; i++) {
            if (i<=len-1){
                bytes[i]= (byte)c;
                System.out.print(c+" ");
                continue;
            }
            bytes[i]='_';
            System.out.print('_'+" ");
        }
        System.out.println();
        return bytes;
    }
}
