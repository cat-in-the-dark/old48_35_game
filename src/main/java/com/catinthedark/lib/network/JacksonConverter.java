package com.catinthedark.lib.network;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JacksonConverter implements NetworkTransport.Converter {
    private final ObjectMapper objectMapper;
    private final Map<String, CustomConverter<?>> converters;
    
    public JacksonConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.converters = new HashMap<>();
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
        System.out.println(json);
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
    
    public void registerConverter(String className, CustomConverter converter) {
        converters.put(className, converter);
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
