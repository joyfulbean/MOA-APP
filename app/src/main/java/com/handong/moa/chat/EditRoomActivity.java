package com.handong.moa.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.handong.moa.R;
import com.handong.moa.data.StuffInfo;
import com.handong.moa.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditRoomActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    public static final int REQUEST_CODE = 100;
    // for server
    private static final String urls = "http://54.180.8.235:5000/room";
    private static final String imageUrls = "http://54.180.8.235:5000/room/og";
    private StuffInfo stuffRoomInfo = new StuffInfo();
    private String roomID;

    private ImageButton backButton;
    private ImageButton editButton;

    // for intent layout
    private TextView foodDateText;
    private TextView stuffDateText;
    private LinearLayout foodLayout;
    private LinearLayout stuffLayout;

    // for intput
    private TextView title;
    private TextView place;


    private TextView stuffAddressText;
    private TextView placeName;

    private CheckBox orderTimeCB;
    private CheckBox orderDateCB;
    private CheckBox stuffLinkCB;

    private StuffInfo stuffInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_room);

        Intent intent = getIntent();
        stuffInfo = (StuffInfo) intent.getSerializableExtra("stuff_info");
        roomID = intent.getStringExtra("room_id");

        setRoomInfo(stuffInfo);

        //* back page
        backButton = findViewById(R.id.createroom_backbutton_for_edit_room);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        stuffAddressText = findViewById(R.id.edit_room_stuffaddress_textview);

        //* create room
        editButton = findViewById(R.id.edit_room_edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setting();

                if(title.getText().toString().length() != 0 && place.getText().toString().length() != 0){
                    if(isValidString()) {
                        Log.i("isValid","It is valid");
                        sendServer();
                    }
                    else{
                        Toast.makeText(EditRoomActivity.this, "사용할 수 없는 특수문자가 사용되었습니다. 다시 입력해주세요", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(EditRoomActivity.this, "*가 표시된 필수 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();

                }


            }
        });

        //* stuff layout
        // stuffLayout = (LinearLayout) findViewById(R.id.edit_room_stufflayout_for_edit_room);        //Category Layout

        stuffDateText = findViewById(R.id.edit_room_stuff_date_textview);
        findViewById(R.id.edit_room_stuff_date_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(); // 날짜 버튼 클릭시 Date Picker Dialog 보여줌
            }
        });



        //* CD:Check Box
        orderTimeCB = findViewById(R.id.edit_order_time_CB);
        orderTimeCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(orderTimeCB.isChecked()) {
                    findViewById(R.id.edit_room_stuff_timepicker).setVisibility(View.VISIBLE);
//                    findViewById(R.id.edit_order_time_line).setVisibility(View.VISIBLE);
                }
                else {
                    findViewById(R.id.edit_room_stuff_timepicker).setVisibility(View.GONE);
//                    findViewById(R.id.edit_order_time_line).setVisibility(View.GONE);
                }
            }
        });
        stuffLinkCB = findViewById(R.id.edit_stuff_link_CB);
        findViewById(R.id.edit_stuff_link_CB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stuffLinkCB.isChecked()) {
                    findViewById(R.id.edit_room_stuffLink_edittext).setVisibility(View.VISIBLE);
                    findViewById(R.id.edit_stuff_link_line).setVisibility(View.VISIBLE);
                }
                else{
                    findViewById(R.id.edit_room_stuffLink_edittext).setVisibility(View.GONE);
                    findViewById(R.id.edit_stuff_link_line).setVisibility(View.GONE);
                }
            }
        });
        orderDateCB = findViewById(R.id.edit_order_date_CB);
        findViewById(R.id.edit_order_date_CB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(orderDateCB.isChecked()) {
                    findViewById(R.id.edit_order_date_RL).setVisibility(View.VISIBLE);
//                    findViewById(R.id.edit_order_date_line).setVisibility(View.VISIBLE);
                }
                else{
                    findViewById(R.id.edit_order_date_RL).setVisibility(View.GONE);
//                    findViewById(R.id.edit_order_date_line).setVisibility(View.GONE);
                }
            }
        });

        //* Check box
        if(!stuffInfo.getOrderTime().isEmpty()){
            orderTimeCB.setChecked(true);
            findViewById(R.id.edit_room_stuff_timepicker).setVisibility(View.VISIBLE);
//            findViewById(R.id.edit_order_time_line).setVisibility(View.VISIBLE);
        }
        if(!stuffInfo.getStuffLink().isEmpty()){
            stuffLinkCB.setChecked(true);
            findViewById(R.id.edit_room_stuffLink_edittext).setVisibility(View.VISIBLE);
            findViewById(R.id.edit_stuff_link_line).setVisibility(View.VISIBLE);
        }
        if(!stuffInfo.getOrderDate().isEmpty()){
            orderDateCB.setChecked(true);
            findViewById(R.id.edit_order_date_RL).setVisibility(View.VISIBLE);
//            findViewById(R.id.edit_order_date_line).setVisibility(View.VISIBLE);
        }

    }

    private void setRoomInfo(StuffInfo stuffInfo) {
        // for recipe page
        TextView title = (TextView)findViewById(R.id.edit_room_stuffTitle_edittext);
        title.setText(stuffInfo.getTitle());

        TextView date = (TextView)findViewById(R.id.edit_room_stuff_date_textview);
        date.setText(stuffInfo.getOrderDate());

        if(!stuffInfo.getOrderTime().equals("")) {
            String[] strTime = stuffInfo.getOrderTime().split(" ");
            String hour = strTime[0].replaceAll("\\D+", "");
            String min = strTime[1].replaceAll("\\D+", "");

            TimePicker time = findViewById(R.id.edit_room_stuff_timepicker);
            time.setHour(Integer.parseInt(hour));
            time.setMinute(Integer.parseInt(min));
        }

        TextView place = (TextView)findViewById(R.id.edit_room_stuffaddress_textview);
        place.setText(stuffInfo.getPlace());

        TextView link = (TextView)findViewById(R.id.edit_room_stuffLink_edittext);
        link.setText(stuffInfo.getStuffLink());
    }

    //* date setting
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        month = month + 1;
        String date = year + "-" + month + "-" + day;
        stuffDateText.setText(date);
    }

    //* date setting
    private void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    //* getting the info
    public void setting(){
        //제목
        title = findViewById(R.id.edit_room_stuffTitle_edittext);
        //날짜
        stuffDateText = findViewById(R.id.edit_room_stuff_date_textview);
        //시간
        TimePicker time = findViewById(R.id.edit_room_stuff_timepicker);
        //장소
        place = findViewById(R.id.edit_room_stuffaddress_textview);
        //링크
        TextView link = findViewById(R.id.edit_room_stuffLink_edittext);

        time.clearFocus();
        int hour = time.getHour();
        int min = time.getMinute();

        stuffRoomInfo.setTitle(title.getText().toString());
        stuffRoomInfo.setPlace(place.getText().toString());

        if(stuffLinkCB.isChecked())
            stuffRoomInfo.setStuffLink(link.getText().toString());
        else stuffRoomInfo.setStuffLink("");
        if(orderDateCB.isChecked())
            stuffRoomInfo.setOrderDate(stuffDateText.getText().toString());
        else stuffRoomInfo.setOrderDate("");
        if(orderTimeCB.isChecked())
            stuffRoomInfo.setOrderTime(hour + "시 " + min + "분");
        else stuffRoomInfo.setOrderTime("");
    }

    //* for security
    public boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }

        return true;
    }

    //* for security
    private boolean isValidString(){
        if(!Utils.isValidInput(stuffRoomInfo.getTitle()))
            return false;
        else if(!Utils.isValidInput(stuffRoomInfo.getPlace()))
            return false;
        else {
            return true;
        }
    }

    //* for send image url (og tag)
    public void ImageUrlSendServer(){
        class sendData extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
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

                    jsonInput.put("room_id", roomID);
                    jsonInput.put("image_url", stuffRoomInfo.getImageUrl());
                    jsonInput.put("og_title", stuffRoomInfo.getOgTitle());
                    Log.i("db", stuffRoomInfo.getImageUrl());
                    RequestBody reqBody = RequestBody.create(
                            MediaType.parse("application/json; charset=utf-8"),
                            jsonInput.toString()
                    );

                    Request request = new Request.Builder()
                            .post(reqBody)
                            .url(imageUrls)
                            .build();

                    Response responses = null;
                    responses = client.newCall(request).execute();
                    responses.close();

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

    //* for send room information
    public void sendServer(){
        class sendData extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Intent intent = new Intent(getApplicationContext(), ChattingActivity.class);
                intent.putExtra("room_id",roomID);
                intent.putExtra("isNew",false);
                startActivity(intent);
                finish();
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

                    Log.i("editRoom", roomID);

                    jsonInput.put("room_id", roomID);
                    jsonInput.put("title", stuffRoomInfo.getTitle());
                    jsonInput.put("date", stuffRoomInfo.getOrderDate());
                    jsonInput.put("time", stuffRoomInfo.getOrderTime());
                    jsonInput.put("place", stuffRoomInfo.getPlace());
                    jsonInput.put("link", stuffRoomInfo.getStuffLink());

                    RequestBody reqBody = RequestBody.create(
                            MediaType.parse("application/json; charset=utf-8"),
                            jsonInput.toString()
                    );

                    Request request = new Request.Builder()
                            .put(reqBody)
                            .url(urls)
                            .build();

                    Response responses = null;
                    responses = client.newCall(request).execute();

                    JSONObject jObject = new JSONObject(responses.body().string());
                    roomID = jObject.getString("room_id");
                    responses.close();
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