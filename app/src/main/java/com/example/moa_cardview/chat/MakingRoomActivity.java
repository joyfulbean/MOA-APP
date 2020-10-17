package com.example.moa_cardview.chat;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.moa_cardview.data.MyData;
import com.example.moa_cardview.R;
import com.example.moa_cardview.data.StuffInfo;
import com.example.moa_cardview.item_page.RecyclerAdapter;
import com.example.moa_cardview.util.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class MakingRoomActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    // for server
    private static final String urls = "http://54.180.8.235:3306/room/stuff";
    private static final String imageUrls = "http://54.180.8.235:3306/room/og";
    private StuffInfo stuffRoomInfo = new StuffInfo();
    private String roomID;

    private ImageButton backButton;
    private ImageButton createButton;

    // for radio
    private RadioGroup radioGroup;
    private RadioButton radioStuffButton;
    private RadioButton radiodefaultButton;
    public String checkingRadio;

    // for intent layout
    private TextView foodDateText;
    private TextView stuffDateText;
    private LinearLayout foodLayout;
    private LinearLayout stuffLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toast.makeText(MakingRoomActivity.this, "시작", Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_making_room);

        //* radio button
        final RadioGroup rg = (RadioGroup)findViewById(R.id.createroom_radiogroup);
        radioGroup = (RadioGroup) findViewById(R.id.createroom_radiogroup);
        radioGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);

        //* back page
        backButton = findViewById(R.id.createroom_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //* create room
        createButton = findViewById(R.id.createroom_createbutton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = rg.getCheckedRadioButtonId();
                radioStuffButton = (RadioButton) findViewById(id);
                checkingRadio = (String) radioStuffButton.getText();
                setting(checkingRadio);

                if(isValidString()) {
                    Log.i("isValid","It is valid");
                    sendServer();


                    Intent intent = new Intent(MakingRoomActivity.this, ChattingActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(MakingRoomActivity.this, "사용할 수 없는 특수문자가 사용되었습니다. 다시 입력해주세", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //* food layout
        foodLayout = (LinearLayout) findViewById(R.id.createroom_foodlayout);        //Category Layout
        foodDateText = findViewById(R.id.createroom_food_date_textview);
        findViewById(R.id.createroom_food_date_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(); // 날짜 버튼 클릭시 Date Picker Dialog 보여줌
            }
        });

        //* stuff layout
        stuffLayout = (LinearLayout) findViewById(R.id.createroom_stufflayout);        //Category Layout
        stuffDateText = findViewById(R.id.createroom_stuff_date_textview);
        findViewById(R.id.createroom_stuff_date_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(); // 날짜 버튼 클릭시 Date Picker Dialog 보여줌
            }
        });



        //방만들기 버튼을 누를때 작동
        /*ImageButton radioInfoButton = (ImageButton)findViewById(R.id.createroom_createbutton);
        radioInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = rg.getCheckedRadioButtonId();
                radioStuffButton = (RadioButton) findViewById(id);
                checkingRadio = (String) radioStuffButton.getText();
                //getCheckedRadioButtonId() 의 리턴값은 선택된 RadioButton 의 id 값.
                //방만들기 안에 값이 다 채워져잇는지 확인하는 코드 짜기

                setting(checkingRadio);


                if(id == R.id.createroom_radiostuff){
                    sendServer();

                } else if(id == R.id.createroom_radiofood){
                    Toast.makeText(MakingRoomActivity.this, "오픈 준비중인 서비스 입니다.", Toast.LENGTH_SHORT).show();

                }else if(id == R.id.createroom_radioott){
                    Toast.makeText(MakingRoomActivity.this, "오픈 준비중인 서비스 입니다.", Toast.LENGTH_SHORT).show();

                }else if(id == R.id.createroom_radiotransport){
                    Toast.makeText(MakingRoomActivity.this, "오픈 준비중인 서비스 입니다.", Toast.LENGTH_SHORT).show();

                }
            }
        });
        */
    }
    //* for get the room information
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        String date = year + "-" + month + "-" + day;
        stuffDateText.setText(date);
    }

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

    public void setting(String checkingRadio){
        //제목
        TextView title = findViewById(R.id.createroom_stuffTitle_edittext);
        //날짜
        stuffDateText = findViewById(R.id.createroom_stuff_date_textview);
        //시간
        TimePicker time = findViewById(R.id.createroom_stuff_timepicker);
        //장소
        TextView place = findViewById(R.id.createroom_stuffaddress_edittext);
        //링크
        TextView link = findViewById(R.id.createroom_stuffLink_edittext);
        //가격
        TextView cost = findViewById(R.id.createroom_stuffprice_edittext);

        time.clearFocus();
        int hour = time.getHour();
        int min = time.getMinute();

        stuffRoomInfo.setCategory(checkingRadio);
        stuffRoomInfo.setTitle(title.getText().toString());
        stuffRoomInfo.setStuffLink(link.getText().toString());
        stuffRoomInfo.setOrderDate(stuffDateText.getText().toString());
        stuffRoomInfo.setOrderTime(hour + ":" + min);
        stuffRoomInfo.setPlace(place.getText().toString());
        stuffRoomInfo.setStuffCost(cost.getText().toString());
    }

    public void saveImage(){
        new Thread() {
            public void run() {
                String image_url = stuffRoomInfo.getStuffLink();
                Log.i("OGT", "image_url:in showImage " + image_url);
                getOGTag(image_url);
                // Display a png image from the specified file
                ImageUrlSendServer();
            }
        }.start();
    }

    private void getOGTag(String url){
        String imageUrl = null;
        String linkTitle = null;
        if (url != null) {
            Document doc = null;

            try {
                doc = Jsoup.connect(url).get(); // -- 1. get방식의 URL에 연결해서 가져온 값을 doc에 담는다.
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            Elements titles = doc.select("meta"); // -- 2. doc에서 selector의 내용을 가져와 Elemntes 클래스에 담는다.
            for(Element element: titles) { // -- 3. Elemntes 길이만큼 반복한다.
                if(element.attr("property").equals("og:image"))
                    imageUrl = element.attr("content");
                if(element.attr("property").equals("og:title"))
                    linkTitle = element.attr("content");
                System.out.println(element.attr("content")); // -- 4. 원하는 요소가 출력된다.
            }
        }
        if(imageUrl == null)
            imageUrl = "https://github.com/HGUMOA/MOA/blob/master/app/src/main/res/drawable-xxhdpi/logosmall.png?raw=true";

        if(linkTitle == null)
            linkTitle = " ";

        stuffRoomInfo.setImageUrl(imageUrl);
        stuffRoomInfo.setOgTitle(linkTitle);
    }

    private boolean isValidString(){
        if(!Utils.isValidInput(stuffRoomInfo.getTitle()))
            return false;
        else if(!Utils.isValidInput(stuffRoomInfo.getStuffCost()))
            return false;
//        else if(!Utils.isValidInput(stuffRoomInfo.getStuffLink()))
//            return false;
        else if(!Utils.isValidInput(stuffRoomInfo.getPlace()))
            return false;
        else {
            return true;
        }
    }

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
                            .put(reqBody)
                            .url(imageUrls)
                            .build();

                    Response responses = null;
                    responses = client.newCall(request).execute();

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
                intent.putExtra("test_id",roomID);
                saveImage();
                startActivity(intent);
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

                    jsonInput.put("creator_email",  MyData.mail);
                    jsonInput.put("creator_name",  MyData.name);
                    jsonInput.put("category", stuffRoomInfo.getCategory());
                    jsonInput.put("title", stuffRoomInfo.getTitle());
                    jsonInput.put("order_date", stuffRoomInfo.getOrderDate());
                    jsonInput.put("order_time", stuffRoomInfo.getOrderTime());
                    jsonInput.put("place", stuffRoomInfo.getPlace());
                    jsonInput.put("stuff_link", stuffRoomInfo.getStuffLink());
                    jsonInput.put("stuff_cost", stuffRoomInfo.getStuffCost());

                    RequestBody reqBody = RequestBody.create(
                            MediaType.parse("application/json; charset=utf-8"),
                            jsonInput.toString()
                    );

                    Request request = new Request.Builder()
                            .post(reqBody)
                            .url(urls)
                            .build();

                    Response responses = null;
                    responses = client.newCall(request).execute();

                    JSONObject jObject = new JSONObject(responses.body().string());
                    roomID = jObject.getString("room_id");
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

    //* about radio group
    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override public void onCheckedChanged(RadioGroup radioGroup, @IdRes int id) {

            //radio button
//            final RadioGroup rg = (RadioGroup)findViewById(R.id.createroom_radiogroup);
//            int id2 = rg.getCheckedRadioButtonId();
//            radioStuffButton = (RadioButton) findViewById(id2);
            radiodefaultButton = findViewById(R.id.createroom_radiostuff);

            if(id == R.id.createroom_radiofood){
                Toast.makeText(MakingRoomActivity.this, "오픈 준비중인 서비스 입니다.", Toast.LENGTH_SHORT).show();
//                foodLayout.setVisibility(View.VISIBLE);
//                stuffLayout.setVisibility(View.GONE);
                foodLayout.setVisibility(View.GONE);
                stuffLayout.setVisibility(View.VISIBLE);
                radiodefaultButton.setChecked(true);

            } else if(id == R.id.createroom_radiostuff){
                foodLayout.setVisibility(View.GONE);
                stuffLayout.setVisibility(View.VISIBLE);

            }else if(id == R.id.createroom_radioott){
                Toast.makeText(MakingRoomActivity.this, "오픈 준비중인 서비스 입니다.", Toast.LENGTH_SHORT).show();
                foodLayout.setVisibility(View.GONE);
                stuffLayout.setVisibility(View.VISIBLE);
                radiodefaultButton.setChecked(true);

            }else if(id == R.id.createroom_radiotransport){
                Toast.makeText(MakingRoomActivity.this, "오픈 준비중인 서비스 입니다.", Toast.LENGTH_SHORT).show();
                foodLayout.setVisibility(View.GONE);
                stuffLayout.setVisibility(View.VISIBLE);
                radiodefaultButton.setChecked(true);

            }
        }
    };
}