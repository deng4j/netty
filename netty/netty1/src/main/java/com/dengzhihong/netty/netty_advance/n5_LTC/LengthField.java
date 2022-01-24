package com.dengzhihong.netty.netty_advance.n5_LTC;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

/**
 * LTC解码器
 */
public class LengthField {

    public static void main(String[] args) throws InterruptedException {
        EmbeddedChannel channel = new EmbeddedChannel(
            //解码器(最大长度，长度字段偏移量，长度字段的长度(int是4)，长度之后调整几个字节是实际内容，去除头几个字节)
            new LengthFieldBasedFrameDecoder(1024,0,4,1,0),
            new LoggingHandler(LogLevel.DEBUG)
        );

        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        send(buffer,"Hello World");
        send(buffer,"Hello! long time no see");
        channel.writeInbound(buffer);
    }

    /**
     *先读长度个字节，然后读实际内容长度len个字节的实际内容
     * +----------+----------------+
     * |  Length  | Actual Content |
     * |  0X000E  | "Hello, World" |
     * +----------+-----------------
     */
    private static void send(ByteBuf buffer,String str) {
        byte[] bytes = str.getBytes();
        int len = bytes.length;
        //先写长度字段(int是4)，和实际内容长度
        buffer.writeInt(len);
        //以2为版本号
        buffer.writeByte(2);
        //再写实际内容
        buffer.writeBytes(bytes);
    }
}
