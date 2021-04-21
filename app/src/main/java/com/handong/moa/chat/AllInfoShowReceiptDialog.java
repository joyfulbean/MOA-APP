package com.handong.moa.chat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.handong.moa.R;
import com.handong.moa.data.ServerInfo;
import com.handong.moa.receipt.ReceiptActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class AllInfoShowReceiptDialog extends Dialog implements View.OnClickListener{

    public AllInfoShowReceiptDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public AllInfoShowReceiptDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    public Activity activity;
    public Dialog dialog;
    private ImageButton popupCloseButton;
    private ArrayList<String> imgUri = new ArrayList<>();
    private String roomId;
    private String totalCost;
    private TextView cost;
    private Button addButton;
    private int peopleNum;
    private TextView peopleNumView;

    private ListView listView;
    private AllImageShowReceiptAdapter imageAdapter;

    private ListView listViewImage;
    private AllInfoShowReceiptAdapter infoAdapter;
    private ArrayList<Bitmap> imageBitmap = new ArrayList<>();
    private static final String postUrl = ServerInfo.getUrl() + "static/receipts/";




    public AllInfoShowReceiptDialog(Activity a, AllInfoShowReceiptAdapter infoAdapter, AllImageShowReceiptAdapter imageAdapter, ArrayList<String> imgUri, String roomId, String totalCost, int peopleNum) {
        super(a);
        this.activity = a;
        this.infoAdapter = infoAdapter;
        this.imageAdapter = imageAdapter;
        this.imgUri = imgUri;
        this.roomId = roomId;
        this.totalCost = totalCost;
        this.peopleNum = peopleNum;
        setupLayout();
    }

    private void setupLayout() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whole_receipt_popup);
        listView = findViewById(R.id.all_receipt_show_list);
        listView.setAdapter(infoAdapter);

        cost = findViewById(R.id.wholereceipt_popup_wholecost);
        cost.setText("총 " + totalCost + "원");

        peopleNumView = findViewById(R.id.chatpage_pplnum_textview1);
        peopleNumView.setText("\uD83D\uDC64 " + Integer.toString(peopleNum) + " 명 참여중");

        addButton = findViewById(R.id.wholereceipt_popup_add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity.getApplicationContext(), ReceiptActivity.class);
                intent.putExtra("test_id",roomId);
                activity.startActivity(intent);

            }
        });
//        infoAdapter.notifyDataSetChanged();


        popupCloseButton = (ImageButton) findViewById(R.id.wholereceipt_popup_closebutton);
        popupCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        // ListView 크기 조절 - image view
        int totalHeight = 0;
        for (int i = 0; i < infoAdapter.getCount(); i++) {
            View listItem = infoAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
            Log.i("view sizezze image", String.valueOf(listItem.getMeasuredHeight())); //result 623
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (infoAdapter.getCount() - 1));
        listView.setLayoutParams(params);

        // for image list view
        listViewImage = findViewById(R.id.all_receipt_show_image_list);
        imageAdapter.setImgBitmap(imageBitmap);
        listViewImage.setAdapter(imageAdapter);

        for(int i = 0; i < imgUri.size(); i++){
            loadImageTask imageTask = new loadImageTask(postUrl + roomId + File.separator + imgUri.get(i));
            Log.i("loadImageTask", postUrl + roomId + File.separator + imgUri.get(i));
            imageTask.execute();
        }
    }

    public class loadImageTask extends AsyncTask<Bitmap, Void, Bitmap> {
        private String url;

        public loadImageTask(String url) {
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(Bitmap... params) {

            Bitmap imgBitmap = null;

            try {
                URL url1 = new URL(url);
                URLConnection conn = url1.openConnection();
                conn.connect();
                int nSize = conn.getContentLength();
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream(), nSize);
                imgBitmap = BitmapFactory.decodeStream(bis);
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return imgBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bit) {
            super.onPostExecute(bit);
            imageBitmap.add(bit);
            imageAdapter.notifyDataSetChanged();
            // ListView 크기 조절 - image view
            int totalHeightImage = 0;
            for (int i = 0; i < imageAdapter.getCount(); i++) {
                View listItem = imageAdapter.getView(i, null, listViewImage);
                listItem.measure(0, 0);
                totalHeightImage += listItem.getMeasuredHeight();
                Log.i("view sizezze image", String.valueOf(listItem.getMeasuredHeight())); //result 623
            }
            ViewGroup.LayoutParams paramsImage = listViewImage.getLayoutParams();
            paramsImage.height = totalHeightImage + (listViewImage.getDividerHeight() * (imageAdapter.getCount() - 1));
            listViewImage.setLayoutParams(paramsImage);
        }
    }

    @Override
    public void onClick(View view) {

    }
}
