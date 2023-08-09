package com.example.mynettyprotocol.server.messageDecoderEncoder;

import com.example.mynettyprotocol.server.domain.Header;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.jboss.marshalling.ByteOutput;
import org.jboss.marshalling.Marshaller;

import java.io.IOException;

/**
 * 消息中对象编码工具
 */
public class MarshallingEncoder {
    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    Marshaller marshaller;

    public MarshallingEncoder() throws IOException {
        this.marshaller = MarshallingCodecFactory.buildMarshalling();
    }

    protected void encode(Object msg, ByteBuf out) throws Exception {
        try {
            int lengthPos = out.writerIndex();
            out.writeBytes(LENGTH_PLACEHOLDER);
            ByteOutput output = new ChannelBufferByteOutput(out);
            marshaller.start(output);
            marshaller.writeObject(msg);
            marshaller.finish();
            out.setInt(lengthPos, out.writerIndex() - lengthPos - 4);
        } finally {
            marshaller.close();
        }
    }

    /**
     * 测试编解码
     */
    public static void main(String[] args) throws Exception {
        Header header = new Header();
        header.setLength(4);
        header.setPriority((byte) 1);
        header.setSessionID(13231);
        header.setType((byte) 6);

        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();

        MarshallingEncoder marshallingEncoder = new MarshallingEncoder();
        marshallingEncoder.encode(header,buf);

        MarshallingDecoder marshallingDecoder = new MarshallingDecoder();
        Object obj = marshallingDecoder.decode(buf);
        System.out.println(obj);
    }
}
