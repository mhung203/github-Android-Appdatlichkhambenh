package com.example.app_dat_lich_kham_benh.api.dto;

public class ChatContactDTO {
    private Integer userId;
    private String fullName;
    private String avatar;
    private String lastMessage;
    private String time;

    public Integer getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getAvatar() { return avatar; }
    public String getLastMessage() { return lastMessage; }
    public String getTime() { return time; }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setTime(String time) {
        this.time = time;
    }
}