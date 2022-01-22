package com.dengzhihong.netty.nio.socket.nio2_selector;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import static com.dengzhihong.netty.nio.utils.ByteBufferUtil.debugRead;

/**
 * 一.使用多路复用器selector优化。
 * 二. 事件：
 * 1.accept：有连接请求时触发
 * 2.connect：客户端连接建立后触发
 * 3.read：读到客户端发送的数据时触发
 * 4.write：可写事件
 *
 * 三.cancel：cancel 会取消注册在 selector 上的 channel，并从 keys 集合中删除 key 后续不会再监听事件。
 *
 * 四.为何要 iter.remove():
 * 因为 select 在事件发生后，就会将相关的 key 放入 selectedKeys 集合，
 * 但不会在处理完后从 selectedKeys 集合中移除，需要我们自己编码删除。
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
                    SelectionKey scKey = sc.register(selector, 0, null);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("{}",sc);
                }else if (key.isReadable()){
                    try {
                        SocketChannel channel = (SocketChannel)key.channel();
                        /**
                         * 这个buffer一次只能装4个字节，装不下那就会读取多次。
                         * 消息边界问题：如果是中文（3个字节），那么第二个字前1个字节和后2个字节会被拆成两份(乱码)。
                         */
                        ByteBuffer buffer = ByteBuffer.allocate(4);
                        //正常断开read返回值为-1
                        int read = channel.read(buffer);
                        if (read==-1){
                            key.cancel();
                            continue;
                        }
                        buffer.flip();
                        debugRead(buffer);
                    } catch (IOException e) {
                        //客户端异常断开了，需要将key取消（从selectorKeys中真正删除key）
                        key.cancel();
                    }
                }
            }
        }
    }
}
