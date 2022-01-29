package com.dengzhihong.selfprotocol.protocol;


import com.google.gson.Gson;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 序列化器Java、Json、Fastjson、Jackson...
 */
public interface Serializer {

    //反序列化方法
    <T> T deserializer(Class<T> clazz,byte[] bytes);

    //序列化方法
    <T> byte[] serializer (T object);

    enum Algorithm implements Serializer{
        Java {
            @Override
            public <T> T deserializer(Class<T> clazz, byte[] bytes) {
                try {
                    ObjectInputStream ois=new ObjectInputStream(new ByteArrayInputStream(bytes));
                    T obj = (T)ois.readObject();
                    return obj;
                } catch (Exception e) {
                    throw new RuntimeException ("反序列化失败",e);
                }
            }

            @Override
            public <T> byte[] serializer(T object) {
                try {
                    ByteArrayOutputStream bos=new ByteArrayOutputStream();
                    ObjectOutputStream oos=new ObjectOutputStream(bos);
                    oos.writeObject(object);
                    byte[] bytes = bos.toByteArray();
                    return bytes;
                } catch (IOException e) {
                    throw new RuntimeException ("序列化失败",e);
                }
            }
        },
        Json{
            @Override
            public <T> T deserializer(Class<T> clazz, byte[] bytes) {
                String json = new String(bytes, StandardCharsets.UTF_8);
                T jsonObj = new Gson().fromJson(json, clazz);
                return jsonObj;
            }

            @Override
            public <T> byte[] serializer(T object) {
                String json = new Gson().toJson(object);
                return json.getBytes(StandardCharsets.UTF_8);
            }
        }
    }
}
