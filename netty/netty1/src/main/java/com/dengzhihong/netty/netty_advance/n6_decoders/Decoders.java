package com.dengzhihong.netty.netty_advance.n6_decoders;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;

public class Decoders {

    public static void main(String[] args) {
        // 以 $_ 为分隔符作为码流结束标识的消息解码
        ByteBuf copiedBuffer = Unpooled.copiedBuffer("$_".getBytes());
        new DelimiterBasedFrameDecoder(1024,copiedBuffer);

        // 定长解码器，长度设置为20
        new FixedLengthFrameDecoder(20);
    }
}
