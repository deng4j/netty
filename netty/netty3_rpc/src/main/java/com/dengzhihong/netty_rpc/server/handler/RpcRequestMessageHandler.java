package com.dengzhihong.netty_rpc.server.handler;

import com.dengzhihong.netty_rpc.message.RpcRequestMessage;
import com.dengzhihong.netty_rpc.message.RpcResponseMessage;
import com.dengzhihong.netty_rpc.server.service.HelloService;
import com.dengzhihong.netty_rpc.server.service.ServiceFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage message)  {
        RpcResponseMessage response = new RpcResponseMessage();
        try {
            //获取真正的对象
            Object service = ServiceFactory.getService(Class.forName(message.getInterfaceName()));
            //获取调用的方法
            Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
            //调用方法并获取返回值
            Object invoke = method.invoke(service, message.getParameterValue());
            response.setReturnValue(invoke);
        } catch (Exception e) {
            e.printStackTrace();
            response.setExceptionValue(e);
        }
        ctx.writeAndFlush(response);
    }

    /**
     * 示例
     */
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RpcRequestMessage message = new RpcRequestMessage(1, "com.dengzhihong.netty_rpc.server.service.HelloService", "sayHello", String.class,
            new Class[] {String.class}, new Object[] {"张三"});
        HelloService service = (HelloService)ServiceFactory.getService(Class.forName(message.getInterfaceName()));
        Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
        Object invoke = method.invoke(service, message.getParameterValue());
        System.out.println(invoke);
    }
}
