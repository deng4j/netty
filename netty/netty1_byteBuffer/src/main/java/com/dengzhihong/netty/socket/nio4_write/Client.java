package com.dengzhihong.netty.socket.nio4_write;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Client {

    public static void main(String[] args) throws IOException {
        SocketChannel sc=SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost",8080));
        System.out.println("waiting...");
        int count=0;
        while (true){
            ByteBuffer buffer = ByteBuffer.allocate(1024*1024);
            count+= sc.read(buffer);
            buffer.clear();
            System.out.println(count);
        }
    }
}
