package com.dengzhihong.netty.netty.n4_Future_Promise.future;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * jdk Future 只能同步等待任务结束（或成功、或失败）才能得到结果
 */
@Slf4j
public class JdkFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        Future<Integer> future = pool.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.info("执行计算");
                return 100;
            }
        });
        //get()会阻塞等待结果
        Integer num = future.get();
        log.info("执行结果：{}",num);
    }
}
