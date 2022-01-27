package com.dengzhihong.selfprotocol.server.handler;

import com.dengzhihong.selfprotocol.message.GroupJoinResponseMessage;
import com.dengzhihong.selfprotocol.message.GroupQuitRequestMessage;
import com.dengzhihong.selfprotocol.message.GroupQuitResponseMessage;
import com.dengzhihong.selfprotocol.server.session.Group;
import com.dengzhihong.selfprotocol.server.session.GroupSession;
import com.dengzhihong.selfprotocol.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class GroupQuitRequestMessageHandler extends SimpleChannelInboundHandler<GroupQuitRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupQuitRequestMessage msg) throws Exception {
        //获取群会话
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        //退出群会话
        Group group = groupSession.removeMember(msg.getGroupName(), msg.getUsername());
        if (null !=group){
            ctx.writeAndFlush(new GroupQuitResponseMessage(true,"您已成功退出群聊:"+msg.getGroupName()));
        }else {
            ctx.writeAndFlush(new GroupJoinResponseMessage(false,"退出群聊:"+msg.getGroupName()+"失败"));
        }
    }
}
