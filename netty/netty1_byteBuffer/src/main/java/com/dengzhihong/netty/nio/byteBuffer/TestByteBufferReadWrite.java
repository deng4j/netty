package com.dengzhihong.netty.nio.byteBuffer;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import static com.dengzhihong.netty.nio.utils.ByteBufferUtil.debugAll;

/**
 *向 buffer 写入数据:
 * 1.调用 channel 的 read 方法: int readBytes = channel.read(buf);
 * 2.调用 buffer 自己的 put 方法: buf.put((byte)127);
 *从 buffer 读取数据:
 * 1.调用 channel 的 write 方法: int writeBytes = channel.write(buf);
 * 2.调用 buffer 自己的 get 方法: byte b = buf.get();
 *      (1)get 方法会让 position 读指针向后走，如果想重复读取数据
 *      (2)可以调用 rewind 方法将 position 重新置为 0
 *      (3)或者调用 get(int i) 方法获取索引 i 的内容，它不会移动读指针
 */
@Slf4j
public class TestByteBufferReadWrite {

    public static void main(String[] args) {
        //使用 allocate 方法为 ByteBuffer 分配空间
        ByteBuffer buffer = ByteBuffer.allocate(10);
        byte b1=0x61;
        buffer.put(b1);
        byte[] bArr={0x62,0x63,0x64,0x65};
        buffer.put(bArr);
        //使用调试类
        debugAll(buffer);
        //这时候读数据，是读不到数据的，因为position现在是5，也就是0x65后面一位
        //log.info("不切读模式直接读：{}",buffer.get());
        //不切换读模式直接读是会出出错
        debugAll(buffer);
        //切换到读模式
        buffer.flip();
        log.info("切读模式读到的十进制：{}",buffer.get());
        debugAll(buffer);
        //切换到写的压缩模式
        log.info("切换到写的压缩模式");
        buffer.compact();
        debugAll(buffer);
        //写一个数据
        log.info("切换到写的压缩模式后写数据");
        buffer.put(new byte[]{0x66,0x67});
        debugAll(buffer);
    }
}
