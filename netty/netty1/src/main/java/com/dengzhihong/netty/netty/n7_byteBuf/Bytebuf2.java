package com.dengzhihong.netty.netty.n7_byteBuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

import java.nio.charset.StandardCharsets;

public class Bytebuf2 {
    public static void main(String[] args) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        buf.writeInt(10); // 写进去4个字节
        buf.writeBytes("234".getBytes(StandardCharsets.UTF_8));
        System.out.println(buf.readInt()); // 读取4个字节
        System.out.println(buf.readerIndex()); // 返回读索引
        System.out.println((char) buf.getByte(4)); // 读取指定索引未
    }

    private static void m1() {
        ByteBuf buf = Unpooled.copiedBuffer("abcdef", CharsetUtil.UTF_8);
        System.out.println("当前可读区 " + buf.readableBytes());
        System.out.println("当前可写区" + buf.writableBytes());
        buf.readBytes(4);
        System.out.println("-------读4个字节之后-----------------");
        System.out.println("当前可读区 " + buf.readableBytes());
        System.out.println("当前可写区" + buf.writableBytes());
        System.out.println("-------执行discardReadBytes()之后-----------------");
        buf.discardReadBytes();
        System.out.println("当前可读区 " + buf.readableBytes());
        System.out.println("当前可写区" + buf.writableBytes());
        System.out.println("-------此时若是移动读索引，就可能读到之前没清空的数据-----------------");
        //读索引移动后如果比写索引大了，那就回报错，所以要先移动写索引
        buf.writerIndex(4);
        buf.readerIndex(3);
        System.out.println("当前字节(本该在被删除但其实没有)："+(char)buf.readByte());
        System.out.println("当前可读区 " + buf.readableBytes());
        System.out.println("当前可写区" + buf.writableBytes());
    }
}
