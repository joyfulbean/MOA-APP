package com.example.moa_cardview.data;

import java.io.Serializable;

public class RoomInfo implements Serializable {
    protected String roomId;
    protected String category;
    protected String title;
    protected String place;
    protected String numUsers;
    protected String numUsersLimit;
    protected String creatorEmail;
    protected String status;
    protected int participantsId;


    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getNumUsers() {
        return numUsers;
    }

    public void setNumUsers(String numUsers) {
        this.numUsers = numUsers;
    }

    public String getNumUsersLimit() {
        return numUsersLimit;
    }

    public void setNumUsersLimit(String numUsersLimit) {
        this.numUsersLimit = numUsersLimit;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public int getParticipantsId() {
        return participantsId;
    }

    public void setParticipantsId(int participantsId) {
        this.participantsId = participantsId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}