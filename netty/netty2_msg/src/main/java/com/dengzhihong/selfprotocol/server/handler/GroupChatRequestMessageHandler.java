package com.dengzhihong.selfprotocol.server.handler;

import com.dengzhihong.selfprotocol.message.GroupChatRequestMessage;
import com.dengzhihong.selfprotocol.message.GroupChatResponseMessage;
import com.dengzhihong.selfprotocol.server.session.GroupSession;
import com.dengzhihong.selfprotocol.server.session.GroupSessionFactory;
import com.dengzhihong.selfprotocol.server.session.Session;
import com.dengzhihong.selfprotocol.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        //先获取群名称
        String groupName = msg.getGroupName();
        //获取所有群成员
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        List<Channel> channelList = groupSession.getMembersChannel(groupName);
        //给所有成员发送消息
        channelList.forEach(channel -> {channel.writeAndFlush(new GroupChatResponseMessage(msg.getFrom(),msg.getContent()));});
    }
}
