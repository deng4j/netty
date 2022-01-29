package com.dengzhihong.selfprotocol.protocolTest;

import com.dengzhihong.selfprotocol.config.Config;
import com.dengzhihong.selfprotocol.message.LoginRequestMessage;
import com.dengzhihong.selfprotocol.message.Message;
import com.dengzhihong.selfprotocol.protocol.MessageCodecSharale;
import com.dengzhihong.selfprotocol.protocol.Serializer;
import com.dengzhihong.selfprotocol.server.handler.LoginRequestMessageHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LoggingHandler;

public class Test3_Serializer {

    public static void main(String[] args) {
        MessageCodecSharale codec = new MessageCodecSharale();
        LoggingHandler loggingHandler = new LoggingHandler();
        EmbeddedChannel channel = new EmbeddedChannel(loggingHandler, codec, loggingHandler);

        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123");
        //测试编码
        channel.writeOutbound(message);
        System.out.println("------------------------------");
        //测试解码
        ByteBuf buf= messageToBytes(message);
        channel.writeInbound(buf);
    }

    private static ByteBuf messageToBytes(Message msg) {
        int algorithm = Config.getSerializerAlgorithm().ordinal();
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        buffer.writeBytes(new byte[]{'b','a','b','y'});
        buffer.writeByte(1);
        buffer.writeByte(algorithm);
        buffer.writeByte(msg.getMessageType());
        buffer.writeInt(msg.getSequenceId());
        buffer.writeByte(0xff);
        byte[] bytes = Serializer.Algorithm.values()[algorithm].serializer(msg);
        buffer.writeInt(bytes.length);
        buffer.writeBytes(bytes);
        return buffer;
    }
}
