package com.example.mynettyprotocol.server.handler;

import com.example.mynettyprotocol.server.common.MessageType;
import com.example.mynettyprotocol.server.domain.Header;
import com.example.mynettyprotocol.server.domain.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 用于在通道激活时发起握手请求
 */
public class LoginAuthReqHandler extends SimpleChannelInboundHandler {

    @Override  
    public void channelActive(ChannelHandlerContext ctx) throws Exception {  
        ctx.writeAndFlush(buildLoginReq());  
    }
  
    private NettyMessage buildLoginReq() {
        NettyMessage message = new NettyMessage();  
        Header header = new Header();
        header.setType(MessageType.LOGIN_REQ.value());
        message.setHeader(header);  
        return message;  
    }  
  
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {  
        ctx.fireExceptionCaught(cause);  
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;

        // 如果是握手应答消息，需要判断是否认证成功
        if (message.getHeader()!=null && message.getHeader().getType()==MessageType.LOGIN_RESP.value()) {
            byte loginResult = (byte) message.getBody();
            if (loginResult != (byte) 0) {
                // 握手失败，关闭连接
                ctx.close();
            } else {
                System.out.println("Login is ok : " + message);
                ctx.fireChannelRead(msg);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}