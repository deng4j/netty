package com.dengzhihong.netty_rpc.server.handler;

import com.dengzhihong.netty_rpc.message.RpcResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 这个handler一定是在nio线程中执行的
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {
    /**
     *存放接收结果的sequenceId-promise
     */
    public static final Map<Integer, Promise> map=new ConcurrentHashMap<>();


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        //拿到promise
        int sequenceId = msg.getSequenceId();
        //取出并返回
        Promise promise = map.remove(sequenceId);
        if (null!=promise){
            Exception exceptionValue = msg.getExceptionValue();
            Object returnValue = msg.getReturnValue();
            if (null!=exceptionValue){
                promise.setFailure(exceptionValue);
            }else {
                promise.setSuccess(returnValue);
            }
        }
    }
}
