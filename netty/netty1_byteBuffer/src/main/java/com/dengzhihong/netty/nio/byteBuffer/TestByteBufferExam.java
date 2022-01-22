package com.dengzhihong.netty.nio.byteBuffer;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

import static com.dengzhihong.netty.nio.utils.ByteBufferUtil.debugAll;

/**
 * 网络上有多条数据发送给服务端，数据之间使用\n进行分隔，但由于某种原因
 * 这些数据在接收时被重新组合了，例如原数据有3条为：
 * hello，i`m your student\n
 * I could`t ask you  a question? \n
 * Can`t you help me?\n
 *
 * 变成了两个ByteBuffer（粘包、半包）：
 * hello，i`m your student\nI could`t ask you  a question? \nCa
 * n`t you help me?\n
 */
@Slf4j
public class TestByteBufferExam {

    public static void main(String[] args) {
        ByteBuffer source = ByteBuffer.allocate(100);
        source.put("hello，i`m your student\nI could`t ask you  a question? \nCa".getBytes());
        split(source);
        source.put("n`t you help me?\n".getBytes());
        split(source);
    }

    private static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            if (source.get(i)=='\n') {
                int length=i+1-source.position();
                ;//获取到了一条完整信息，并存入新的ByteBuffer
                ByteBuffer target = ByteBuffer.allocate(length);
                //从source读，向target写
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                debugAll(target);
            }
        }
        source.compact();
    }
}
