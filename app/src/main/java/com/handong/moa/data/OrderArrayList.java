package com.handong.moa.data;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderArrayList implements Serializable {
    private ArrayList<OrderInfo> writeInfo;
    private ArrayList<OrderInfo> imageInfo;

    public OrderArrayList(ArrayList<OrderInfo> writeInfo, ArrayList<OrderInfo> imageInfo) {
        this.writeInfo = writeInfo;
        this.imageInfo = imageInfo;
    }

    public ArrayList<OrderInfo> getWriteInfo() {
        return writeInfo;
    }

    public ArrayList<OrderInfo> getImageInfo() {
        return imageInfo;
    }
}
