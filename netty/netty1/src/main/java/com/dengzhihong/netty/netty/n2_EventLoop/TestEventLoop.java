package com.dengzhihong.netty.netty.n2_EventLoop;

import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * EventLoop事件循环对象:
 * 1.EventLoop 本质是一个单线程执行器（同时维护了一个 Selector），里面有 run 方法处理 Channel 上源源不断的 io 事件。
 * 2.继承关系比较复杂:
 *   >一条线是继承自 j.u.c.ScheduledExecutorService 因此包含了线程池中所有的方法。
 *   >另一条线是继承自 netty 自己的 OrderedEventExecutor：
 *    >>提供了 boolean inEventLoop(Thread thread) 方法判断一个线程是否属于此 EventLoop。
 *    >>提供了 parent 方法来看看自己属于哪个 EventLoopGroup。
 * 3.EventLoopGroup 是一组 EventLoop，Channel 一般会调用 EventLoopGroup 的 register 方法来绑定其中一个 EventLoop，
 *   后续这个 Channel 上的 io 事件都由此 EventLoop 来处理（保证了 io 事件处理时的线程安全）。
 * 4.继承自 netty 自己的 EventExecutorGroup：
 *   > 提供了 boolean inEventLoop(Thread thread) 方法判断一个线程是否属于此 EventLoop。
 *   > 提供了 parent 方法来看看自己属于哪个 EventLoopGroup。
 */
@Slf4j
public class TestEventLoop {

    public static void main(String[] args) {
        //1.创建事件循环组
        NioEventLoopGroup group = new NioEventLoopGroup(2); //可以处理io事件、普通任务、定时任务
        //DefaultEventLoopGroup defaultGroup = new DefaultEventLoopGroup();//普通任务、定时任务
        //2.获取下一个事件循环对象
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());
        //3.执行一个普通任务
        group.next().submit(()->{
            log.info("执行普通任务");
        });
        //4.执行定时任务
        group.next().scheduleAtFixedRate(()->{log.info("定时任务:2秒后执行，每1秒执行一次");},2,1, TimeUnit.SECONDS);
    }
}
