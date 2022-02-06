package com.dengzhihong.selfprotocol.server.handler;

import com.dengzhihong.selfprotocol.message.ChatResponseMessage;
import com.dengzhihong.selfprotocol.message.GroupChatRequestMessage;
import com.dengzhihong.selfprotocol.message.GroupCreateRequestMessage;
import com.dengzhihong.selfprotocol.message.GroupCreateResponseMessage;
import com.dengzhihong.selfprotocol.server.session.Group;
import com.dengzhihong.selfprotocol.server.session.GroupSession;
import com.dengzhihong.selfprotocol.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.Set;

/**
 * 群发handler
 */
@ChannelHandler.Sharable
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        //获取群名称
        String groupName = msg.getGroupName();
        //获取群成员
        Set<String> members = msg.getMembers();
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(groupName, members);
        if (null == group){
            //向群创建者发送消息创建成功
            ctx.writeAndFlush(new GroupCreateResponseMessage(true,groupName+"创建成功"));
            //给群成员发送消息
            List<Channel> membersChannel = groupSession.getMembersChannel(groupName);
            membersChannel.forEach(channel -> {channel.writeAndFlush(new GroupCreateResponseMessage(true,"你已被拉入"+groupName));});
        }else {
            ctx.writeAndFlush(new GroupCreateResponseMessage(false,groupName+"创建失败"));
        }
    }
}
