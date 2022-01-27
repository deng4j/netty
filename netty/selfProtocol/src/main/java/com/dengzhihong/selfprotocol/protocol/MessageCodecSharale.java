package com.dengzhihong.selfprotocol.protocol;

import com.dengzhihong.selfprotocol.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * 继承MessageToMessageCodec可以使用@Sharable注解
 */
@ChannelHandler.Sharable
@Slf4j
public class MessageCodecSharale extends MessageToMessageCodec<ByteBuf,Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        //如果和TCL解码器使用的话，不需要考虑这个buf的线程安全，因为TCL传递的消息是完整的
        ByteBuf buf = ctx.alloc().buffer();
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
        //到这里再加上长度，总共添加了4+1+1+1+4+4=15个字节，为了满足2的整数幂，添加一个字节
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
        //传递给下一个出站处理器
        outList.add(buf);
    }

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
