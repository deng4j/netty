package com.dengzhihong.netty.netty.n9_byteBuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;

import static com.dengzhihong.netty.netty.n9_byteBuf.LogUtil.log;

/**
 * Bytebuf是ByteBuffer的增强，Bytebuf是可以动态扩容的。
 * 组成：capacity、max capacity、read index、write index
 * 可写部分：起始位置到容量或最大容量，随着不断写入，写指针往后移动
 * 可读部分：读指针到写指针部分，随着不断读取，读指针往后移动
 * 废弃部分：已读部分
 *
 * 池化：
 * 1.没有池化，则每次都得创建新的 ByteBuf 实例，这个操作对直接内存代价昂贵，就算是堆内存，也会增加 GC 压力
 * 2.有了池化，则可以重用池中 ByteBuf 实例，并且采用了与 jemalloc 类似的内存分配算法提升分配效率
 * 3.高并发时，池化功能更节约内存，减少内存溢出的可能
 * 4.开启池化(默认开启的)，系统环境变量来设置：
 *   > -Dio.netty.allocator.type={unpooled|pooled}
 *
 * retain & release：
 * 1.由于 Netty 中有堆外内存的 ByteBuf 实现，堆外内存最好是手动来释放，而不是等 GC 垃圾回收。
 *   >UnpooledHeapByteBuf 使用的是 JVM 内存，只需等 GC 回收内存即可
 *   >UnpooledDirectByteBuf 使用的就是直接内存了，需要特殊的方法来回收内存
 *   >PooledByteBuf 和它的子类使用了池化机制，需要更复杂的规则来回收内存
 * 2.Netty 这里采用了引用计数法来控制回收内存，每个 ByteBuf 都实现了 ReferenceCounted 接口
 *   >每个 ByteBuf 对象的初始计数为 1
 *   >调用 release 方法计数减 1，如果计数为 0，ByteBuf 内存被回收
 *   >调用 retain 方法计数加 1，表示调用者没用完之前，其它 handler 即使调用了 release 也不会造成回收
 *   >当计数为 0 时，底层内存会被回收，这时即使 ByteBuf 对象还在，其各个方法均无法正常使用
 * 3.有时候不清楚 ByteBuf 被引用了多少次，但又必须彻底释放，可以循环调用 release 直到返回 true
 */
@Slf4j
public class TestBytebuf {

    public static void main(String[] args) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        log(buf);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append("aaaa");
        }
        buf.writeBytes(sb.toString().getBytes());
        log(buf);
        //创建方式一：堆内存
        ByteBuf heapBuffer = ByteBufAllocator.DEFAULT.heapBuffer(10);
        /**
         * 创建方式二：直接内存(默认)
         * 1.直接内存对 GC 压力小，因为这部分内存不受 JVM 垃圾回收的管理，但也要注意及时主动释放
         * 2.直接内存创建和销毁的代价昂贵，但读写性能高（少一次内存复制），适合配合池化功能一起用
         */
        ByteBuf directBuffer = ByteBufAllocator.DEFAULT.directBuffer(10);
    }
}
