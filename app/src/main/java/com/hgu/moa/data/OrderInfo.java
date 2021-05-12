package com.hgu.moa.data;

import android.net.Uri;

import java.io.Serializable;

public class OrderInfo implements Serializable {
    private String roomId;
    private String stuffName;
    private String cost;
    private String num;
    private String id;
    private String ref_cnt;
    private String registered_on;
    private String user_id;
    private Uri filePath;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRef_cnt() {
        return ref_cnt;
    }

    public void setRef_cnt(String ref_cnt) {
        this.ref_cnt = ref_cnt;
    }

    public String getRegistered_on() {
        return registered_on;
    }

    public void setRegistered_on(String registered_on) {
        this.registered_on = registered_on;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public Uri getFilePath() {
        return filePath;
    }

    public void setFilePath(Uri filePath) {
        this.filePath = filePath;
    }

    public OrderInfo() {
    }

    public OrderInfo(String roomId, String cost, Uri filePath) {
        this.roomId = roomId;
        this.cost = cost;
        this.filePath = filePath;
    }


    public OrderInfo(String roomId, String stuffName, String cost) {
        this.roomId = roomId;
        this.stuffName = stuffName;
        this.cost = cost;
        this.num = num;
    }

    public OrderInfo(String roomId, String stuffName, String cost, String num) {
        this.roomId = roomId;
        this.stuffName = stuffName;
        this.cost = cost;
        this.num = num;
    }

    public OrderInfo(String roomId, String stuffName, String cost, String num, String id, String ref_cnt, String registered_on, String user_id) {
        this.roomId = roomId;
        this.stuffName = stuffName;
        this.cost = cost;
        this.num = num;
        this.id = id;
        this.ref_cnt = ref_cnt;
        this.registered_on = registered_on;
        this.user_id = user_id;
    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int i) {
//        dest.writeString(roomId);
//        dest.writeString(stuffName);
//        dest.writeString(cost);
//        dest.writeString(num);
//        dest.writeString(filePath.toString());
//    }
}
