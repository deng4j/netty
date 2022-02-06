package com.dengzhihong.selfprotocol.server.handler;

import com.dengzhihong.selfprotocol.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
@ChannelHandler.Sharable
public class QuitHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当连接正常断开时触发事件
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //从会话中移除
        SessionFactory.getSession().unbind(ctx.channel());
        log.info("已断开连接");
    }

    /**
     * 捕捉到异常时触发
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //从会话中移除
        SessionFactory.getSession().unbind(ctx.channel());
        log.info("异常断开连接:{}",cause.getMessage());
    }
}
