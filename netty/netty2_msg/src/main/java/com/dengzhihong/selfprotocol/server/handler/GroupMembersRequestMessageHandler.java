package com.dengzhihong.selfprotocol.server.handler;

import com.dengzhihong.selfprotocol.message.GroupMembersRequestMessage;
import com.dengzhihong.selfprotocol.message.GroupMembersResponseMessage;
import com.dengzhihong.selfprotocol.server.session.GroupSession;
import com.dengzhihong.selfprotocol.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Set;

@ChannelHandler.Sharable
public class GroupMembersRequestMessageHandler extends SimpleChannelInboundHandler<GroupMembersRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMembersRequestMessage msg) throws Exception {
        //获取群会话
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Set<String> members = groupSession.getMembers(msg.getGroupName());
        if (members.size()>0){
            ctx.writeAndFlush(new GroupMembersResponseMessage(members));
        }
    }
}
