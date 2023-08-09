package com.dengzhihong.netty.netty.n4_Future_Promise.promise;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

/**
 * await 死锁检查
 */
@Slf4j
public class NettyPromise_await_check {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //准备一个EventLoop对象
        EventLoop group = new NioEventLoopGroup().next();
        //主动创建Promise，结果容器
        DefaultPromise<Integer> promise= new DefaultPromise<>(group);

        group.submit(()->{
            System.out.println("1");
            try {
                promise.await();
                // 注意不能仅捕获 InterruptedException 异常
                // 否则 死锁检查抛出的 BlockingOperationException 会继续向上传播
                // 而提交的任务会被包装为 PromiseTask，它的 run 方法中会 catch 所有异常然后设置为 Promise 的失败结果而不会抛出
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("2");
        });
        group.submit(()->{
            System.out.println("3");
            try {
                promise.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("4");
        });
    }
}
