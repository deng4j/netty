package com.dengzhihong.selfprotocol.server.handler;

import com.dengzhihong.selfprotocol.message.GroupJoinRequestMessage;
import com.dengzhihong.selfprotocol.message.GroupJoinResponseMessage;
import com.dengzhihong.selfprotocol.server.session.Group;
import com.dengzhihong.selfprotocol.server.session.GroupSession;
import com.dengzhihong.selfprotocol.server.session.GroupSessionFactory;
import com.dengzhihong.selfprotocol.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class GroupJoinRequestMessageHandler extends SimpleChannelInboundHandler<GroupJoinRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupJoinRequestMessage msg) throws Exception {
        //获取群会话
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        //添加
        Group group = groupSession.joinMember(msg.getGroupName(), msg.getUsername());
        if (null !=group){
            ctx.writeAndFlush(new GroupJoinResponseMessage(true,"您已成功加入群聊:"+msg.getGroupName()));
        }else {
            ctx.writeAndFlush(new GroupJoinResponseMessage(false,"加入群聊:"+msg.getGroupName()+"失败"));
        }
    }
}
