package com.catinthedark.lib.network.messages;

public class GameStartedMessage {
    private String role;
    private String clientID;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }
}
