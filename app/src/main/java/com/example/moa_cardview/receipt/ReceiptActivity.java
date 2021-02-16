package com.example.moa_cardview.receipt;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moa_cardview.R;
import com.example.moa_cardview.chat.ChatAdapter;
import com.example.moa_cardview.chat.ChatMessageItem;
import com.example.moa_cardview.chat.ChattingActivity;
import com.example.moa_cardview.data.MyData;
import com.example.moa_cardview.data.OrderInfo;
import com.example.moa_cardview.data.StuffInfo;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReceiptActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String urls = "http://54.180.8.235:5000/room";
    private static final String MyItemSend_urls = "http://54.180.8.235:5000/receipt";

//    private StuffInfo stuffRoomInfo = new StuffInfo();

    private ImageButton ReceiptButton;
    private String roomID;
    private OrderInfo listInfo = new OrderInfo();

    TextView myOrderAddButton, othersOrderAddButton;

    EditText stuff_name;
    EditText stuff_cost;
    TextView stuff_num;
    ListView listView,listView2;
    //image구현 필요.
    //String stuff_img = "http://yebinfigthing";

    private ArrayList<OrderInfo> orderInfos = new ArrayList<>();
    private AdapterView.OnItemClickListener listener;
    private MyAdapter myAdapter;


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

        stuff_name = findViewById(R.id.order_myorder_product1);
        stuff_cost = findViewById(R.id.order_myorder_price1);
        stuff_num = findViewById(R.id.order_myorder_count1);
        listView = (ListView)findViewById(R.id.order_myorderothers_listview);

        //receive 받아서 방 정보 세팅
        //InfoReceiveServer();

        //주문 목록 세팅
        ListReceiveServer();

        //null handling 필요...
        orderInfos.add(new OrderInfo(roomID,"hello","123","12"));

        myAdapter = new MyAdapter(this, orderInfos, stuff_name, stuff_cost, stuff_num);
        listView.setAdapter(myAdapter);

        //상품추가 버튼
        myOrderAddButton = findViewById(R.id.order_myorder_addbutton);
        myOrderAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderInfo orderInfo = new OrderInfo(roomID, stuff_name.getText().toString(), stuff_cost.getText().toString(), stuff_num.getText().toString());
                orderInfos.add(orderInfo);
                myAdapter.notifyDataSetChanged();
                //서버와 받는거 부분 에러..!! 디버깅 필요.!!
                //서버에게 보내는건 나중에 "주문서 등록"버튼 누를때, orderInfos를 for문으로 보내는게 더 나을듯해보임.
                //MyItemSendServer();
                //디자인 회색처리 해주세요
                stuff_name.setText("");
                stuff_cost.setText("");
                stuff_num.setText("1");
            }
        });

        //나도 추가 버튼 (구현 아직 안됨) 추후 구현 필요
        othersOrderAddButton = findViewById(R.id.order_othersorder_add_button1);
        othersOrderAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "toast message",Toast.LENGTH_SHORT);

            }
        });

        //* create room
        ReceiptButton = findViewById(R.id.createroom_createbutton);
        ReceiptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChattingActivity.class);
                intent.putExtra("room_id",roomID);
                intent.putExtra("isNew",true);
                startActivity(intent);
                finish();
            }
        });

        //숫자 plus mius 버튼 활성화
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

//    public void settingScreen(){
//        // for recipe page
//        TextView chatpage_title_textview = (TextView)findViewById(R.id.order_product);
//        chatpage_title_textview.setText(stuffRoomInfo.getTitle());
//
//        TextView chatpage_date_textview = (TextView)findViewById(R.id.order_date);
//        chatpage_date_textview.setText(stuffRoomInfo.getOrderDate());
//
//        TextView chatpage_time_textview = (TextView)findViewById(R.id.order_time);
//        chatpage_time_textview.setText(stuffRoomInfo.getOrderTime());
//
//        TextView chatpage_place_textview = (TextView)findViewById(R.id.order_place);
//        chatpage_place_textview.setText(stuffRoomInfo.getPlace());
//    }
//
//    public void InfoReceiveServer(){
//        class sendData extends AsyncTask<Void, Void, String> {
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//            }
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//                settingScreen();
////                // 아니면 여기서 추가를해줘도 될 듯 하네
////                if(isNew.equals("1")) {
////                    String content = MyData.name + "님이 입장 했습니다.";
////                    sendMessageFirebase("ENTER_EXIT", content, "none");
////                    isNew = "0";
////                }
//            }
//            @Override
//            protected void onProgressUpdate(Void... values) {
//                super.onProgressUpdate(values);
//            }
//            @Override
//            protected void onCancelled(String s) {
//                super.onCancelled(s);
//            }
//            @Override
//            protected void onCancelled() {
//                super.onCancelled();
//            }
//            @Override
//            protected String doInBackground(Void... voids) {
//                try {
//                    OkHttpClient client = new OkHttpClient();
//
//                    JSONObject jsonInput = new JSONObject();
//                    jsonInput.put("room_id", roomID);
//                    jsonInput.put("user_email", MyData.mail);
//
//                    RequestBody reqBody = RequestBody.create(
//                            MediaType.parse("application/json; charset=utf-8"),
//                            jsonInput.toString()
//                    );
//
//                    Request request = new Request.Builder()
//                            .post(reqBody)
//                            .url(urls + File.separator + roomID)
//                            .build();
//
//                    Response responses = null;
//                    responses = client.newCall(request).execute();
//
//                    //json array로 받아서 파싱수 thing에 저장해준다.
//                    // 가장 큰 JSONObject를 가져옵니다.
//                    JSONObject obj = new JSONObject(responses.body().string());
//                    stuffRoomInfo.setTitle(obj.getString("title"));
//                    stuffRoomInfo.setOrderDate(obj.getString("order_date"));
//                    stuffRoomInfo.setOrderTime(obj.getString("order_time"));
//                    stuffRoomInfo.setPlace(obj.getString("place"));
//                    stuffRoomInfo.setNumUsers(obj.getString("num_user"));
//
////                    if(isNew.equals("1")){
////                        Log.i("isNew", "true");
////                    }
////                    else {
////                        Log.i("isNew", "false");
////                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//        }
//        sendData sendData = new sendData();
//        sendData.execute();
//    }

        public void listsettingScreen(){
        // for recipe page

    }

        public void ListReceiveServer(){
        class sendData extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                listsettingScreen();
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
                    Log.i("listTest", "list server start");
                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .get()
                            .url(MyItemSend_urls + "/" + "7")
                            .build();

                    Response responses = null;
                    responses = client.newCall(request).execute();

                    JSONObject jObject = new JSONObject(responses.body().string());
                    JSONArray jArray = jObject.getJSONArray("data");

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject obj = jArray.getJSONObject(i);

                        listInfo.setId(obj.getString("id"));
                        listInfo.setRef_cnt(obj.getString("ref_cnt"));
                        listInfo.setRegistered_on(obj.getString("registered_on"));
                        listInfo.setCost(obj.getString("stuff_cost"));
                        //listInfo.set(obj.getString("stuff_img"));
                        listInfo.setStuffName(obj.getString("stuff_name"));
                        listInfo.setNum(obj.getString("stuff_num"));
                        listInfo.setUser_id(obj.getString("user_id"));
                        Log.i("listTest", obj.getString("user_id"));
                        Log.i("listTest", obj.getString("stuff_name"));

                        //orderInfos.add(temp);
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


    //* send each item to server
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
                    jsonInput.put("user_email", MyData.mail);
                    Log.i("testing",stuff_name.getText().toString());
                    jsonInput.put("stuff_name", stuff_name.getText().toString());
                    Log.i("testing",stuff_cost.getText().toString());
                    jsonInput.put("stuff_cost", Integer.parseInt(stuff_cost.getText().toString()));
                    Log.i("testing",stuff_num.getText().toString());
                    jsonInput.put("stuff_num", Integer.parseInt(stuff_num.getText().toString()));
                    //jsonInput.put("stuff_img", stuff_img);

                    RequestBody reqBody = RequestBody.create(
                            MediaType.parse("application/json; charset=utf-8"),
                            jsonInput.toString()
                    );

                    Request request = new Request.Builder()
                            .post(reqBody)
                            .url(MyItemSend_urls + "/" + roomID + "/" + MyData.mail)
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


    @Override
    public void onClick(View v) {
        String temp = v.getTag().toString();
        int position = Integer.parseInt(temp.substring(1));

        Log.i("ehlsi", temp);

//        if(temp.contains("D")) {
//            OrderInfo temp_edit = myAdapter.getItem(position);
//            orderInfos.remove(temp_edit);
//            Log.i("ehlsi", "?????????mmmmmm");
//            myAdapter.notifyDataSetChanged();
//            stuff_name.setText(temp_edit.getStuffName());
//            stuff_cost.setText(temp_edit.getCost());
//            stuff_num.setText(temp_edit.getNum());
//            onResume();
//        } else {
//            OrderInfo temp_delete = myAdapter.getItem(position);
//            orderInfos.remove(temp_delete);
//            stuff_name.setText(temp_delete.getStuffName());
//            stuff_cost.setText(temp_delete.getCost());
//            stuff_num.setText(temp_delete.getNum());
//            myAdapter.notifyDataSetChanged();
//            Log.i("ehlsi", "eeeeeee");
//            onResume();
//        }
    }
}