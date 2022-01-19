package com.dengzhihong.netty.socket.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Client {

    public static void main(String[] args) throws IOException {
        SocketChannel sc=SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost",8080));
        System.out.println("waiting...");
        sc.write(StandardCharsets.UTF_8.encode("hello"));
        sc.write(Charset.defaultCharset().encode("the world"));
    }
}
