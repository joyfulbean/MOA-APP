package com.example.moa_cardview.data;

public class OrderInfo {
    private String roomId;
    private String stuffName;
    private String cost;
    private String num;

    public OrderInfo(String roomId, String stuffName, String cost, String num) {
        this.roomId = roomId;
        this.stuffName = stuffName;
        this.cost = cost;
        this.num = num;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getStuffName() {
        return stuffName;
    }

    public void setStuffName(String stuffName) {
        this.stuffName = stuffName;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
