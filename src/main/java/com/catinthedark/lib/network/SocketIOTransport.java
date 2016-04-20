package com.catinthedark.lib.network;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URI;

public final class SocketIOTransport extends NetworkTransport {
    private final Socket socket;

    private final Emitter.Listener onConnect = objects -> System.out.println("Connected to server");
    private final Emitter.Listener onDisconnect = objects -> System.out.println("Disconnected from server");
    private final Emitter.Listener onMessage = objects -> onReceive((String)objects[0]);
    private final Emitter.Listener onConnectError = objects -> System.out.println("Can't connect");

    public SocketIOTransport(final Converter converter, final URI uri) {
        super(converter);

        final IO.Options options = new IO.Options();
        options.forceNew = true;
        options.reconnection = false;

        socket = IO.socket(uri, options);
        socket.on(Socket.EVENT_CONNECT, onConnect)
                .on(Socket.EVENT_DISCONNECT, onDisconnect)
                .on(Socket.EVENT_CONNECT_ERROR, onConnectError)
                .on(Socket.EVENT_MESSAGE, onMessage);
    }

    public void connect() {
        socket.connect();
        System.out.println("Connection was opened "+ socket.id());
    }

    public void disconnect() {
        socket.disconnect();
        System.out.println("Connection was closed");
    }

    @Override
    protected void sendToNetwork(String message) {
        socket.send(message);
    }
}
