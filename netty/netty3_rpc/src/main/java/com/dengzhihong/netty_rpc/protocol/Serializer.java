package com.dengzhihong.netty_rpc.protocol;


import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;
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
                Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
                String json = new String(bytes, StandardCharsets.UTF_8);
                T jsonObj = gson.fromJson(json, clazz);
                return jsonObj;
            }

            @Override
            public <T> byte[] serializer(T object) {
                Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
                String json = gson.toJson(object);
                return json.getBytes(StandardCharsets.UTF_8);
            }
        }
    }

    /**
     * json序列化器，因为json不支持Class对象序列化
     */
    class ClassCodec implements JsonSerializer<Class<?>> , JsonDeserializer<Class<?>>{

        @Override
        public Class<?> deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            try {
                String str = json.getAsString();
                return Class.forName(str);
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }
        }

        /**
         * 反序列化时json不能直接转字符串，需要先将json相关信息封装成一个JsonElement
         */
        @Override
        public JsonElement serialize(Class<?> aClass, Type type, JsonSerializationContext jsonSerializationContext) {
            //class转json（类的全路径转字符串），字符串属于基本数据类型，使用JsonPrimitive
            return new JsonPrimitive(aClass.getName());
        }
    }
}
