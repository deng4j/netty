package com.dengzhihong.netty.socket.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import static com.dengzhihong.netty.utils.ByteBufferUtil.debugRead;

/**
 * 使用nio理解非阻塞，但实际不会这么用，因为这个线程一直在跑
 */
@Slf4j
public class Server {

    public static void main(String[] args) throws IOException {
        //0.ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);
        //1.创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //ServerSocketChannel是阻塞的，现在改为false非阻塞，影响的是accept()方法
        ssc.configureBlocking(false);
        //2.绑定监听端口
        ssc.bind(new InetSocketAddress(8080));
        //3.连接集合
        ArrayList<SocketChannel> channels = new ArrayList<>();
        while (true){
            /**
             * 4.现在是非阻塞，线程运行到这，如果没有建立连接，会继续走，但sc==null。
             * 建立了一个连接后，sc==null，等待下一个连接
             */
            SocketChannel sc = ssc.accept();
            if (null!=sc) {
                //SocketChannel设为非阻塞，影响的是read()方法
                log.info("connected...{}",sc);
                sc.configureBlocking(false);
                channels.add(sc);
            }
            for (SocketChannel channel: channels) {
                //5.read()没有读到数据，返回0
                int read = channel.read(buffer);
                if (read>0) {
                    buffer.flip();
                    debugRead(buffer);
                    buffer.clear();
                    log.info("after read...{}",channel);
                }
            }
        }
    }
}
