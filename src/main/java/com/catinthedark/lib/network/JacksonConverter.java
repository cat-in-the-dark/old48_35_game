package com.catinthedark.lib.network;

import com.catinthedark.lib.network.messages.DisconnectedMessage;
import com.catinthedark.lib.network.messages.GameStartedMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JacksonConverter implements NetworkTransport.Converter {
    private final ObjectMapper objectMapper;
    private final Map<String, CustomConverter<?>> converters;
    
    public JacksonConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.converters = new HashMap<>();
        
        registerConverter(GameStartedMessage.class, data -> {
            GameStartedMessage message = new GameStartedMessage();
            message.setRole((String)data.get("role"));
            message.setClientID((String) data.get("clientID"));
            return message;
        });

        registerConverter(DisconnectedMessage.class, data -> {
            DisconnectedMessage message = new DisconnectedMessage();
            message.setClientID((String) data.get("clientID"));
            return message;
        });
    }
    
    @Override
    public String toJson(Object data) throws NetworkTransport.ConverterException {
        Wrapper wrapper = new Wrapper();
        wrapper.setData(data);
        wrapper.setClassName(data.getClass().getCanonicalName());
        try {
            return objectMapper.writeValueAsString(wrapper);
        } catch (Exception e) {
            throw new NetworkTransport.ConverterException("Can't convert to json " + data + " : " + e.getMessage(), e);
        }
    }
    
    @Override
    public Wrapper fromJson(String json) throws NetworkTransport.ConverterException {
        Wrapper wrapper;
        try {
            wrapper = objectMapper.readValue(json, Wrapper.class);
        } catch (IOException e) {
            throw new NetworkTransport.ConverterException("Can't parse " + json + " : " + e.getMessage(), e);
        }
        CustomConverter converter = converters.get(wrapper.getClassName());
        if (converter != null) {
            Object data = converter.apply((Map<String, Object>)wrapper.getData());
            wrapper.setData(data);
            return wrapper;
        } else {
            throw new NetworkTransport.ConverterException("There is no " + wrapper.getClassName() + " converter");
        }
    }
    
    public <T> JacksonConverter registerConverter(Class<T> className, CustomConverter<T> converter) {
        converters.put(className.getCanonicalName(), converter);
        return this;
    }

    public <T> JacksonConverter registerConverter(String className, CustomConverter<T> converter) {
        converters.put(className, converter);
        return this;
    }
    
    public Collection<String> registeredConverters() {
        return converters.keySet();
    }
    
    public static class Wrapper implements IMessageBus.Wrapper {
        private String sender;
        private Object data;
        private String className;

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }
    }
    
    public interface CustomConverter<T> {
        T apply(Map<String, Object> data);
    }
}
