package com.dengzhihong.netty.nio.fileChannel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * FileChannel:
 *1.FileChannel 只能工作在阻塞模式下
 *2.不能直接打开 FileChannel:
 *  通过 FileInputStream 获取的 channel 只能读
 *  通过 FileOutputStream 获取的 channel 只能写
 *  通过 RandomAccessFile 是否能读写根据构造 RandomAccessFile 时的读写模式决定
 *
 */
public class TestFileChannelTransfer {

    public static void main(String[] args) {
        try {
            FileChannel from = new FileInputStream("D:\\ideasX\\project2\\netty\\netty1_byteBuffer\\src\\main\\resources\\fileChannelRead.txt").getChannel();
            FileChannel to = new FileOutputStream("D:\\ideasX\\project2\\netty\\netty1_byteBuffer\\src\\main\\resources\\fileChannelWrite.txt").getChannel();

            long size= from.size();
            //left代表剩余多少字节
            for (long left = size; left >0 ; ) {
                //效率高，底层利用操作系统的零拷贝进行优化,一次2g数据
                long transferNum = from.transferTo((size-left), from.size(), to);
                left-=transferNum;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
