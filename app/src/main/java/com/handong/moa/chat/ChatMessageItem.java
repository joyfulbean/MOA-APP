package com.handong.moa.chat;

public class ChatMessageItem {
    private String name;
    private String message;
    private String time;
    private String img;

    //firebase DB에 객체로 값을 읽어올 때..
    //파라미터가 비어있는 생성자가 필요함.
    public ChatMessageItem() {
    }

    public ChatMessageItem(String name, String message, String time, String img) {
        this.name = name;
        this.message = message;
        this.time = time;
        this.img = img;
    }
    //Getter & Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
