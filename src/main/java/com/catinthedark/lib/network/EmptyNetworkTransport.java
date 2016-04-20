package com.catinthedark.lib.network;

public final class EmptyNetworkTransport extends NetworkTransport {
    public EmptyNetworkTransport(Converter converter) {
        super(converter);
    }

    @Override
    protected void sendToNetwork(String message) {
        System.out.println("SEND TO SERVER " + message);
    }
}
