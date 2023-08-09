package com.example.mynettyprotocol.server.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求头
 */
public final class Header implements Serializable {
    private static final long serialVersionUID = 1L;

    private int crcCode = 0xabef0101;
    private int length=26; // 消息长度，最少22
    private long sessionID; //会话ID
    private byte type; // 消息类型
    private byte priority; // 消息优先级
    private Map<String,Object> attachment = new HashMap<>();

    public int getCrcCode() {
        return crcCode;
    }

    public void setCrcCode(int crcCode) {
        this.crcCode = crcCode;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public long getSessionID() {
        return sessionID;
    }

    public void setSessionID(long sessionID) {
        this.sessionID = sessionID;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public Map<String, Object> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, Object> attachment) {
        this.attachment = attachment;
    }

    @Override
    public String toString() {
        return "Header[" +
                "crcCode=" + crcCode +
                ", length=" + length +
                ", sessionID=" + sessionID +
                ", type=" + type +
                ", priority=" + priority +
                ", attachment=" + attachment +
                ']';
    }
}
