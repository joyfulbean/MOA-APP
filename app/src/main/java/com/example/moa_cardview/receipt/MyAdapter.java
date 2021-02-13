package com.example.moa_cardview.receipt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.moa_cardview.R;
import com.example.moa_cardview.data.OrderInfo;
import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<OrderInfo> orderinfo;

    public MyAdapter(Context context, ArrayList<OrderInfo> data) {
        mContext = context;
        orderinfo = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return orderinfo.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public OrderInfo getItem(int position) {
        return orderinfo.get(position);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.myorder_addlayout, null);

        TextView stuff_name = (TextView) view.findViewById(R.id.order_myorderothers_product1);
        TextView stuff_cost = (TextView) view.findViewById(R.id.order_myorderothers_price1);
        TextView stuff_num = (TextView) view.findViewById(R.id.order_myorderothers_count1);

        stuff_name.setText(orderinfo.get(position).getStuffName());
        stuff_cost.setText(orderinfo.get(position).getCost());
        stuff_num.setText(orderinfo.get(position).getNum());

        return view;
    }
}
