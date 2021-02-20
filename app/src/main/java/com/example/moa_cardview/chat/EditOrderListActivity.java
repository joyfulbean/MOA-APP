package com.example.moa_cardview.chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moa_cardview.R;
import com.example.moa_cardview.data.OrderInfo;
import com.example.moa_cardview.receipt.MyAdapter;

import java.util.ArrayList;

public class EditOrderListActivity extends AppCompatActivity {
    private ArrayList<OrderInfo> eachOrderInfos;
    private ArrayList<String> imgUrls;
    private ArrayList<Bitmap> imageBitmap;

    private TextView name;
    private TextView cost;
    private TextView num;

    private ListView listView;
    private ListView imageListView;
    private MyAdapter listAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order_list);

        Intent intent = getIntent();
        eachOrderInfos = (ArrayList<OrderInfo>) intent.getSerializableExtra("each_orderInfos");
        imgUrls = (ArrayList<String>) intent.getSerializableExtra("imgUrls");
        imageBitmap = (ArrayList<Bitmap>) intent.getSerializableExtra("imageBitmap");

        listView = findViewById(R.id.edit_receipt_edit_info_listview);
        imageListView = findViewById(R.id.edit_receipt_edit_image_listview);
        name = findViewById(R.id.receipt_edit_product_name);
        cost = findViewById(R.id.receipt_edit_price_edittext);
        num = findViewById(R.id.receipt_edit_count);

        listAdapter = new MyAdapter(EditOrderListActivity.this, eachOrderInfos, name, cost, num);
        listView.setAdapter(listAdapter);
        listAdapter.setListView(listView);

        // ListView 크기 조절 - write(normal) list view
        int totalHeight = 0;
        for (int i = 0; i < eachOrderInfos.size(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
            Log.i("view sizezze normal", String.valueOf(listItem.getMeasuredHeight()));  //result 342
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);

    }
}