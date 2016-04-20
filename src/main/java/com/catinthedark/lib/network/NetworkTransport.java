package com.catinthedark.lib.network;

public abstract class NetworkTransport implements IMessageBus.Transport {
    private final Converter converter;
    private Receiver receiver;
    
    public NetworkTransport(Converter converter) {
        this.converter = converter;
    }
    
    protected abstract void sendToNetwork(String message);
    
    @Override
    public void send(Object message) {
        try {
            String json = converter.toJson(message);
            sendToNetwork(json);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * onReceive 
     * @param json
     */
    void onReceive(String json) {
        try {
            IMessageBus.Wrapper data = converter.fromJson(json);
            receiver.apply(data);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    @Override
    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }
    
    public interface Converter {
        String toJson(Object data) throws ConverterException;
        IMessageBus.Wrapper fromJson(String json) throws ConverterException;
    }

    public static class ConverterException extends Exception {

        public ConverterException(String s, Exception e) {
            super(s, e);
        }

        public ConverterException(String s) {
            super(s);
        }
    }
}
