package com.example.netty6websocket.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketServer {

    public static void main(String[] args) throws InterruptedException {
        int port = 8080;
        new WebSocketServer().run(port);
    }


    public void run(int port) throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 将消息解码为Http消息
                            pipeline.addLast("http-codec",new HttpServerCodec());
                            // 将Http消息的多个部分组合成一条完整的Http消息
                            pipeline.addLast("aggregator",new HttpObjectAggregator(65536));
                            // 用于向客户端发送h5文件，主要作用于支持浏览器和服务端进行websocket通信
                            pipeline.addLast("http-chunked",new ChunkedWriteHandler());
                            pipeline.addLast("handler",new WebSocketServerHandler());
                        }
                    });
            Channel ch = serverBootstrap.bind(port).sync().channel();

            System.out.println("web socket server started at port:"+port);
            System.out.println("open brower：http://localhost:"+port);

            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
