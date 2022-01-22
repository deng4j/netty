package com.dengzhihong.netty.netty.n1_hello;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;


/**
 * 1.把 channel 理解为数据的通道
 * 2.把 msg 理解为流动的数据，最开始输入是 ByteBuf，但经过 pipeline 的加工，会变成其它类型对象，最后输出又变成 ByteBuf
 * 3.把 handler 理解为数据的处理工序
 *     工序有多道，合在一起就是 pipeline，pipeline 负责发布事件（读、读取完成...）传播给每个 handler， handler 对自己感兴趣的事件进行处理（重写了相应事件处理方法）
 *     handler 分 Inbound 和 Outbound 两类
 * 4.把 eventLoop 理解为处理数据的工人
 *     工人可以管理多个 channel 的 io 操作，并且一旦工人负责了某个 channel，就要负责到底（绑定）
 *     工人既可以执行 io 操作，也可以进行任务处理，每位工人有任务队列，队列里可以堆放多个 channel 的待处理任务，任务分为普通任务、定时任务
 *     工人按照 pipeline 顺序，依次按照 handler 的规划（代码）处理数据，可以为每道工序指定不同的工人
 */
public class HelloServer {

    public static void main(String[] args) {

        new ServerBootstrap() //1.服务端启动器，负责组装netty组件，启动服务器
            .group(new NioEventLoopGroup()) //2.加入事件组，如boss负责连接和worker负责读写
            .channel(NioServerSocketChannel.class) //3.选择ServerSocketChannel的实现
            .childHandler( //4.决定worker执行哪些操作
                new ChannelInitializer<NioSocketChannel>() { //5.添加了初始化处理器，channel是与客户端进行数据读写的通道，负责添加别的handler
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception { //6.具体添加哪些handler，连接建立后才调用初始化方法
                    ch.pipeline().addLast(new StringDecoder()); //6.1将ByteBuffer转化为String
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){ //6.2自定义handler
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            System.out.println(msg);
                        }
                    });
                }
            })
            .bind(8080);
    }
}
