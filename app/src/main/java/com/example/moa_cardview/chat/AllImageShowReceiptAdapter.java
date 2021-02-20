package com.example.moa_cardview.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.moa_cardview.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ShowReceiptAllImageAdapter extends BaseAdapter {
    private ArrayList<Bitmap> imgBitmap;
    private ImageView imageView;
    private Context context;
    private String roomId;
    private LayoutInflater mLayoutInflater = null;



    public ShowReceiptAllImageAdapter(Context context) {
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
