package com.dengzhihong.netty.netty.n7_byteBuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

/**
 * Unpooled:
 * 1.Unpooled 是一个工具类，类如其名，提供了非池化的 ByteBuf 创建、组合、复制等操作
 */
public class Unpooled1 {

    public static void main(String[] args) {
        ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer(5);
        buf1.writeBytes(new byte[]{1, 2, 3, 4, 5});
        ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer(5);
        buf2.writeBytes(new byte[]{6, 7, 8, 9, 10});

        // 当包装 ByteBuf 个数超过一个时, 底层使用了 CompositeByteBuf
        ByteBuf buf3 = Unpooled.wrappedBuffer(buf1, buf2);
        System.out.println(ByteBufUtil.prettyHexDump(buf3));
    }
}
