package com.catinthedark.lib.network;

public interface IMessageBus {
    void send(Object message);
    <T> void subscribe(Class<T> clazz, Callback<T> callback);
    
    interface Callback<T> {
        void apply(T message, String sender);
    }
    
    interface Wrapper {
        Object getData();
        String getSender();
    }

    interface Transport {
        void send(Object message);
        void setReceiver(Receiver receiver);

        interface Receiver {
            void apply(Wrapper data);
        }
    }
}
