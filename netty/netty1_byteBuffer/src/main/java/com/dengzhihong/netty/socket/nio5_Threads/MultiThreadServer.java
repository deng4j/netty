package com.dengzhihong.netty.socket.nio5_Threads;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.dengzhihong.netty.utils.ByteBufferUtil.debugAll;

/**
 * 多线程执行读写,利用队列线程通信
 */
public class MultiThreadServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        Selector boss = Selector.open();
        SelectionKey bossKey = ssc.register(boss, 0, null);
        //boss负责建立连接
        bossKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));
        //创建固定数量的worker
        Worker worker = new Worker("worker-0");
        while (true){
            boss.select();
            Iterator<SelectionKey> iter = boss.selectedKeys().iterator();
            while (iter.hasNext()){
                SelectionKey key = iter.next();
                iter.remove();
                if (key.isAcceptable()){
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    /**
                     * 1.由于worker线程和boss线程不是一个，为避免boss线程阻塞导致worker线程停止工作，
                     * 所以让sc.register(worker.selector,SelectionKey.OP_READ,null);在worker线程中运行
                     */
                    worker.register(sc);
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
        private ConcurrentLinkedQueue<Runnable> queue=new ConcurrentLinkedQueue<>();

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
            //3.向队列添加任务，并没有立刻执行
            queue.add(()->{
                //关联selector
                try {
                    sc.register(this.selector,SelectionKey.OP_READ,null);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });
            //5.唤醒selector
            selector.wakeup();
        }

        @Override
        public void run() {
            while (true){
                try {
                    //4.避免阻塞导致注册失败，注册前唤醒一下
                    selector.select();
                    Runnable task = queue.poll();
                    if (null!=task) {
                        //6.在这执行注册
                        task.run();
                    }
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
