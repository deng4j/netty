package com.dengzhihong.netty_rpc.server.service;

import com.dengzhihong.netty_rpc.config.Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 根据接口名寻找Service实现类
 */
public class ServiceFactory {

    static Properties properties;
    static Map<Class<?>,Object> map=new ConcurrentHashMap<>();

    static {
        try {
            InputStream in = Config.class.getResourceAsStream("/application.properties");
            properties = new Properties();
            properties.load(in);
            Set<String> names = properties.stringPropertyNames();
            for (String name: names) {
                if (name.endsWith("Service")){
                    Class<?> interfaceClass = Class.forName(name);
                    Class<?> instanceClass = Class.forName(properties.getProperty(name));
                    map.put(interfaceClass,instanceClass.newInstance());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T getService(Class<T> interfaceClass){
        return  (T) map.get(interfaceClass);
    }
}
