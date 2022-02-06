package com.dengzhihong.netty_rpc.server.service.impl;

import com.dengzhihong.netty_rpc.server.service.HelloService;

public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String msg) {
        return "你好"+msg;
    }
}
