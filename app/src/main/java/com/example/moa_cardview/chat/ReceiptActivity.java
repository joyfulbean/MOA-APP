package com.example.moa_cardview.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moa_cardview.R;
import com.example.moa_cardview.data.MyData;
import com.example.moa_cardview.data.StuffInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReceiptActivity extends AppCompatActivity {

    private static final String urls = "http://54.180.8.235:5000/room";

    private ImageButton ReceiptButton;
    private String roomID;

    private StuffInfo stuffRoomInfo = new StuffInfo();

    EditText stuff_name;
    EditText stuff_cost;
    TextView stuff_num;
    String stuff_img = "http://yebinfigthing";
    private static final String MyItemSend_urls = "http://54.180.8.235:5000/receipt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        //get the room id
        Intent secondIntent = getIntent();
        String message = secondIntent.getStringExtra("test_id");
        if(message == null) {
            roomID = "1";
        }
        else{
            roomID = message;
            Log.i("this", roomID);
        }

        InfoReceiveServer();

        //* create room
        ReceiptButton = findViewById(R.id.createroom_createbutton);
        ReceiptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyItemSendServer();
                Intent intent = new Intent(getApplicationContext(), ChattingActivity.class);
                intent.putExtra("room_id",roomID);
                intent.putExtra("isNew",true);
                startActivity(intent);
                finish();
            }
        });

        stuff_name = findViewById(R.id.order_myorder_product1);
        stuff_cost = findViewById(R.id.order_myorder_price1);
        stuff_num = findViewById(R.id.order_myorder_count1);
        ImageButton minus = findViewById(R.id.order_minusbutton1);
        ImageButton plus = findViewById(R.id.order_plusbutton1);

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int total_num = Integer.parseInt(stuff_num.getText().toString()) - 1;
                stuff_num.setText(Integer.toString(total_num));
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int total_num = Integer.parseInt(stuff_num.getText().toString()) + 1;
                stuff_num.setText(Integer.toString(total_num));
            }
        });

    }

    public void settingScreen(){
        // for recipe page
        TextView chatpage_title_textview = (TextView)findViewById(R.id.order_product);
        chatpage_title_textview.setText(stuffRoomInfo.getTitle());

        TextView chatpage_date_textview = (TextView)findViewById(R.id.order_date);
        chatpage_date_textview.setText(stuffRoomInfo.getOrderDate());

        TextView chatpage_time_textview = (TextView)findViewById(R.id.order_time);
        chatpage_time_textview.setText(stuffRoomInfo.getOrderTime());

        TextView chatpage_place_textview = (TextView)findViewById(R.id.order_place);
        chatpage_place_textview.setText(stuffRoomInfo.getPlace());
    }

    public void InfoReceiveServer(){
        class sendData extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                settingScreen();
//                // 아니면 여기서 추가를해줘도 될 듯 하네
//                if(isNew.equals("1")) {
//                    String content = MyData.name + "님이 입장 했습니다.";
//                    sendMessageFirebase("ENTER_EXIT", content, "none");
//                    isNew = "0";
//                }
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
                    jsonInput.put("user_email", MyData.mail);

                    RequestBody reqBody = RequestBody.create(
                            MediaType.parse("application/json; charset=utf-8"),
                            jsonInput.toString()
                    );

                    Request request = new Request.Builder()
                            .post(reqBody)
                            .url(urls + File.separator + roomID)
                            .build();

                    Response responses = null;
                    responses = client.newCall(request).execute();

                    //json array로 받아서 파싱수 thing에 저장해준다.
                    // 가장 큰 JSONObject를 가져옵니다.
                    JSONObject obj = new JSONObject(responses.body().string());
                    stuffRoomInfo.setTitle(obj.getString("title"));
                    stuffRoomInfo.setOrderDate(obj.getString("order_date"));
                    stuffRoomInfo.setOrderTime(obj.getString("order_time"));
                    stuffRoomInfo.setPlace(obj.getString("place"));
                    stuffRoomInfo.setNumUsers(obj.getString("num_user"));

//                    if(isNew.equals("1")){
//                        Log.i("isNew", "true");
//                    }
//                    else {
//                        Log.i("isNew", "false");
//                    }

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

    //* user exit room
    public void MyItemSendServer(){
        class sendData extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject jsonInput = new JSONObject();

                    jsonInput.put("room_id", roomID);
                    jsonInput.put("user_id", MyData.mail);
                    jsonInput.put("stuff_name", stuff_name);
                    jsonInput.put("stuff_cost", stuff_cost);
                    jsonInput.put("stuff_num", stuff_num);
                    jsonInput.put("stuff_img", stuff_img);

                    RequestBody reqBody = RequestBody.create(
                            MediaType.parse("application/json; charset=utf-8"),
                            jsonInput.toString()
                    );

                    Request request = new Request.Builder()
                            .post(reqBody)
                            .url(MyItemSend_urls)
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
}