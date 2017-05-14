package com.example.chat3;

import java.util.Date;

public class Message {
    private String id;
    private String name;
    private String bodyMessage;
    private long date;

    public Message() {
    }

    public Message(String bodyMessage, String name) {
        this.name = name;
        this.bodyMessage = bodyMessage;
        this.date = new Date().getTime();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBodyMessage() {
        return bodyMessage;
    }

    public void setBodyMessage(String bodyMessage) {
        this.bodyMessage = bodyMessage;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
