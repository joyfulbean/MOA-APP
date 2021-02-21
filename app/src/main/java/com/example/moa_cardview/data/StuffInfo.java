package com.example.moa_cardview.data;

import java.io.Serializable;

public class StuffInfo extends RoomInfo implements Serializable {
    private String stuffLink;
    private String stuffCost;
    private String orderDate;
    private String orderTime;
    private String ogTitle;
    private String imageUrl = null;
    private boolean expandable;

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public String getStuffLink() {
        return stuffLink;
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public String getOgTitle() {
        return ogTitle;
    }

    public void setOgTitle(String ogTitle) {
        this.ogTitle = ogTitle;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void setStuffLink(String stuffLink) {
        this.stuffLink = stuffLink;
    }

    public String getStuffCost() {
        return stuffCost;
    }

    public void setStuffCost(String stuffCost) {
        this.stuffCost = stuffCost;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }
}