package com.example.fakemessenger.chat;

public class Message {
    private String text;
    private long timeInMillis;
    private String senderUid;
    private String senderProfileURL;

    public Message(){
        //no arg constructor is needed
    }

    public Message(String text, long timeInMillis, String senderUid, String senderProfileURL) {
        this.text = text;
        this.timeInMillis = timeInMillis;
        this.senderUid = senderUid;
        this.senderProfileURL = senderProfileURL;
    }

    public String getSenderProfileURL() {
        return senderProfileURL;
    }

    public void setSenderProfileURL(String senderProfileURL) {
        this.senderProfileURL = senderProfileURL;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }
}
