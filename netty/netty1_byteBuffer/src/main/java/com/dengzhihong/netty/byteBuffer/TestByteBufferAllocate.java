package com.dengzhihong.netty.byteBuffer;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

/**
 * 内核缓冲区，ByteBuffer用户缓冲区，socket缓冲区
 *
 */
@Slf4j
public class TestByteBufferAllocate {

    public static void main(String[] args) {
        //开辟后就不能调整了
        ByteBuffer buffer = ByteBuffer.allocate(16);
        ByteBuffer bufferDirect = ByteBuffer.allocateDirect(16);
        log.info("allocate()创建的ByteBuffer：{}",buffer.getClass());
        log.info("allocateDirect()创建的ByteBuffer：{}",bufferDirect.getClass());
        /**
         * class java.nio.HeapByteBuffer: java堆内存，读写效率较低，受垃圾回收GC的影响。
         * class java.nio.DirectByteBuffer:
         * 优点：操作系统内存，读写效率较高，因为不会受GC影响，而且会少一次拷贝。
         * 缺点：分配内存效率低，还可能会内存泄漏。
         */

    }
}
