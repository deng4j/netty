package com.example.mynettyprotocol.server.handler;

import com.example.mynettyprotocol.server.common.MessageType;
import com.example.mynettyprotocol.server.domain.Header;
import com.example.mynettyprotocol.server.domain.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;

/**
 * 发送心跳处理器
 */
public class HeartBeatReqHandler extends SimpleChannelInboundHandler {
  
    private volatile ScheduledFuture<?> heartBeat;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        // 握手成功，主动发送心跳消息
        if (message.getHeader()!=null && message.getHeader().getType()== MessageType.LOGIN_RESP.value()) {
            heartBeat = ctx.executor().scheduleAtFixedRate(new HeartBeatReqHandler.HeartBeatTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);
        } else if (message.getHeader()!=null && message.getHeader().getType()==MessageType.HEARTBEAT_RESP.value()) {
            System.out.println("Client receive server heart beat message : ---> " + message);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private class HeartBeatTask implements Runnable {  
        private final ChannelHandlerContext ctx;  
  
        public HeartBeatTask(final ChannelHandlerContext ctx) {  
            this.ctx = ctx;  
        }  
  
        @Override  
        public void run() {  
            NettyMessage heatBeat = buildHeatBeat();  
            System.out.println("Client send heart beat messsage to server : ---> " + heatBeat);  
            ctx.writeAndFlush(heatBeat);  
        }  
  
        private NettyMessage buildHeatBeat() {  
            NettyMessage message = new NettyMessage();  
            Header header = new Header();
            header.setType(MessageType.HEARTBEAT_REQ.value());  
            message.setHeader(header);  
            return message;  
        }  
    }  
  
    @Override  
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {  
        cause.printStackTrace();  
        if (heartBeat != null) {  
            heartBeat.cancel(true);  
            heartBeat = null;  
        }  
        ctx.fireExceptionCaught(cause);  
    }  
      
}  