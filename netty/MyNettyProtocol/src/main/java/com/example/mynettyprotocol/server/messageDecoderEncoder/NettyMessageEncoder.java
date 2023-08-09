package com.example.mynettyprotocol.server.messageDecoderEncoder;

import com.example.mynettyprotocol.server.domain.Header;
import com.example.mynettyprotocol.server.domain.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 消息编码器
 */
public final class NettyMessageEncoder extends MessageToByteEncoder<NettyMessage> {

    MarshallingEncoder marshallingEncoder;

    public NettyMessageEncoder() throws IOException {
        this.marshallingEncoder = new MarshallingEncoder();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, NettyMessage msg, ByteBuf sendBuf) throws Exception {
        if (msg==null||msg.getHeader()==null){
            throw new Exception("The encode message is null");
        }
        sendBuf.writeInt(msg.getHeader().getCrcCode());
        sendBuf.writeInt(msg.getHeader().getLength());
        sendBuf.writeLong(msg.getHeader().getSessionID());
        sendBuf.writeByte(msg.getHeader().getType());
        sendBuf.writeByte(msg.getHeader().getPriority());
        sendBuf.writeInt(msg.getHeader().getAttachment().size());
        String key = null;
        byte[] keyArray = null;
        Object value = null;
        for (Map.Entry<String, Object> param : msg.getHeader().getAttachment().entrySet()) {
            key= param.getKey();
            keyArray = key.getBytes(StandardCharsets.UTF_8);
            sendBuf.writeInt(keyArray.length);
            sendBuf.writeBytes(keyArray);
            value = param.getValue();
            marshallingEncoder.encode(value,sendBuf);
        }
        if (msg.getBody()!=null){
            marshallingEncoder.encode(msg.getBody(),sendBuf);
        }else sendBuf.writeInt(0);
        // 设置消息长度
        sendBuf.setInt(4, sendBuf.readableBytes()-8);
    }

    public static void main(String[] args) throws Exception {
        Header header = new Header();
        header.setLength(10);
        header.setPriority((byte) 1);
        header.setSessionID(13231);
        header.setType((byte) 6);

        NettyMessage nettyMessage = new NettyMessage();
        nettyMessage.setHeader(header);

        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();

        NettyMessageEncoder encoder = new NettyMessageEncoder();
        encoder.encode(null,nettyMessage,buf);

        EmbeddedChannel channel =new EmbeddedChannel(
                new NettyMessageDecoder(1024,4,4),
                new LoggingHandler(LogLevel.DEBUG)
        );
        channel.writeInbound(buf);
    }
}
