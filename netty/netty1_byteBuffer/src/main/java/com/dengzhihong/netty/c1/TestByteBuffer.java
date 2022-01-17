package com.dengzhihong.netty.c1;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * ByteBuffer内部结构：
 * 1.position：指针
 * 2.Limit：写入限制
 * 3.Capacity：容量
 * 写模式clear()：position是写入位置，limit等于容量
 * 读模式flip()：position是读取位置，limit切换为读取限制
 * 写模式compact()：是把为读完的部分向前压缩，然后切换到写模式
 *
 */
@Slf4j
public class TestByteBuffer {

    public static void main(String[] args) {
        try {
            //FileChannel代表一个数据读写的通道
            //获取方式：1.输入输出流获取；2.RandomAccessFile获取
            FileChannel channel = new FileInputStream("D:\\ideasX\\project2\\netty\\netty1_byteBuffer\\src\\main\\resources\\data.txt").getChannel();
            //准备一个缓冲区,由allocate()划分一块内存，这里10个字节
            ByteBuffer buffer=ByteBuffer.allocate(10);
            while (true) {
                //从channel读数据，向buffer写入，如果返回值是-1就读完了
                int len = channel.read(buffer);
                log.info("读取的字节长度：{}",len);
                if (len==-1){
                    break;
                }
                //打印buffer中的内容,flip()切换至读模式
                buffer.flip();
                //是否有剩余的数据
                while (buffer.hasRemaining()){
                    byte b = buffer.get();
                    //每次从buffer中读10个字节
                    log.info("读取的字符：{}",(char) b);
                }
                //切换为写模式，重新写入到buffer
                buffer.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
