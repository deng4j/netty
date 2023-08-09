package com.dengzhihong.netty.netty.n4_Future_Promise.promise;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

/**
 * 异步处理任务失败
 */
@Slf4j
public class NettyPromise_async {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //准备一个EventLoop对象
        EventLoop group = new NioEventLoopGroup().next();
        //主动创建Promise，结果容器
        DefaultPromise<Integer> promise= new DefaultPromise<>(group);

        promise.addListener(future -> {
            log.debug("result {}", (promise.isSuccess() ? promise.getNow() : promise.cause()).toString());
        });

        //计算并想promise填充结果
        new Thread(()->{
            try {
                log.info("开始计算...");
                int i = 1/0;
            } catch (Exception e) {
                e.printStackTrace();
                //告诉主线程出异常
                promise.setFailure(e);
            }
        }).start();
        //接收结果
        log.info("非阻塞等待结果，还没promise还没填充结果会返回null...");
        log.info("非阻塞等待的结果：{}",promise.getNow());
    }
}
