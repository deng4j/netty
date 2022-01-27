package com.dengzhihong.netty.netty_protocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import static com.google.common.net.HttpHeaders.CONTENT_LENGTH;

@Slf4j
public class TestHttp {

    /**
     *打开浏览器测试localhost:8080/index.html
     */
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try {
            ChannelFuture channelFuture = new ServerBootstrap().channel(NioServerSocketChannel.class)
                .group(boss, worker)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                        //添加http编解码，将编解码后的数据传递给下一个handler
                        ch.pipeline().addLast(new HttpServerCodec());
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                //DefaultHttpRequest、LastHttpContent$1
                                log.info("请求对象：{}",msg.getClass());
                                if (msg instanceof HttpRequest){
                                    //请求头

                                }else if (msg instanceof HttpContent){
                                    //请求体
                                }
                                ctx.fireChannelRead(msg);
                            }
                        });
                        //只关心HttpRequest，那就只将HttpRequest传递下去
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<HttpRequest>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
                                log.info("获取请求:{}",msg.uri());
                                //返回响应(http版本，状态)
                                DefaultFullHttpResponse response=new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
                                byte[] bytes = "<h1 color='red'> hello world!</h1>".getBytes();
                                //设置响应头
                                response.headers().setInt(CONTENT_LENGTH,bytes.length);
                                //设置响应体
                                ByteBuf buf = response.content();
                                buf.writeBytes(bytes);
                                //返回数据
                                ctx.writeAndFlush(response);
                            }
                        });

                    }
                })
                .bind(8080)
                .sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
