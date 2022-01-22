package com.dengzhihong.netty.nio.socket.nio3_boundary;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import static com.dengzhihong.netty.nio.utils.ByteBufferUtil.debugAll;
import static com.dengzhihong.netty.nio.utils.ByteBufferUtil.debugRead;

/**
 * 一.边界问题处理：
 * 1.一种思路是固定消息长度，数据包大小一样，服务器按预定长度读取，缺点是浪费带宽
 * 2.另一种思路是按分隔符拆分，缺点是效率低（这里采用）
 * 3.TLV 格式，即 Type 类型、Length 长度、Value 数据，类型和长度已知的情况下，就可以方便获取
 * 消息大小，分配合适的 buffer，缺点是 buffer 需要提前分配，如果内容过大，则影响 server 吞吐量
 *      Http 1.1 是 TLV 格式
 *      Http 2.0 是 LTV 格式
 *
 * 二.每个SocketChannel都应该有一个自己的附件ByteBuffer
 */
@Slf4j
public class Server {

    public static void main(String[] args) throws IOException {
        //1.创建selector,管理多个channel
        Selector selector = Selector.open();

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        //2.channel注册到selector,可以通过返回的selectionKey知道是哪个channel的事件
        SelectionKey sscKey = ssc.register(selector, 0, null);
        //这个key只关注accept事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        log.info("register key:{}",sscKey);

        ssc.bind(new InetSocketAddress(8080));
        while (true){
            //3.没有事件发生，会让线程阻塞,事件未被处理会重新添加到Set集合，除非使用cancel()取消事件
            selector.select();
            //4.处理事件，selectedKeys()拿到事件Set集合
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()){
                SelectionKey key = iter.next();
               //从selectedKeys中删除，key发生事件后selector不会主动删除key
                iter.remove();
                log.info("key:{}",key);
                //5.区分事件类型
                if (key.isAcceptable()) {
                    //拿到触发事件的channel
                    ServerSocketChannel channel = (ServerSocketChannel)key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(5);
                    //给这个一个这个SelectionKey关联一个ByteBuffer附件
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("{}",sc);
                }else if (key.isReadable()){
                    try {
                        SocketChannel channel = (SocketChannel)key.channel();
                        //拿到这个key关联的附件
                        ByteBuffer buffer = (ByteBuffer)key.attachment();
                        //正常断开read返回值为-1,读完了为0
                        int read = channel.read(buffer);
                        if (read==-1||read==0){
                            key.cancel();
                            continue;
                        }
                        split(buffer);
                        //如果buffer满了，需要扩容
                        if (buffer.position()==buffer.capacity()){
                            ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                            buffer.flip();
                            newBuffer.put(buffer);
                            key.attach(newBuffer);
                        }
                    } catch (IOException e) {
                        //客户端异常断开了，需要将key取消（从selectorKeys中真正删除key）
                        key.cancel();
                    }
                }
            }
        }
    }

    private static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            if (source.get(i)=='\n') {
                int length=i+1-source.position();
                ;//获取到了一条完整信息，并存入新的ByteBuffer
                ByteBuffer target = ByteBuffer.allocate(length);
                //从source读，向target写
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                debugAll(target);
            }
        }
        source.compact();
    }
}
