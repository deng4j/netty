package com.dengzhihong.netty_rpc.protocol;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * id计数器，保证id唯一
 */
public class SequenceIdGenerator {
    private static final AtomicInteger id=new AtomicInteger();

    public static int nextId(){
        return id.incrementAndGet();
    }
}
