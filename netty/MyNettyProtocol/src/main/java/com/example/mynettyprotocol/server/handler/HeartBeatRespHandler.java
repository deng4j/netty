package com.example.mynettyprotocol.server.handler;

import com.example.mynettyprotocol.server.common.MessageType;
import com.example.mynettyprotocol.server.domain.Header;
import com.example.mynettyprotocol.server.domain.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 心跳响应处理器
 */
public class HeartBeatRespHandler extends SimpleChannelInboundHandler {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        // 返回心跳应答消息
        if (message.getHeader()!=null && message.getHeader().getType()== MessageType.HEARTBEAT_REQ.value()) {
            System.out.println("Receive client heart beat message : ---> " + message);
            NettyMessage heartBeat = buildHeatBeat();
            System.out.println("Send heart beat response message to client : ---> " + heartBeat);
            ctx.writeAndFlush(heartBeat);
        } else
            ctx.fireChannelRead(msg);
    }

    private NettyMessage buildHeatBeat() {  
        NettyMessage message = new NettyMessage();  
        Header header = new Header();
        header.setType(MessageType.HEARTBEAT_RESP.value());  
        message.setHeader(header);  
        return message;  
    }  
      
}  