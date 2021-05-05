package com.handong.moa.chat;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.handong.moa.data.MyData;
import com.handong.moa.R;
import com.handong.moa.data.ServerInfo;
import com.handong.moa.data.StuffInfo;
import com.handong.moa.receipt.ReceiptActivity;
import com.handong.moa.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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



public class MakingRoomActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    public static final int REQUEST_CODE = 100;
    // for server
    private static final String urls = ServerInfo.getUrl() + "room";
    private static final String imageUrls = ServerInfo.getUrl() + "sroom/og";
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
    private TextView stuffDateText;
    private LinearLayout stuffLayout;

    // for intput
    private TextView title;
    private TextView place;


    private TextView stuffAddressText;
    private TextView placeName;

    private CheckBox orderTimeCB;
    private CheckBox orderDateCB;
    private CheckBox stuffLinkCB;

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

        stuffAddressText = findViewById(R.id.createroom_stuffaddress_textview);

        //* create room
        createButton = findViewById(R.id.createroom_createbutton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = rg.getCheckedRadioButtonId();
                radioStuffButton = (RadioButton) findViewById(id);
                checkingRadio = (String) radioStuffButton.getText();
                setting(checkingRadio);

                if(title.getText().toString().length() != 0 ){
                    if(isValidString()) {
                        Log.i("isValid","It is valid");
                        sendServer();
                    }
                    else{
                        Toast.makeText(MakingRoomActivity.this, "사용할 수 없는 특수문자가 사용되었습니다. 다시 입력해주세요", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MakingRoomActivity.this, "*가 표시된 필수 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();

                }


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

        setDateToToday();

        //* CD:Check Box
        orderTimeCB = findViewById(R.id.order_time_CB);
        orderTimeCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(orderTimeCB.isChecked()) {
                    findViewById(R.id.createroom_stuff_timepicker).setVisibility(View.VISIBLE);
//                    findViewById(R.id.order_time_line).setVisibility(View.VISIBLE);
                }
                else {
                    findViewById(R.id.createroom_stuff_timepicker).setVisibility(View.GONE);
//                    findViewById(R.id.order_time_line).setVisibility(View.GONE);
                }
            }
        });
        stuffLinkCB = findViewById(R.id.stuff_link_CB);
        findViewById(R.id.stuff_link_CB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stuffLinkCB.isChecked()) {
                    findViewById(R.id.createroom_stuffLink_edittext).setVisibility(View.VISIBLE);
                    findViewById(R.id.stuff_link_line).setVisibility(View.VISIBLE);
                }
                else{
                    findViewById(R.id.createroom_stuffLink_edittext).setVisibility(View.GONE);
                    findViewById(R.id.stuff_link_line).setVisibility(View.GONE);
                }
            }
        });
//        orderDateCB = findViewById(R.id.order_date_CB);
//        findViewById(R.id.order_date_CB).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(orderDateCB.isChecked()) {
//                    findViewById(R.id.order_date_RL).setVisibility(View.VISIBLE);
//                    findViewById(R.id.order_date_line).setVisibility(View.VISIBLE);
//                }
//                else{
//                    findViewById(R.id.order_date_RL).setVisibility(View.GONE);
//                    findViewById(R.id.order_date_line).setVisibility(View.GONE);
//                }
//            }
//        });

        //방만들기 버튼을 누를때 작동
        ImageButton radioInfoButton = (ImageButton)findViewById(R.id.createroom_createbutton);
        radioInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = rg.getCheckedRadioButtonId();
                radioStuffButton = (RadioButton) findViewById(id);
                checkingRadio = (String) radioStuffButton.getText();
                //getCheckedRadioButtonId() 의 리턴값은 선택된 RadioButton 의 id 값.
                //방만들기 안에 값이 다 채워져잇는지 확인하는 코드 짜기
                setting(checkingRadio);
                sendServer();

//                if(id == R.id.createroom_radiostuff || id == R.id.createroom_radiofood ){
//                    sendServer();
//
//                }else if(id == R.id.createroom_radioott){
//                    Toast.makeText(MakingRoomActivity.this, "오픈 준비중인 서비스 입니다.", Toast.LENGTH_SHORT).show();
//
//                }else if(id == R.id.createroom_radiotransport){
//                    Toast.makeText(MakingRoomActivity.this, "오픈 준비중인 서비스 입니다.", Toast.LENGTH_SHORT).show();
//
//                }
            }
        });

    }
    //* date setting
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        month = month + 1;
        String date = month + "월" + " " + day + "일";
        stuffDateText.setText(date);
    }
    private void setDateToToday() {
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        month = month + 1;
        String date = month + "월" + " " + day + "일";
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
    public void setting(String checkingRadio){
        //제목
        title = findViewById(R.id.createroom_stuffTitle_edittext);
        //날짜
        stuffDateText = findViewById(R.id.createroom_stuff_date_textview);
        //시간
        TimePicker time = findViewById(R.id.createroom_stuff_timepicker);
        //장소
        place = findViewById(R.id.createroom_stuffaddress_textview);
        //링크
        TextView link = findViewById(R.id.createroom_stuffLink_edittext);
        //가격
        TextView cost = findViewById(R.id.createroom_stuffprice_edittext);

        time.clearFocus();
        int hour = time.getHour();
        int min = time.getMinute();

        stuffRoomInfo.setCategory(checkingRadio);
        stuffRoomInfo.setTitle(title.getText().toString());
        stuffRoomInfo.setPlace(place.getText().toString());

        stuffRoomInfo.setOrderDate(stuffDateText.getText().toString());

        if(stuffLinkCB.isChecked())
            stuffRoomInfo.setStuffLink(link.getText().toString());
        else stuffRoomInfo.setStuffLink("");

        if(orderTimeCB.isChecked())
            stuffRoomInfo.setOrderTime(hour + "시 " + min + "분");
        else stuffRoomInfo.setOrderTime("");

        stuffRoomInfo.setStuffCost(cost.getText().toString());
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

    //* save image (og tag)
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

    //* get og tag (og tag)
    private void getOGTag(String url){
        String imageUrl = null;
        String linkTitle = null;
        if (isValidURL(url)) {
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
        else{
            imageUrl = "https://github.com/HGUMOA/MOA/blob/master/app/src/main/res/drawable-xxhdpi/logosmall.png?raw=true";
            linkTitle = " ";
        }

        if(imageUrl == null)
            imageUrl = "https://github.com/HGUMOA/MOA/blob/master/app/src/main/res/drawable-xxhdpi/logosmall.png?raw=true";

        if(linkTitle == null)
            linkTitle = " ";

        stuffRoomInfo.setImageUrl(imageUrl);
        stuffRoomInfo.setOgTitle(linkTitle);
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

                sendMessageFirebase("MOA", "주문서 기능을 사용해보세요!!!!!!!!", "none", "MOA", "none");
                Intent intent = new Intent(getApplicationContext(), ChattingActivity.class);
//                Intent intent = new Intent(getApplicationContext(), ReceiptActivity.class);
                intent.putExtra("room_id",roomID);
                saveImage();
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

                    jsonInput.put("creator_email",  MyData.mail);
                    jsonInput.put("creator_name",  MyData.name);
                    jsonInput.put("category", stuffRoomInfo.getCategory());
                    jsonInput.put("title", stuffRoomInfo.getTitle());
                    jsonInput.put("order_date", stuffRoomInfo.getOrderDate());
                    jsonInput.put("order_time", stuffRoomInfo.getOrderTime());
                    jsonInput.put("place", stuffRoomInfo.getPlace());
                    jsonInput.put("stuff_link", stuffRoomInfo.getStuffLink());
//                    jsonInput.put("stuff_cost", Integer.parseInt(stuffRoomInfo.getStuffCost()));

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

    //send message on firebase
    private void sendMessageFirebase(String name, String content, String image, String uid, String url){
        FirebaseDatabase firebaseDatabase;                           //Firebase Database 관리 객체참조변수
        DatabaseReference roodIdReference;
        Calendar calendar = Calendar.getInstance(); //현재 시간을 가지고 있는 객체
        String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);

        ChatMessageItem messageItem = new ChatMessageItem(name, content, time, image, uid, url);

        firebaseDatabase = FirebaseDatabase.getInstance();
        roodIdReference = firebaseDatabase.getReference(roomID);
        roodIdReference.push().setValue(messageItem);
    }

    //* about radio group
    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override public void onCheckedChanged(RadioGroup radioGroup, @IdRes int id) {

            radiodefaultButton = findViewById(R.id.createroom_radiostuff);

            if(id == R.id.createroom_radiofood){
                TextView tv1 = findViewById(R.id.createroom_stuffTitle_text);
                tv1.setText("배달음식 이름");
                EditText et1 = findViewById(R.id.createroom_stuffTitle_edittext);
                et1.setHint("배달음식 이름");
                ImageView ig1 = findViewById(R.id.createroom_img1);
                ig1.setVisibility(View.VISIBLE);
                TextView tv2 = findViewById(R.id.createroom_stuffPlace_text);
                tv2.setVisibility(View.VISIBLE);
                EditText et2 = findViewById(R.id.createroom_stuffaddress_textview);
                et2.setVisibility(View.VISIBLE);
                TextView ck1 = findViewById(R.id.order_date_CB);
                ck1.setVisibility(View.VISIBLE);
                RelativeLayout rl =findViewById(R.id.order_date_RL);
                rl.setVisibility(View.VISIBLE);
                ImageView ig2 = findViewById(R.id.order_date_line);
                ig2.setVisibility(View.VISIBLE);
                CheckBox ck2 = findViewById(R.id.order_time_CB);
                ck2.setVisibility(View.VISIBLE);


            } else if(id == R.id.createroom_radiostuff){
                TextView tv = findViewById(R.id.createroom_stuffTitle_text);
                tv.setText("상품 이름");
                EditText et = findViewById(R.id.createroom_stuffTitle_edittext);
                et.setHint("상품 이름");
                ImageView ig1 = findViewById(R.id.createroom_img1);
                ig1.setVisibility(View.VISIBLE);
                TextView tv2 = findViewById(R.id.createroom_stuffPlace_text);
                tv2.setVisibility(View.VISIBLE);
                EditText et2 = findViewById(R.id.createroom_stuffaddress_textview);
                et2.setVisibility(View.VISIBLE);
                TextView ck1 = findViewById(R.id.order_date_CB);
                ck1.setVisibility(View.VISIBLE);
                RelativeLayout rl =findViewById(R.id.order_date_RL);
                rl.setVisibility(View.VISIBLE);
                ImageView ig2 = findViewById(R.id.order_date_line);
                ig2.setVisibility(View.VISIBLE);
                CheckBox ck2 = findViewById(R.id.order_time_CB);
                ck2.setVisibility(View.VISIBLE);

            }else if(id == R.id.createroom_radioott){
                TextView tv1 = findViewById(R.id.createroom_stuffTitle_text);
                tv1.setText("방 제목");
                EditText et1 = findViewById(R.id.createroom_stuffTitle_edittext);
                et1.setHint("ex.넷플릭스 4인 구합니다.");
                ImageView ig1 = findViewById(R.id.createroom_img1);
                ig1.setVisibility(View.GONE);
                TextView tv2 = findViewById(R.id.createroom_stuffPlace_text);
                tv2.setVisibility(View.GONE);
                EditText et2 = findViewById(R.id.createroom_stuffaddress_textview);
                et2.setVisibility(View.GONE);
                TextView ck1 = findViewById(R.id.order_date_CB);
                ck1.setVisibility(View.GONE);
                RelativeLayout rl =findViewById(R.id.order_date_RL);
                rl.setVisibility(View.GONE);
                ImageView ig2 = findViewById(R.id.order_date_line);
                ig2.setVisibility(View.GONE);
                CheckBox ck2 = findViewById(R.id.order_time_CB);
                ck2.setVisibility(View.GONE);

            }else if(id == R.id.createroom_radiotransport){
                TextView tv = findViewById(R.id.createroom_stuffTitle_text);
                tv.setText("이동 경로");
                EditText et = findViewById(R.id.createroom_stuffTitle_edittext);
                et.setHint("ex. 한동대 -> 포항역");
                ImageView ig1 = findViewById(R.id.createroom_img1);
                ig1.setVisibility(View.GONE);
                TextView tv2 = findViewById(R.id.createroom_stuffPlace_text);
                tv2.setVisibility(View.GONE);
                EditText et2 = findViewById(R.id.createroom_stuffaddress_textview);
                et2.setVisibility(View.GONE);
                TextView ck1 = findViewById(R.id.order_date_CB);
                ck1.setVisibility(View.VISIBLE);
                ck1.setText("출발 날짜");
                RelativeLayout rl =findViewById(R.id.order_date_RL);
                rl.setVisibility(View.VISIBLE);
                ImageView ig2 = findViewById(R.id.order_date_line);
                ig2.setVisibility(View.VISIBLE);
                CheckBox ck2 = findViewById(R.id.order_time_CB);
                ck2.setVisibility(View.VISIBLE);
                ck2.setText("출발 시간");
                //radiodefaultButton.setChecked(true);

            }
        }
    };
}