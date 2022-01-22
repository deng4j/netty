package com.dengzhihong.netty.nio.byteBuffer;

import lombok.extern.slf4j.Slf4j;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static com.dengzhihong.netty.nio.utils.ByteBufferUtil.debugAll;

/**
 * 分散读取案例
 */
@Slf4j
public class TestScatteringReads {

    public static void main(String[] args) {
        try {
            //准备3个ByteBuffer
            ByteBuffer buffer1 = ByteBuffer.allocate(3);
            ByteBuffer buffer2 = ByteBuffer.allocate(3);
            ByteBuffer buffer3 = ByteBuffer.allocate(5);
            //把文件数据一次读到3个ByteBuffer中
            FileChannel fileChannel = new RandomAccessFile("D:\\ideasX\\project2\\netty\\netty1_byteBuffer\\src\\main\\resources\\words.txt", "r").getChannel();
            fileChannel.read(new ByteBuffer[]{buffer1,buffer2,buffer3});
            log.info("buffer1：");
            debugAll(buffer1);
            log.info("buffer2：");
            debugAll(buffer2);
            log.info("buffer3：");
            debugAll(buffer3);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
