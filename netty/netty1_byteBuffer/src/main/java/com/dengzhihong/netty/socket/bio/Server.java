package com.dengzhihong.netty.socket.bio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import static com.dengzhihong.netty.utils.ByteBufferUtil.debugRead;

/**
 * 使用nio理解阻塞
 */
@Slf4j
public class Server {

    public static void main(String[] args) throws IOException {
        //0.ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);
        //1.创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //2.绑定监听端口
        ssc.bind(new InetSocketAddress(8080));
        //3.连接集合
        ArrayList<SocketChannel> channels = new ArrayList<>();
        while (true){
            log.info("connecting...");
            //4.accept建立与客户端连接(阻塞),SocketChannel用于与客户端之间通信
            SocketChannel sc = ssc.accept();
            log.info("connected...{}",sc);
            channels.add(sc);
            for (SocketChannel channel: channels) {
                //5.接收客户端发送的数据,read()阻塞方法
                log.info("before read...{}",channel);
                channel.read(buffer);
                buffer.flip();
                debugRead(buffer);
                buffer.clear();
                log.info("after read...{}",channel);
            }
        }
    }
}
