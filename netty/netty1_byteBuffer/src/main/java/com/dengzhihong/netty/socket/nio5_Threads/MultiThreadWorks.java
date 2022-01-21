package com.dengzhihong.netty.socket.nio5_Threads;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import static com.dengzhihong.netty.utils.ByteBufferUtil.debugAll;

/**
 * 利用多个Worker：
 * Work数应该为cup数量：Runtime.getRuntime().availableProcessors();
 * 如果工作在 docker 容器下，因为容器不是物理隔离的，会拿到物理 cpu 个数，而不是容器申请时的个数。
 * 这个问题直到 jdk 10 才修复，使用 jvm 参数 UseContainerSupport 配置， 默认开启
 */
public class MultiThreadWorks {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        Selector boss = Selector.open();
        SelectionKey bossKey = ssc.register(boss, 0, null);
        //boss负责建立连接
        bossKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));
        //创建固定worker数组
        Worker[] workers=new Worker[2];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker("worker-"+i);
        }
        AtomicInteger index=new AtomicInteger();
        while (true){
            boss.select();
            Iterator<SelectionKey> iter = boss.selectedKeys().iterator();
            while (iter.hasNext()){
                SelectionKey key = iter.next();
                iter.remove();
                if (key.isAcceptable()){
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    //0和1均匀分配
                    workers[index.getAndIncrement() % workers.length].register(sc);
                }
            }
        }
    }

    /**
     * 负责读写
     */
    static class Worker implements Runnable{
        private Thread thread;
        private Selector selector;
        private String name;
        //判断是否已经初始化
        private volatile boolean start=false;

        public Worker(String name){
            this.name=name;
        }

        //初始化线程和Selector

        /**
         *2.这个方法也是在boss线程中执行的，只有当执行了start()方法，run()方法才在worker线程中执行。
         * 为了让sc.register(this.selector,SelectionKey.OP_READ,null);在run()中执行，使用队列解耦。
         */
        public void register(SocketChannel sc) throws IOException {
            if (!start) {
                this.thread=new Thread(this,name);
                this.selector=Selector.open();
                this.thread.start();
                this.start=true;
            }
            //3.wakeup就像一张发票，只要给了select()就不会阻塞，运行完后票就没了，所以不管两个的顺序是什么都可以注册成功
            selector.wakeup();
            sc.register(this.selector,SelectionKey.OP_READ,null);
        }

        @Override
        public void run() {
            while (true){
                try {
                    //4.避免阻塞导致注册失败，注册前唤醒一下
                    selector.select();
                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                    while (iter.hasNext()){
                        SelectionKey key = iter.next();
                        iter.remove();
                        if (key.isReadable()){
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            SocketChannel channel = (SocketChannel)key.channel();
                            channel.read(buffer);
                            buffer.flip();
                            debugAll(buffer);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
