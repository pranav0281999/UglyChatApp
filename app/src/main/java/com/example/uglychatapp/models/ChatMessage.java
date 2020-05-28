package com.example.uglychatapp.models;

import java.io.Serializable;

public class ChatMessage implements Serializable {
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    private String body;

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    private String receiver;
}