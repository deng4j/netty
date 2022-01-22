package com.dengzhihong.netty.netty.n2_EventLoop.event2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * 细化1：NioEventLoopGroup分工：
 * NioServerSocketChannel只会跟一个NioEventLoopGroup绑定，所以boss的NioEventLoopGroup不需要设置线程数，
 * 而worker的NioEventLoopGroup可以根据你的cpu核心数*2。
 *
 * 细化2：将耗时较长的io交给其他事件组执行
 *
 * handler线程切换：
 * 如果两个handler绑定的是同一事件组，那么直接调用，
 * 否则会把要调用的代码封装成一个任务对象，由下一个handler的事件组执行。
 */
@Slf4j
public class EventLoopServer {

    public static void main(String[] args) {
        //细分2：创建一个EventLoopGroup执行io事件
        DefaultEventLoopGroup defaultGroup = new DefaultEventLoopGroup();
        new ServerBootstrap()
            //细分1：boss负责ServerSocketChannel上的accept事件，worker只负责socketChannel上的读写操作
            .group(new NioEventLoopGroup(),new NioEventLoopGroup(2))
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    //这个是使用worker的nioEventLoopGroup事件组执行
                    ch.pipeline().addLast("handler1",new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            ByteBuf buf= (ByteBuf)msg;
                            log.info(buf.toString(Charset.defaultCharset()));
                            //将消息传递给下一个channel
                            ctx.fireChannelRead(msg);
                        }
                    });
                    //这里执行用的就是defaultGroup事件组
                    ch.pipeline().addLast(defaultGroup,"handler2",new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            ByteBuf buf= (ByteBuf)msg;
                            log.info(buf.toString(Charset.defaultCharset()));
                        }
                    });
                }
            })
            .bind(8080);
    }
}
