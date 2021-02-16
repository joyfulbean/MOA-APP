package com.example.moa_cardview.receipt;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.moa_cardview.R;
import com.example.moa_cardview.data.OrderInfo;
import java.util.ArrayList;
import java.util.Map;

public class MyAdapter extends BaseAdapter{

    Context context = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<OrderInfo> orderinfos;
    TextView name;
    TextView cost;
    TextView num;


    public MyAdapter(Context context, ArrayList<OrderInfo> data, TextView name, TextView cost, TextView num) {
        context = context;
        orderinfos = data;
        mLayoutInflater = LayoutInflater.from(context);
        this.name = name;
        this.cost = cost;
        this.num = num;
    }

    @Override
    public int getCount() {
        return orderinfos.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public OrderInfo getItem(int position) {
        return orderinfos.get(position);
    }

    @Override
    public View getView(final int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.myorder_addlayout, null);


        TextView stuff_name = (TextView) view.findViewById(R.id.order_myorderothers_product1);
        TextView stuff_cost = (TextView) view.findViewById(R.id.order_myorderothers_price1);
        TextView stuff_num = (TextView) view.findViewById(R.id.order_myorderothers_count1);


        TextView editButton = (TextView) view.findViewById(R.id.order_myorderothers_editbutton);
        editButton.setOnClickListener((View.OnClickListener)context);
        editButton.setTag("E" + position);



        ImageButton deleteButton = (ImageButton) view.findViewById(R.id.order_myorderothers_closebutton);
        deleteButton.setOnClickListener((View.OnClickListener) context);
        deleteButton.setTag("D" + position);



        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderInfo temp_edit = getItem(position);
                name.setText(temp_edit.getStuffName());
                cost.setText(temp_edit.getCost());
                num.setText(temp_edit.getNum());
                orderinfos.remove(getItem(position));
                notifyDataSetChanged();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderinfos.remove(getItem(position));
                notifyDataSetChanged();
            }
        });

        stuff_name.setText(orderinfos.get(position).getStuffName());
        stuff_cost.setText(orderinfos.get(position).getCost());
        stuff_num.setText(orderinfos.get(position).getNum());

        return view;
    }

}