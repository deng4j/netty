package com.dengzhihong.selfprotocol.protocolTest;

import com.dengzhihong.selfprotocol.message.LoginRequestMessage;
import com.dengzhihong.selfprotocol.protocol.MessageCodec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @Sharable表示支持多线程，可以查看LoggingHandler类
 */
public class TestMessageCodec {

    public static void main(String[] args) throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(
            //解决粘包半包，不能多个worker使用同一个LTC
            new LengthFieldBasedFrameDecoder(1024,12,4,0,0),
            new LoggingHandler(LogLevel.INFO),
            new MessageCodec());
        //1.测试encode
        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123");
        channel.writeOneOutbound(message);
        //2.测试decode
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        //将编码的写入buf
        new MessageCodec().encode(null,message,buf);
        //测试黏包半包
        buf.writeInt(2);

        channel.writeInbound(buf);
    }
}
