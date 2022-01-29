package com.dengzhihong.netty.netty_advance.n1_boundary;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import static com.dengzhihong.netty.netty.n9_byteBuf.LogUtil.log;

/**
 * 客户端通过.option(参数名称,值)方法给SocketChannel配置参数，
 * 服务端的.option()是给ServerSocketChannel配置参数
 * 服务端的.childOption()是给SocketChannel配置参数
 */
public class HelloServer {

    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        new ServerBootstrap()
            .group(boss,worker)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_RCVBUF,10)//调整系统的接收器缓冲区（滑动窗口）
            .childHandler(
                new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            ByteBuf buf= (ByteBuf)msg;
                            log(buf);
                        }
                    });
                }
            })
            .bind(8080);
    }
}
