package com.dengzhihong.netty.aio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static com.dengzhihong.netty.utils.ByteBufferUtil.debugAll;

/**
 * 文件AIO
 */
@Slf4j
public class AioFile {
    public static void main(String[] args) throws IOException {
        try{
            AsynchronousFileChannel s = AsynchronousFileChannel.open(Paths.get("D:\\ideasX\\project2\\netty\\netty1_byteBuffer\\src\\main\\resources\\data.txt"), StandardOpenOption.READ);
            ByteBuffer buffer = ByteBuffer.allocate(16);
            log.debug("begin...");
            /**
             * 参数一：ByteBuffer
             * 参数二：读取的起始位置
             * 参数三：附件
             * 参数四：回调对象
             * read执行是使用守护线程执行的，所以如果主线程执行完毕，守护线程也会停止
             */
            s.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                //read成功调用这个
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    log.debug("read completed...{}", result);
                    attachment.flip();
                    debugAll(attachment);
                }
                //read失败调这个
                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    log.debug("read failed...");
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        log.debug("do other things...");
        //避免守护线程意外结束
        System.in.read();
    }
}