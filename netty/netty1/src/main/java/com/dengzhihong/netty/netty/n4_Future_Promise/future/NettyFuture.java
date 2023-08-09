package com.dengzhihong.netty.netty.n4_Future_Promise.future;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

/**
 * netty Future 可以同步等待任务结束得到结果，也可以异步方式得到结果，但都是要等任务结束
 */
@Slf4j
public class NettyFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        EventLoop eventLoop = group.next();
        Future<Integer> future = eventLoop.submit(() -> {
            log.info("执行计算");
            return 100;
        });
        addListener(future);
    }

    /**
     * 异步接收结果
     * @param future
     */
    private static void addListener(Future<Integer> future) {
        future.addListener(new GenericFutureListener<Future<? super Integer>>() {
            @Override
            public void operationComplete(Future<? super Integer> future) throws Exception {
                //获取任务结果，非阻塞，还未产生结果时返回 null
                Object num = future.getNow();
                log.info("执行结果：{}",num);
            }
        });
    }

    /**
     * 阻塞得到结果
     */
    private static void get(Future<Integer> future) throws InterruptedException, ExecutionException {
        //get()阻塞方法
        Integer num = future.get();
        log.info("执行结果：{}",num);
    }
}
