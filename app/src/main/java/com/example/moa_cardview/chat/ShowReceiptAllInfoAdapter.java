package com.example.moa_cardview.chat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moa_cardview.R;
import com.example.moa_cardview.data.OrderInfo;

import java.util.ArrayList;



public class ShowReceiptAllInfoAdapter extends RecyclerView.Adapter<ShowReceiptAllInfoAdapter.MyViewHolder>{

        private Context context;
        private ArrayList<OrderInfo> orders = new ArrayList<>();

    public ShowReceiptAllInfoAdapter(ArrayList<OrderInfo> orders) {
        this.orders = orders;
    }

    public ShowReceiptAllInfoAdapter(Context context, ArrayList<OrderInfo> orders) {
            this.context = context;
            this.orders = orders;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v;
            v = LayoutInflater.from(context).inflate(R.layout.all_receipt_show_card, parent, false);
            MyViewHolder vHolder = new MyViewHolder(v);
            return vHolder;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView stuff_name;
            TextView stuff_cost;
            TextView stuff_num;


            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                stuff_name = itemView.findViewById(R.id.wholereceipt_popup_foodname);
                stuff_cost = itemView.findViewById(R.id.wholereceipt_popup_cost);
                stuff_num = itemView.findViewById(R.id.wholereceipt_popup_amount);
            }

        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.stuff_name.setText(orders.get(position).getStuffName());
            holder.stuff_cost.setText(orders.get(position).getCost());
            holder.stuff_num.setText(orders.get(position).getNum());
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }

        public void addItem(OrderInfo orderInfo){
            orders.add(orderInfo);
            notifyDataSetChanged();
            Log.i("ehlsi", Integer.toString(getItemCount()) + "in MyOrder");
        }
}