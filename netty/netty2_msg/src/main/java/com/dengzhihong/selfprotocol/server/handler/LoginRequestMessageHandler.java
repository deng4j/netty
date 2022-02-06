package com.dengzhihong.selfprotocol.server.handler;

import com.dengzhihong.selfprotocol.message.LoginRequestMessage;
import com.dengzhihong.selfprotocol.message.LoginResponseMessage;
import com.dengzhihong.selfprotocol.server.service.UserServiceFactory;
import com.dengzhihong.selfprotocol.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 登陆handler
 * 这个handler只关心LoginRequestMessage类型消息
 * 可以共享的handler要加@Sharable注解
 */
@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        String password = msg.getPassword();
        //判断用户名密码正确
        boolean login = UserServiceFactory.getUserService().login(username, password);
        LoginResponseMessage responseMessage;
        if (login) {
            responseMessage = new LoginResponseMessage(true, "登陆成功");
            //保存用户信息
            SessionFactory.getSession().bind(ctx.channel(), username);
        } else {
            responseMessage = new LoginResponseMessage(false, "用户名或密码错误");
        }
        ctx.writeAndFlush(responseMessage);
    }
}
