package com.dengzhihong.selfprotocol.client;


import com.dengzhihong.selfprotocol.message.*;
import com.dengzhihong.selfprotocol.protocol.MessageCodecSharale;
import com.dengzhihong.selfprotocol.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ChatClient {

    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.INFO);
        MessageCodecSharale messageCodec = new MessageCodecSharale();
        //线程间通信
        CountDownLatch waitingForLogin = new CountDownLatch(1);
        AtomicBoolean login = new AtomicBoolean(false);

        try {
            Channel channel = new Bootstrap()
                    .channel(NioSocketChannel.class)
                    .group(group)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            //TCl是入站处理器
                            ch.pipeline().addLast(new ProtocolFrameDecoder());
                            ch.pipeline().addLast(loggingHandler);
                            ch.pipeline().addLast(messageCodec);
                            //3秒内没有向服务器发送消息，触发一个IdleState.WRITER_IDLE写空闲事件
                            ch.pipeline().addLast(new IdleStateHandler(0, 3, 0));
                            //ChannelDuplexHandler同时作为入站和出站处理器
                            ch.pipeline().addLast(new ChannelDuplexHandler() {
                                @Override
                                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                    IdleStateEvent event = (IdleStateEvent) evt;
                                    if (event.state() == IdleState.WRITER_IDLE) {
                                        //触发的写空闲事件
                                        //log.info("写空闲超过3秒，发送一个心跳包");
                                        //给服务器发送一个心跳消息，防止被服务器关闭
                                        ctx.writeAndFlush(new PingMessage());
                                    }
                                }
                            });

                            ch.pipeline().addLast("client handler", new ChannelInboundHandlerAdapter() {
                                //接收信息
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    log.info(":{}", msg);
                                    if (msg instanceof LoginResponseMessage) {
                                        LoginResponseMessage response = (LoginResponseMessage) msg;
                                        if (response.isSuccess()) {
                                            //如果登陆成功
                                            login.set(true);
                                        }
                                        //唤醒
                                        waitingForLogin.countDown();
                                    }
                                }

                                //连接建立后触发Active事件
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    //开启另一条线程，负责向服务器发送消息，防止阻塞
                                    new Thread(() -> {
                                        Scanner scanner = new Scanner(System.in);

                                        System.out.println("输入用户名");
                                        String uname = scanner.nextLine();
                                        System.out.println("请输入密码");
                                        String pwd = scanner.nextLine();
                                        //构造消息对象发送给服务器
                                        LoginRequestMessage message = new LoginRequestMessage(uname, pwd);
                                        //发送消息，ctx.writeAndFlush()从当前handler往前找出站handler
                                        ctx.writeAndFlush(message);
                                        try {
                                            //阻塞等待登陆的结果响应,唤醒后继续向下运行
                                            waitingForLogin.await();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        if (!login.get()) {
                                            ctx.channel().close();
                                            return;
                                        }

                                        //登陆成功，操作
                                        while (true) {
                                            System.out.println("==================================");
                                            System.out.println("send [username] [content]");
                                            System.out.println("gsend  [group name] [content]");
                                            System.out.println("gcreat [group name] [m1,m2,m3...]");
                                            System.out.println("gmembers [group name]");
                                            System.out.println("gjoin [group name]");
                                            System.out.println("gquit [group name]");
                                            System.out.println("quit");
                                            System.out.println("==================================");
                                            String line = scanner.nextLine();
                                            String[] commend = line.split(" ");
                                            switch (commend[0]) {
                                                case "send":
                                                    ctx.writeAndFlush(new ChatRequestMessage(uname, commend[1], commend[2]));
                                                    break;
                                                case "gsend":
                                                    ctx.writeAndFlush(new GroupChatRequestMessage(uname, commend[1], commend[2]));
                                                    break;
                                                case "gcreat":
                                                    String[] friends = commend[2].split(",");
                                                    List<String> list = Arrays.asList(friends);
                                                    Set<String> set = new HashSet<>(list);
                                                    set.add(uname);
                                                    ctx.writeAndFlush(new GroupCreateRequestMessage(commend[1], set));
                                                    break;
                                                case "gmembers":
                                                    ctx.writeAndFlush(new GroupMembersRequestMessage(commend[1]));
                                                    break;
                                                case "gjoin":
                                                    ctx.writeAndFlush(new GroupJoinRequestMessage(uname, commend[1]));
                                                    break;
                                                case "gquit":
                                                    ctx.writeAndFlush(new GroupQuitRequestMessage(uname, commend[1]));
                                                    break;
                                                case "quit":
                                                    ctx.channel().close();
                                                    return;
                                            }
                                        }
                                    }, "system in").start();
                                }

                                //连接断开触发
                                @Override
                                public void channelInactive(ChannelHandlerContext ctx) throws Exception {

                                }

                                //捕捉到异常触发
                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

                                }
                            });
                        }
                    }).connect("localhost", 8080).sync().channel();
            //也可以在这发送消息

            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
