package com.example.moa_cardview.receipt;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.moa_cardview.R;
import com.example.moa_cardview.chat.ChatMessageItem;
import com.example.moa_cardview.data.OrderInfo;

import java.util.ArrayList;

public class AddMyOrderAdapter extends BaseAdapter {
    ArrayList<OrderInfo> orderInfos;
    private LayoutInflater layoutInflater;
    private Context context;

    public AddMyOrderAdapter(ArrayList<OrderInfo> orderInfos, LayoutInflater layoutInflater, Context context) {
        this.orderInfos = orderInfos;
        this.layoutInflater = layoutInflater;
        this.context = context;
    }

    @Override
    public int getCount() {
        return orderInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return orderInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        //재활용할 뷰는 사용하지 않음!!
        View itemView = null;

        //현재 보여줄 번째의(position)의 데이터로 뷰를 생성
        for(int i = 0; i<orderInfos.size(); i++){
            OrderInfo orderInfo = orderInfos.get(i);
            itemView = layoutInflater.inflate(R.layout.myorder_addlayout, viewGroup, false);
            saveInfo(orderInfo, itemView);
        }

        return itemView;
    }

    private void saveInfo(OrderInfo orderInfo, View itemView){
        TextView stuff_name;
        TextView stuff_cost;
        TextView stuff_num;
        stuff_name = itemView.findViewById(R.id.order_myorder_product1);
        stuff_cost = itemView.findViewById(R.id.order_myorder_price1);
        stuff_num = itemView.findViewById(R.id.order_myorder_count1);

        orderInfo.setStuffName(stuff_name.getText().toString());
        orderInfo.setCost(stuff_cost.getText().toString());
        orderInfo.setNum(stuff_num.getText().toString());

        Log.i("order info test cost : ", orderInfo.getCost());
        Log.i("order info test name : ", orderInfo.getStuffName());
        Log.i("order info test num : ", orderInfo.getNum());


    }
}


