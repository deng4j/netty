package com.example.mynettyprotocol.server.domain;

import com.example.mynettyprotocol.server.domain.Header;

/**
 * netty消息
 */
public final class NettyMessage {
    private Header header;
    private Object body;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "NettyMessage[header=" + header + ']';
    }
}
