package com.dengzhihong.netty.netty.n5_pipeline;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * Pipeline:
 * 1.ChannelHandler 用来处理 Channel 上的各种事件，分为入站、出站两种。所有 ChannelHandler 被连成一串
 * > 入站处理器通常是 ChannelInboundHandlerAdapter 的子类，主要用来读取客户端数据，写回结果
 * > 出站处理器通常是 ChannelOutboundHandlerAdapter 的子类，主要对写回结果进行加工
 *
 * 2.这样一个handler链：head in_1 in_2 in_3 out_4 out_5 out_6 tail
 *  > 入站处理器中，ctx.fireChannelRead(msg) 是调用下一个入站处理器
 *  > 3 处的 ctx.channel().write(msg) 会从尾部开始触发后续出站处理器的执行
 *  > 出站处理器中，ctx.write(msg, promise) 的调用也会触发上一个出站处理器
 *  >ctx.channel().write(msg) VS ctx.write(msg)
 *    > 都是触发出站处理器的执行
 *    > ctx.channel().write(msg) 从尾部开始查找出站处理器
 *    > ctx.write(msg) 是从当前节点找上一个出站处理器
 *    > 3 处的 ctx.channel().write(msg) 如果改为 ctx.write(msg) 仅会打印 1 2 3，因为节点3 之前没有其它出站处理器了
 *    > 6 处的 ctx.write(msg, promise) 如果改为 ctx.channel().write(msg) 会打印 1 2 3 6 6 6... 因为 ctx.channel().write() 是从尾部开始查找，结果又是节点6 自己
 */
@Slf4j
public class PipelineServer {

    public static void main(String[] args) {
        new ServerBootstrap()
            .group(new NioEventLoopGroup())
            .channel(NioServerSocketChannel.class)
            .childHandler(
                new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    //1.通过channel拿到pipeline
                    ChannelPipeline pipeline = ch.pipeline();
                    //2.添加入站处理器head-> h1->h2->h3->h4->h5->h6 ->tail,是一个双向链表
                    pipeline.addLast("h1",new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.info("h1");
                            ByteBuf buf= (ByteBuf)msg;
                            String str = buf.toString(Charset.defaultCharset());
                            //调用fireChannelInactive()方法，将数据传递给下一个handler
                            super.channelRead(ctx, str);
                        }
                    });
                    pipeline.addLast("h2",new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.info("h2");
                            Student student = new Student(msg.toString());
                            //将student对象传递给下一个handler
                            super.channelRead(ctx, student);
                        }
                    });
                    pipeline.addLast("h3",new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.info("h3：{}",msg.toString());
                            super.channelRead(ctx, msg);
                            //ch.writeAndFlush() 从tail往前找出站handler
                            ch.writeAndFlush(ctx.alloc().buffer().writeBytes("h4h4".getBytes()));
                            //ctx.writeAndFlush()从当前handler往前找出站handler，找不到出站就废了
                            //ctx.writeAndFlush(ctx.alloc().buffer().writeBytes("我是h4".getBytes()));
                        }
                    });

                    //3.添加出站处理器
                    pipeline.addLast("h4",new ChannelOutboundHandlerAdapter(){
                        @Override
                        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                            log.info("h4");
                            super.write(ctx, msg, promise);
                        }
                    });
                    pipeline.addLast("h5",new ChannelOutboundHandlerAdapter(){
                        @Override
                        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                            log.info("h5");
                            super.write(ctx, msg, promise);
                        }
                    });
                    pipeline.addLast("h6",new ChannelOutboundHandlerAdapter(){
                        @Override
                        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                            log.info("h6");
                            super.write(ctx, msg, promise);
                        }
                    });
                }
            })
            .bind(8080);

    }

    @Data
    @AllArgsConstructor
    static class Student{
        private String name;
    }
}
