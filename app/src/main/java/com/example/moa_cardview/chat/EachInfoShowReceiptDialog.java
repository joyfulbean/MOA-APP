package com.example.moa_cardview.chat;

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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.moa_cardview.R;
import com.example.moa_cardview.data.OrderInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EachInfoShowReceiptDialog extends Dialog implements View.OnClickListener{

    public EachInfoShowReceiptDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public EachInfoShowReceiptDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    public Activity activity;
    public Dialog dialog;
    private String roomId;
    private String totalCost;
    private boolean isLock;

    // screen
    private ImageButton popupCloseButton;
    private TextView name;
    private TextView cost;
    private TextView editButton;

    // personal information
    private String userName;
    private String userEmail;

    // order info
    private ListView listView;
    private EachImageShowReceiptAdapter imageAdapter;
    private ArrayList<OrderInfo> each_orderInfos = new ArrayList<>();
    private static final String receiptInfoUrls = "http://54.180.8.235:5000/receipt";

    private ListView listViewImage;
    private EachInfoShowReceiptAdapter infoAdapter;
    private ArrayList<String> imgUrls = new ArrayList<>();
    private ArrayList<Bitmap> imageBitmap = new ArrayList<>();
    private static final String imageInfoUrls = "http://54.180.8.235:5000/receipt/image";
    private static final String postUrl = "http://54.180.8.235:5000/static/receipts/";




    public EachInfoShowReceiptDialog(Activity a, EachInfoShowReceiptAdapter infoAdapter, EachImageShowReceiptAdapter imageAdapter, String roomId, String userName, String userEmail, boolean isLock) {
        super(a);
        this.activity = a;
        this.infoAdapter = infoAdapter;
        this.userName = userName;
        this.userEmail = userEmail;
        this.imageAdapter = imageAdapter;
        this.roomId = roomId;
        this.isLock = isLock;
        setupLayout();
    }

    private void setupLayout() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.individual_receipt_popup);
        listView = findViewById(R.id.each_receipt_show_list);
        listViewImage = findViewById(R.id.each_receipt_show_list_image);

        cost = findViewById(R.id.individualreceipt_popup_wholecost);
        receiveEachOrderInfo();

        name = findViewById(R.id.name);
        name.setText(userName);

        editButton = findViewById(R.id.individualreceipt_edit_button);
        if(isLock){
            editButton.setVisibility(View.GONE);
        }else {
            editButton.setVisibility(View.VISIBLE);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, EditOrderListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("each_orderInfos", each_orderInfos);
                    intent.putExtra("imgUrls", imgUrls);
                    intent.putExtra("imageBitmap",imageBitmap);
                    activity.startActivity(intent);
                }
            });
        }

        popupCloseButton = (ImageButton) findViewById(R.id.individualreceipt_popup_closebutton);
        popupCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    //* receive orderInfo, for show all receipt info
    public void receiveEachOrderInfo(){
        class sendData extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                each_orderInfos.clear();
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                infoAdapter.setOrderInfos(each_orderInfos);
                listView.setAdapter(infoAdapter);

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
                imageAdapter.setImgBitmap(imageBitmap);
                listViewImage.setAdapter(imageAdapter);

                for(int i = 0; i < imgUrls.size(); i++){
                    eachLoadImageTask imageTask = new eachLoadImageTask(postUrl + roomId + File.separator + imgUrls.get(i));
                    imageTask.execute();
                }

                cost.setText("총 " + totalCost + "원");
            }
            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
            }
            @Override
            protected void onCancelled(String s) {
                super.onCancelled(s);
            }
            @Override
            protected void onCancelled() {
                super.onCancelled();
            }
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject jsonInput = new JSONObject();

                    Request request = new Request.Builder()
                            .url(receiptInfoUrls + File.separator + roomId + File.separator + userEmail)
                            .build();

                    Response responses = null;
                    responses = client.newCall(request).execute();

                    JSONObject jObject = new JSONObject(responses.body().string());
                    JSONArray jArray = jObject.getJSONArray("receipts");

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject obj = jArray.getJSONObject(i);
                        Log.i("JSONObjectJSONObject", obj.toString());
                        OrderInfo temp = new OrderInfo();
                        temp.setStuffName(obj.getString("stuff_name"));
                        temp.setCost(obj.getString("stuff_cost"));
                        temp.setNum(Integer.toString(obj.getInt("stuff_num")));
                        //이미지 정보 추가해야함.
                        each_orderInfos.add(temp);
                    }
                    //get info totalCost
                    int infoTotalCost = jObject.getInt("total_cost");
                    Log.i("infoTotalCost", Integer.toString(infoTotalCost));

                    OkHttpClient clientImage = new OkHttpClient();

                    Request requestImage = new Request.Builder()
                            .url(imageInfoUrls + File.separator + roomId + File.separator + userEmail)
                            .build();

                    Response responsesImage = null;
                    responsesImage = clientImage.newCall(requestImage).execute();


                    JSONObject jObjectImage = new JSONObject(responsesImage.body().string());
                    JSONArray jArrayImage = jObjectImage.getJSONArray("receipts");

                    Log.i("jArrayImage.length()", String.valueOf(jArrayImage.length()));
                    for (int i = 0; i < jArrayImage.length(); i++) {
                        String url = jArrayImage.getString(i);
                        Log.i("jArrayImage.length()", url);
                        imgUrls.add(url);
                    }
                    //get image totalCost
                    int imageTotalCost = jObjectImage.getInt("total_cost");

                    //sum totalCost
                    int temp = infoTotalCost + imageTotalCost;
                    totalCost = Integer.toString(temp);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }
        sendData sendData = new sendData();
        sendData.execute();
    }

    public class eachLoadImageTask extends AsyncTask<Bitmap, Void, Bitmap> {
        private String url;

        public eachLoadImageTask(String url) {
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

}
