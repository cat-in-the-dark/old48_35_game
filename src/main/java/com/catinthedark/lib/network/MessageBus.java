package com.catinthedark.lib.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessageBus implements IMessageBus {
    private final List<Subscriber<?>> subscribers = new ArrayList<>();
    private final Transport transport;
    
    public MessageBus(Transport transport) {
        this.transport = transport;
        this.transport.setReceiver(wrapper -> subscribers
                .stream()
                .filter(sub -> Objects.equals(sub.className, wrapper.getData().getClass().getCanonicalName()))
                .forEach(sub -> sub.send(wrapper.getData(), wrapper.getSender())));
    }
    
    @Override
    public void send(Object message) {
        transport.send(message);
    }
    
    @Override
    public <T> void subscribe(Class<T> clazz, Callback<T> callback) {
        Subscriber<T> subscriber = new Subscriber<>(clazz.getCanonicalName(), callback);
        subscribers.add(subscriber);
    }

    static class Subscriber<T> {
        final String className;
        final Callback<T> callback;

        Subscriber(String className, Callback<T> callback) {
            this.className = className;
            this.callback = callback;
        }
        
        void send(Object data, String sender) {
            callback.apply((T)data, sender);
        }
    }
}
