package com.handong.moa.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.handong.moa.R;

import java.util.ArrayList;

public class AllImageShowReceiptAdapter extends BaseAdapter {
    private ArrayList<Bitmap> imgBitmap;
    private ImageView imageView;
    private Context context;
    private String roomId;
    private LayoutInflater mLayoutInflater = null;



    public AllImageShowReceiptAdapter(Context context) {
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setImgBitmap(ArrayList<Bitmap> imgBitmap) {
        this.imgBitmap = imgBitmap;
    }

    @Override
    public int getCount() {
        return imgBitmap.size();
    }

    @Override
    public Object getItem(int i) {
        return imgBitmap.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View converView, ViewGroup viewGroup) {
        View view = mLayoutInflater.inflate(R.layout.all_receipt_show_image_card, null);
        imageView = view.findViewById(R.id.wholereceipt_popup_imagemenu1);
        imageView.setImageBitmap(imgBitmap.get(i));

        return view;
    }

}
