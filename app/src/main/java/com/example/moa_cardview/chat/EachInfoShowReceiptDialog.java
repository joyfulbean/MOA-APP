package com.example.moa_cardview.chat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ShowReceiptEachInfoDialog extends Dialog implements View.OnClickListener{

    public ShowReceiptEachInfoDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public ShowReceiptEachInfoDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    public Activity activity;
    public Dialog dialog;
    private ImageButton popupCloseButton;
    private ArrayList<String> imgUri = new ArrayList<>();
    private String roomId;
    private TextView name;

    // name
    private String userName;
    private String userEmail;

    // order info
    private ListView listView;
    private AllImageShowReceiptAdapter imageAdapter;
    private ArrayList<OrderInfo> each_orderInfos = new ArrayList<>();
    private static final String receiptInfoUrls = "http://54.180.8.235:5000/receipt";

    private ListView listViewImage;
    private EachInfoShowReceiptAdapter infoAdapter;
    private ArrayList<Bitmap> imageBitmap = new ArrayList<>();
    private static final String postUrl = "http://54.180.8.235:5000/static/receipts/";




    public ShowReceiptEachInfoDialog(Activity a, EachInfoShowReceiptAdapter infoAdapter, String roomId, String userName, String userEmail) {
        super(a);
        this.activity = a;
        this.infoAdapter = infoAdapter;
        this.userName = userName;
        this.userEmail = userEmail;
        this.imageAdapter = imageAdapter;
        this.imgUri = imgUri;
        this.roomId = roomId;
        setupLayout();
    }

    private void setupLayout() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.individual_receipt_popup);
        listView = findViewById(R.id.each_receipt_show_list);
        receiveEachOrderInfo();
        name = findViewById(R.id.name);
        name.setText(userName);


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
                    JSONArray jArray = jObject.getJSONArray("data");

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

}
