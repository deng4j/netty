package com.dengzhihong.selfprotocol.protocol;

import com.dengzhihong.selfprotocol.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * ByteToMessageCodec或CombinedChannelDuplexHandler子类，不能加@Sharable注解，类中明确规定了
 */
@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {

    @Override
    public void encode(ChannelHandlerContext ctx, Message msg, ByteBuf buf) throws Exception {
        //设置4字节魔术
        buf.writeBytes(new byte[]{'b','a','b','y'});
        //设置版本
        buf.writeByte(1);
        //序列化算法：0==jdk，1==json
        buf.writeByte(0);
        //字节指令类型
        buf.writeByte(msg.getMessageType());
        //4字节指令序号
        buf.writeInt(msg.getSequenceId());
        //到这里再加上长度，总共添加了4+1+1+1+4=11个字节，为了满足2的整数幂，添加一个字节
        buf.writeByte(0xff);
        //获取内容的字节数组
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        ObjectOutputStream oos=new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] bytes = bos.toByteArray();
        //长度，一定是在实际内容前面，否则定长解码器会报错
        buf.writeInt(bytes.length);
        //正文
        buf.writeBytes(bytes);
    }

    /**
     * 还有问题：粘包半包
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> list) throws Exception {
        //读魔术的4个字节
        int magicNum = buf.readInt();
        //读版本
        byte version = buf.readByte();
        //读序列化类型
        byte serializerType = buf.readByte();
        //读指令类型
        byte messageType = buf.readByte();
        //读指令序号
        int sequenceId = buf.readInt();
        //读无意义的填充字节
        buf.readByte();
        //读长度
        int len = buf.readInt();
        //读实际内容
        byte[] bytes = new byte[len];
        buf.readBytes(bytes,0,len);
        //将读到的内容反序列化成对象
        if (serializerType==0){
            ObjectInputStream ois=new ObjectInputStream(new ByteArrayInputStream(bytes));
            Message message = (Message)ois.readObject();
            log.info("{},{},{},{},{},{}",magicNum,version,serializerType,messageType,sequenceId,len);
            log.info("{}",message);
            //将消息存入到list，以便传递给下一个handler
            list.add(message);
        }
    }
}
