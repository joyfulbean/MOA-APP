package com.handong.moa.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
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

import java.util.ArrayList;

public class EditImageAdapter extends BaseAdapter {
    private Context context = null;
    private LayoutInflater mLayoutInflater = null;
    private ArrayList<OrderInfo> imageInfo;
    private ArrayList<Bitmap> imgBitmap;
    private ArrayList<OrderInfo> deleteImageInfo = new ArrayList<>();
    private ListView listView;


    public void setImgBitmap(ArrayList<Bitmap> imgBitmap) {
        this.imgBitmap = imgBitmap;
    }

    public ArrayList<OrderInfo> getDeleteImageInfo(){
        return deleteImageInfo;
    }

    public EditImageAdapter(Context context, ArrayList<OrderInfo> imageUrls) {
        this.context = context;
        this.imageInfo = imageUrls;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    @Override
    public int getCount() {
        return imgBitmap.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Bitmap getItem(int position) {
        return imgBitmap.get(position);
    }

    @Override
    public View getView(final int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.orderimage_layout, null);


        ImageView image = view.findViewById(R.id.image);
        image.setImageBitmap(imgBitmap.get(position));

        TextView cost = (TextView) view.findViewById(R.id.order_myorderimage_price1);
        cost.setText(imageInfo.get(position).getCost());


        ImageButton deleteButton = (ImageButton) view.findViewById(R.id.order_myorderimage_closebutton1);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteImageInfo.add(imageInfo.get(position));
                Log.i("??????????? delte", imageInfo.get(position).getFilePath().toString());
                imgBitmap.remove(position);
                imageInfo.remove(position);

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
