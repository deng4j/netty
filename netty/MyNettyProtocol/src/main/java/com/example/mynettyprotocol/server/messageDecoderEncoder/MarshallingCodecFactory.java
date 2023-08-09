package com.example.mynettyprotocol.server.messageDecoderEncoder;

import org.jboss.marshalling.*;

import java.io.IOException;

/**
 * JBoss编解码器工厂
 */
public final class MarshallingCodecFactory {

    public static Marshaller buildMarshalling() throws IOException {
        // serial表示创建的是java序列化工厂对象
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration= new MarshallingConfiguration();
        configuration.setVersion(5);
        Marshaller marshaller = marshallerFactory.createMarshaller(configuration);
        return marshaller;
    }

    public static Unmarshaller buildUnMarshalling() throws IOException {
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration= new MarshallingConfiguration();
        configuration.setVersion(5);
        final Unmarshaller unmarshaller = marshallerFactory.createUnmarshaller(configuration);
        return unmarshaller;
    }
}
