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

import java.nio.charset.StandardCharsets;


/**
 * LTC解码器
 */
public class LengthField {

    public static void main(String[] args) throws InterruptedException {
        m3();
    }

    private static void m3() {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024,4,4),
                new LoggingHandler(LogLevel.DEBUG)
        );

        ByteBuf sendBuf = ByteBufAllocator.DEFAULT.buffer();

        sendBuf.writeInt(0xabef0101);
        sendBuf.writeInt(10);
        sendBuf.writeLong(13231);
        sendBuf.writeByte((byte) 6);
        sendBuf.writeByte((byte) 1);
        sendBuf.writeInt(0);
        sendBuf.writeInt(0);
        channel.writeInbound(sendBuf);
    }
    private static void m2() {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024,2,4),
                new LoggingHandler(LogLevel.DEBUG)
        );

        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        buffer.writeBytes("00".getBytes(StandardCharsets.UTF_8)); // 长度偏移量2
        String s = "abcdefjh";
        buffer.writeInt(s.length()); // 长度
        buffer.writeBytes(s.getBytes(StandardCharsets.UTF_8));
        channel.writeInbound(buffer);
    }

    private static void m1() {
        EmbeddedChannel channel = new EmbeddedChannel(
            /**
             * lengthFieldOffset 长度域的偏移量，简单而言就是偏移几个字节是长度域
             * lengthFieldLength ： 长度域的所占的字节数(int是4)
             * lengthAdjustment ： 长度之后调整几个字节是实际内容
             * initialBytesToStrip ： 需要跳过开头的字节数
             */
            new LengthFieldBasedFrameDecoder(1024,0,4,1,0),
            new LoggingHandler(LogLevel.DEBUG)
        );

        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        send1(buffer,"Hello World");
        send1(buffer,"Hello! long time no see");
        channel.writeInbound(buffer);
    }

    /**
     *先读长度个字节，然后读实际内容长度len个字节的实际内容
     * +----------+----------------+
     * |  Length  | Actual Content |
     * |  0X000E  | "Hello, World" |
     * +----------+-----------------
     */
    private static void send1(ByteBuf buffer,String str) {
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
