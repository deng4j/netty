package com.dengzhihong.selfprotocol.server.handler;

import com.dengzhihong.selfprotocol.message.ChatRequestMessage;
import com.dengzhihong.selfprotocol.message.ChatResponseMessage;
import com.dengzhihong.selfprotocol.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 聊天handler
 */
@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        //找到消息接收者
        String to = msg.getTo();
        //找到消息接收者的channel
        Channel channel = SessionFactory.getSession().getChannel(to);
        if (null != channel){
            //如果接收者channel存在，把消息发送给接收者
            channel.writeAndFlush(new ChatResponseMessage(msg.getFrom(), msg.getContent()));
        }else {
            //给发送者返回信息
            ctx.writeAndFlush(new ChatResponseMessage(false,"对方不在线"));
        }
    }
}
