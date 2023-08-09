package com.example.netty6websocket.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpUtil.setContentLength;

@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker handshaker;
    private volatile ScheduledFuture<?> heartBeat;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 传统的http接入
        if (msg instanceof FullHttpRequest){
            log.info("传统的http接入");
            handleHttpRequest(ctx,(FullHttpRequest)msg);
        }
        //WebSocket接入
        else if (msg instanceof WebSocketFrame) {
            log.info("WebSocket接入");
            handleWebSocketFrame(ctx,(WebSocketFrame)msg);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // 判断是否关闭链路的指令
        if (frame instanceof CloseWebSocketFrame){
            handshaker.close(ctx.channel(), ((CloseWebSocketFrame) frame).retain());
            return;
        }
        // 判断是否是ping消息
        if (frame instanceof PingWebSocketFrame){
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
        }
        // 本案例仅支持文本消息，不支持二进制消息
        if (!(frame instanceof TextWebSocketFrame)){
            throw new UnsupportedOperationException(String.format("%s frame types not supported",frame.getClass().getName()));
        }
        //返回应答消息
        String request = ((TextWebSocketFrame) frame).text();
        log.info(String.format("%s received %s",ctx.channel(),request));
        ctx.executor().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                ctx.writeAndFlush(new TextWebSocketFrame(request+",欢迎使用netty websocket，现在时刻："+new Date()));
            }
        },0, 1000, TimeUnit.MILLISECONDS);
//        ctx.channel().writeAndFlush(new TextWebSocketFrame(request+",欢迎使用netty websocket，现在时刻："+new Date()));
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        // 如果Http解码异常
        if (!req.decoderResult().isSuccess() || (!"websocket".equals(req.headers().get("Upgrade")))){
            sendHttpResonse(ctx,req,new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        // 构造握手响应返回，本机测试
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "ws://localhost:8080/websocket",null,false
        );
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker==null){
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        }else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    private void sendHttpResonse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse res) {
        // 返回应答给客户端
        if (res.status().code()!=200){
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            setContentLength(res,res.content().readableBytes());
        }
        // 如果是非keep-alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!isKeepAlive(req)||res.status().code()!=200){
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
