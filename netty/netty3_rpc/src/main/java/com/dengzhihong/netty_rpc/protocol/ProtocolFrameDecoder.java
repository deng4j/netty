package com.dengzhihong.netty_rpc.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 用于简化和固定LengthFieldBasedFrameDecoder
 */
public class ProtocolFrameDecoder extends LengthFieldBasedFrameDecoder {

    public ProtocolFrameDecoder(){
        this(1024,12,4,0,0);
    }

    public ProtocolFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
