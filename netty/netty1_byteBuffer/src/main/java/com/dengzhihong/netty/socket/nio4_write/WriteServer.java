package com.dengzhihong.netty.socket.nio4_write;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * 向客户端写数据
 */
public class WriteServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        ssc.bind(new InetSocketAddress(8080));

        while (true){
            selector.select();
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()){
                SelectionKey key = iter.next();
                iter.remove();
                if (key.isAcceptable()){
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    SelectionKey scKey = sc.register(selector, 0, null);
                    scKey.interestOps(SelectionKey.OP_READ);

                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 20000000; i++) {
                        sb.append("b");
                    }
                    ByteBuffer buffer = Charset.defaultCharset().encode(sb.toString());
                    int write = sc.write(buffer);
                    System.out.println(write);

                    //判断是否有剩余内容
                    if (buffer.hasRemaining()){
                        //没有写完，关注可写事件,read为1，write为4，变成关注读+关注写
                        scKey.interestOps(scKey.interestOps()+SelectionKey.OP_WRITE);
                        //把未写完的数据挂到scKey上,等到可写事件时写
                        scKey.attach(buffer);
                    }
                }else if (key.isWritable()){
                    //完成上面未写完的事件
                    ByteBuffer buffer = (ByteBuffer)key.attachment();
                    SocketChannel sc = (SocketChannel)key.channel();
                    int write = sc.write(buffer);
                    System.out.println(write);
                    //数据写完，需要把buffer去除
                    if (!buffer.hasRemaining()){
                        key.attach(null);
                        //不需要继续关注write事件
                        key.interestOps(key.interestOps()-SelectionKey.OP_WRITE);
                        key.cancel();
                    }
                }
            }
        }
    }
}
