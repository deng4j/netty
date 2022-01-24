package com.dengzhihong.netty.netty.n9_byteBuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

import static com.dengzhihong.netty.netty.n9_byteBuf.LogUtil.log;

/**
 * CompositeByteBuf:
 * 1.【零拷贝】的体现之一，可以将多个 ByteBuf 合并为一个逻辑上的 ByteBuf，避免拷贝
 * 2.CompositeByteBuf 是一个组合的 ByteBuf，它内部维护了一个 Component 数组，
 *   每个 Component 管理一个 ByteBuf，记录了这个 ByteBuf 相对于整体偏移量等信息，代表着整体中某一段的数据。
 *     >优点，对外是一个虚拟视图，组合这些 ByteBuf 不会产生内存复制
 *     >缺点，复杂了很多，多次操作会带来性能的损耗
 */
public class TestCompositeBytebuf {

    public static void main(String[] args) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        buf.writeBytes("cftgj45613vgh".getBytes());
        ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer();
        buf1.writeBytes("tcyviubh456132".getBytes());

        ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer();
        //writeBytes()会发生数据复制
        buf2.writeBytes(buf).writeBytes(buf1);
        log(buf2);
        //----------------------------------------------
        ByteBuf buf3 = ByteBufAllocator.DEFAULT.buffer();
        buf3.writeBytes("tcfvgb888888".getBytes());
        ByteBuf buf4 = ByteBufAllocator.DEFAULT.buffer();
        buf4.writeBytes("jbctghj999999".getBytes());

        //addComponents()不会发生复制,设置写指针自动增长为true
        CompositeByteBuf compositeByteBuf = ByteBufAllocator.DEFAULT.compositeBuffer();
        compositeByteBuf.addComponents(true,buf3,buf4);
        log(compositeByteBuf);
    }
}
