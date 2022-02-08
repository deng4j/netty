package com.dengzhihong.netty_rpc.client;

import com.dengzhihong.netty_rpc.message.RpcRequestMessage;
import com.dengzhihong.netty_rpc.protocol.MessageCodecSharale;
import com.dengzhihong.netty_rpc.protocol.ProtocolFrameDecoder;
import com.dengzhihong.netty_rpc.protocol.SequenceIdGenerator;
import com.dengzhihong.netty_rpc.server.handler.RpcResponseMessageHandler;
import com.dengzhihong.netty_rpc.server.service.HelloService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;

@Slf4j
public class RpcClientManager {

    private static volatile Channel channel = null;

    /**
     * 获取唯一的channel对象
     */
    public static Channel getChannel() {
        if (null == channel) {
            synchronized (RpcClientManager.class) {
                if (null == channel) {
                    initChannel();
                }
            }
        }
        return channel;
    }

    /**
     * 创建代理
     */
    public static <T> T getProxyService(Class<T> serviceClass) {

        ClassLoader loader = serviceClass.getClassLoader();
        Class<?>[] interfaces = new Class[] {serviceClass};
        Object proxyInstance = Proxy.newProxyInstance(loader, interfaces, ((proxy, method, args) -> {
            //1.将方法调用转化为消息对象
            int sequenceId = SequenceIdGenerator.nextId();
            RpcRequestMessage msg = new RpcRequestMessage(sequenceId, serviceClass.getName(), method.getName(), method.getReturnType(),
                method.getParameterTypes(), args);
            //发送消息
            getChannel().writeAndFlush(msg);
            //指定promise异步接收结果线程
            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            //将promise存入ResponseHandler
            RpcResponseMessageHandler.map.put(sequenceId, promise);
            //等待promise的结果
            promise.await();
            if (!promise.isSuccess()) {
                throw new RuntimeException(promise.cause());
            }
            return promise.getNow();
        }));
        return (T)proxyInstance;
    }

    /**
     * 初始化channel
     */
    private static void initChannel() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler logHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharale messageCodec = new MessageCodecSharale();
        //rpc响应消息处理器
        RpcResponseMessageHandler rpcHandler = new RpcResponseMessageHandler();
        Bootstrap bootstrap = new Bootstrap().channel(NioSocketChannel.class).group(group).handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProtocolFrameDecoder());
                ch.pipeline().addLast(logHandler);
                ch.pipeline().addLast(messageCodec);
                ch.pipeline().addLast(rpcHandler);
            }
        });
        try {
            channel = bootstrap.connect("localhost", 8080).sync().channel();
            //改成异步关闭channel，不然一直阻塞等待关闭就返回不了
            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //测试
        HelloService service = getProxyService(HelloService.class);
        String str = service.sayHello("李四");
        System.out.println(str);
    }
}