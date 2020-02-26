package com.example.fakemessenger.chat;

public class Chat {
    private String chatId;
    private String lastMessage;
    private String user1Id;
    private String user2Id;
    private String user1Name;
    private String user2Name;
    private String user1ProfilePictureURL;
    private String user2ProfilePictureURL;
    private long lastMessageTimeInMillis;


    public Chat(){
        //no arg constructor is needed
    }


    public Chat(String chatId, String user1Id, String user2Id, String user1Name, String user2Name,
                String user1ProfilePictureURL, String user2ProfilePictureURL) {
        this.chatId = chatId;
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.user1Name = user1Name;
        this.user2Name = user2Name;
        this.user1ProfilePictureURL = user1ProfilePictureURL;
        this.user2ProfilePictureURL = user2ProfilePictureURL;
    }

    public String getUser1ProfilePictureURL() {
        return user1ProfilePictureURL;
    }

    public void setUser1ProfilePictureURL(String user1ProfilePictureURL) {
        this.user1ProfilePictureURL = user1ProfilePictureURL;
    }

    public String getUser2ProfilePictureURL() {
        return user2ProfilePictureURL;
    }

    public void setUser2ProfilePictureURL(String user2ProfilePictureURL) {
        this.user2ProfilePictureURL = user2ProfilePictureURL;
    }

    public String getUser1Name() {
        return user1Name;
    }

    public void setUser1Name(String user1Name) {
        this.user1Name = user1Name;
    }

    public String getUser2Name() {
        return user2Name;
    }

    public void setUser2Name(String user2Name) {
        this.user2Name = user2Name;
    }

    public String getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(String user1Id) {
        this.user1Id = user1Id;
    }

    public String getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(String user2Id) {
        this.user2Id = user2Id;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastMessageTimeInMillis() {
        return lastMessageTimeInMillis;
    }

    public void setLastMessageTimeInMillis(long lastMessageTimeInMillis) {
        this.lastMessageTimeInMillis = lastMessageTimeInMillis;
    }
}
