package com.example.moa_cardview.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moa_cardview.R;
import com.example.moa_cardview.data.OrderInfo;

import java.io.IOException;
import java.util.ArrayList;



public class AllInfoShowReceiptAdapter extends BaseAdapter{

    private ArrayList<OrderInfo> orderInfos;
    private TextView name;
    private TextView num;
    private TextView cost;
    private Context context;
    private LayoutInflater mLayoutInflater = null;

    public AllInfoShowReceiptAdapter(Context context, ArrayList<OrderInfo> orderInfos) {
        this.orderInfos = orderInfos;
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return orderInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return orderInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View converView, ViewGroup viewGroup) {
        View view = mLayoutInflater.inflate(R.layout.all_receipt_show_card, null);
        name = view.findViewById(R.id.wholereceipt_popup_foodname);
        cost = view.findViewById(R.id.wholereceipt_popup_cost);
        num = view.findViewById(R.id.wholereceipt_popup_amount);
        name.setText(orderInfos.get(position).getStuffName());
        cost.setText(orderInfos.get(position).getCost());
        num.setText(orderInfos.get(position).getNum());

        return view;
    }
}