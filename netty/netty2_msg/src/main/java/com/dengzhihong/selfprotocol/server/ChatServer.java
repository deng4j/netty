package com.dengzhihong.selfprotocol.server;

import com.dengzhihong.selfprotocol.message.ChatRequestMessage;
import com.dengzhihong.selfprotocol.protocol.MessageCodecSharale;
import com.dengzhihong.selfprotocol.protocol.ProtocolFrameDecoder;
import com.dengzhihong.selfprotocol.server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatServer {

    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler logHandler = new LoggingHandler(LogLevel.INFO);
        MessageCodecSharale messageCodec = new MessageCodecSharale();

        LoginRequestMessageHandler loginRequestMessageHandler = new LoginRequestMessageHandler();
        ChatRequestMessageHandler chatRequestMessageHandler = new ChatRequestMessageHandler();
        GroupCreateRequestMessageHandler groupCreateRequestMessageHandler = new GroupCreateRequestMessageHandler();
        GroupChatRequestMessageHandler groupChatRequestMessageHandler = new GroupChatRequestMessageHandler();
        GroupJoinRequestMessageHandler groupJoinRequestMessageHandler = new GroupJoinRequestMessageHandler();
        GroupQuitRequestMessageHandler groupQuitRequestMessageHandler = new GroupQuitRequestMessageHandler();
        GroupMembersRequestMessageHandler groupMembersRequestMessageHandler = new GroupMembersRequestMessageHandler();
        QuitHandler quitHandler = new QuitHandler();
        try {
            ChannelFuture channelFuture = new ServerBootstrap().channel(NioServerSocketChannel.class)
                .group(boss, worker)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ProtocolFrameDecoder());
                        ch.pipeline().addLast(logHandler);
                        ch.pipeline().addLast(messageCodec);
                        /**
                         * 空闲检测器,只需要监测read或write空闲时间过长
                         * 5秒内没有收到channel的数据会触发一个IdleState#READER_IDLE事件
                         * 写空闲事件IdleState.WRITER_IDLE
                         * 读写空闲事件IdleState.ALL_IDLE
                         * 0表示不关心
                         */
                        ch.pipeline().addLast(new IdleStateHandler(5,0,0));
                        //ChannelDuplexHandler同时作为入站和出站处理器
                        ch.pipeline().addLast(new ChannelDuplexHandler(){
                            @Override
                            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                IdleStateEvent event= (IdleStateEvent)evt;
                                if (event.state()== IdleState.READER_IDLE) {
                                    //触发的读空闲事件
                                    log.info("读空闲超过5秒,关闭channel");
                                    //关闭空闲channel
                                    ctx.channel().close();
                                }
                            }
                        });
                        //只关心LoginRequestMessage的消息
                        ch.pipeline().addLast(loginRequestMessageHandler);
                        //只关心ChatRequestMessage消息
                        ch.pipeline().addLast(chatRequestMessageHandler);
                        //群创建handler
                        ch.pipeline().addLast(groupCreateRequestMessageHandler);
                        //发送群消息handler
                        ch.pipeline().addLast(groupChatRequestMessageHandler);
                        //加入群聊
                        ch.pipeline().addLast(groupJoinRequestMessageHandler);
                        //获取群成员
                        ch.pipeline().addLast(groupMembersRequestMessageHandler);
                        //退出群聊
                        ch.pipeline().addLast(groupQuitRequestMessageHandler);
                        //退出会话
                        ch.pipeline().addLast(quitHandler);
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
