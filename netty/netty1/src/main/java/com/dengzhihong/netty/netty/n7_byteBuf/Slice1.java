package com.dengzhihong.netty.netty.n7_byteBuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static com.dengzhihong.netty.netty.n7_byteBuf.LogUtil.log;

/**
 * 一.Slice
 * 1.【零拷贝】的体现之一，对原始 ByteBuf 进行切片成多个 ByteBuf，切片后的 ByteBuf 并没有发生内存复制，
 * 还是使用原始 ByteBuf 的内存，切片后的 ByteBuf 维护独立的 read，write 指针。
 * 2.不允许对已经切片后的切片进行写入操作，因为会影响其他切片：
 *  无参 slice 是从原始 ByteBuf 的 read index 到 write index 之间的内容进行切片，
 *  切片后的 max capacity 被固定为这个区间的大小，因此不能追加 write
 * 二.duplicate
 * 【零拷贝】的体现之一，就好比截取了原始 ByteBuf 所有内容，并且没有 max capacity 的限制，
 * 也是与原始 ByteBuf 使用同一块底层内存，只是读写指针是独立的
 * 三.copy
 * 会将底层内存数据进行深拷贝，因此无论读写，都与原始 ByteBuf 无关
 */
public class Slice1 {

    public static void main(String[] args) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
        buf.writeBytes("abcdefg".getBytes());
        log(buf);
        //从0开始切片，长度为4。
        ByteBuf buf1 = buf.slice(0, 4);
        ByteBuf buf2 = buf.slice(4, 3);
        log(buf1);
        log(buf2);
        //切片过程中，没有发生数据复制
        System.out.println("-----------测试切片------------");
        buf1.setByte(0,'q');
        log(buf1);
        log(buf);
    }
}
