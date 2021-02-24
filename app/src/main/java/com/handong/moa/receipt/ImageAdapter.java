package com.handong.moa.receipt;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handong.moa.R;
import com.handong.moa.data.OrderInfo;

import java.io.IOException;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    Context context = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<OrderInfo> orderinfos;
    ListView listView;

    public ImageAdapter(Context context, ArrayList<OrderInfo> data) {
        context = context;
        orderinfos = data;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setListView(ListView listView) {
        this.listView = listView;
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
        View view = mLayoutInflater.inflate(R.layout.orderimage_layout, null);


        ImageView image = view.findViewById(R.id.image);
        TextView cost = (TextView) view.findViewById(R.id.order_myorderimage_price1);

        Uri filePath = orderinfos.get(position).getFilePath();

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(view.getContext().getContentResolver(), filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        image.setImageBitmap(bitmap);

        cost.setText(orderinfos.get(position).getCost());

        ImageButton deleteButton = (ImageButton) view.findViewById(R.id.order_myorderimage_closebutton1);
        deleteButton.setOnClickListener((View.OnClickListener) context);
        deleteButton.setTag("D" + position);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderinfos.remove(getItem(position));

                // 삭제시 뷰 사이즈 조절
                ViewGroup.LayoutParams params = listView.getLayoutParams();
                params.height = (623 * getCount()) + (listView.getDividerHeight() * (getCount() - 1));
                listView.setLayoutParams(params);

                notifyDataSetChanged();
            }
        });

        return view;
    }

}
