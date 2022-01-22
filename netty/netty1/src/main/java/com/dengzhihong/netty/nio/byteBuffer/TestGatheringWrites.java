package com.dengzhihong.netty.nio.byteBuffer;

import lombok.extern.slf4j.Slf4j;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

import static com.dengzhihong.netty.nio.utils.ByteBufferUtil.debugAll;

/**
 * 集中写文件：
 * 1.方式一：将3个ByteBuffer的数据集中到一个大的ByteBuffer
 * 2.方式二：将3个ByteBuffer合成一个
 */
@Slf4j
public class TestGatheringWrites {

    public static void main(String[] args) {
        try {
            //准备3个ByteBuffer
            ByteBuffer buffer1 = StandardCharsets.UTF_8.encode("你好");
            ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("奥特曼");
            ByteBuffer buffer3 = StandardCharsets.UTF_8.encode("你相信光吗");
            //把3个ByteBuffer中的数据写到一个文件中
            FileChannel fileChannel = new RandomAccessFile("D:\\ideasX\\project2\\netty\\netty1_byteBuffer\\src\\main\\resources\\words2.txt", "rw").getChannel();
            fileChannel.write(new ByteBuffer[]{buffer1,buffer2,buffer3});

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
