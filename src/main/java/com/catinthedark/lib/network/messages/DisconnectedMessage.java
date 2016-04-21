package com.catinthedark.lib.network.messages;

public class DisconnectedMessage extends Message {
    private String clientID;

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }
}
