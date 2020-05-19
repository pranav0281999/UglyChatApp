package com.example.uglychatapp.models;

import java.io.Serializable;

public class ChatMessage implements Serializable {
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    String body;

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    boolean isMine;

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    String receiver;
}