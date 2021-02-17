package com.example.moa_cardview.receipt;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
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

    private StuffInfo stuffRoomInfo = new StuffInfo();

    private ImageButton ReceiptButton;
    private String roomID;

    private ArrayList<OrderInfo> orderInfos = new ArrayList<>();
    private ArrayList<OrderInfo> listInfos = new ArrayList<>();
    EditText stuff_name;
    EditText stuff_cost;
    TextView stuff_num;
    ListView listView,listView2;
    //image구현 필요.
    //String stuff_img = "http://yebinfigthing";

    TextView myOrderAddButton, othersOrderAddButton;

    private AdapterView.OnItemClickListener listener;
    private MyAdapter myAdapter;
    private MyAdapter2 myAdapter2;

    // camera
    private ImageButton stuff_camera;
    private Uri filePath;
    private ImageView imagePreview;
    private EditText image_cost;
    private RelativeLayout relativeLayoutImage;
    private LinearLayout linearLayoutWrite;
    private boolean isImage = false;
    private ListView listViewImage;
    private ArrayList<OrderInfo> imageInfos = new ArrayList<>();
    private ImageAdapter imageAdapter;
//    private
    //상수
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1001;
    private static final int MY_STORAGE_ACCESS = 101;
    private static final int CAMERA_CAPTURE = 102;
    public static final int REQUEST_CODE = 100;



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



        // camera button 클릭시, 보여지는 image view, cost info, relative layout
        imagePreview = findViewById(R.id.image_preview);
        image_cost = findViewById(R.id.order_myorderimage_price_image);
        relativeLayoutImage = findViewById(R.id.image);
        linearLayoutWrite = findViewById(R.id.write_suff_info);

        // for image list view
        listViewImage = findViewById(R.id.order_image_listview);
        imageAdapter = new ImageAdapter(this, imageInfos);
        listViewImage.setAdapter(imageAdapter);
        imageAdapter.setListView(listViewImage);

        //camera button
        stuff_camera = findViewById(R.id.order_myorder_camerabutton1);

        // camera button click
        stuff_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //이미지를 선택
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
            }
        });

        //receive 받아서 방 정보 세팅
        InfoReceiveServer();

        //주문 목록 세팅
        listView2 = (ListView)findViewById(R.id.order_othersorder_listview);
        ListReceiveServer();

        //null handling 필요...
        orderInfos.add(new OrderInfo(roomID,"hello","123","12"));

        myAdapter = new MyAdapter(this, orderInfos, stuff_name, stuff_cost, stuff_num);
        listView.setAdapter(myAdapter);
        myAdapter.setListView(listView);

        //상품추가 버튼
        myOrderAddButton = findViewById(R.id.order_myorder_addbutton);
        myOrderAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isImage){ // image
                    OrderInfo imageInfo = new OrderInfo(roomID, image_cost.getText().toString(), filePath);
                    imageInfos.add(imageInfo);
                    imageAdapter.notifyDataSetChanged();
                    //서버에 이미지 정보 보내는 함수 필요
                    //send image server
                    //image setting 부분 안보이게
                    relativeLayoutImage.setVisibility(View.INVISIBLE);
                    isImage = false;
                    //다시 상품정보 입력란 보이게
                    linearLayoutWrite.setVisibility(View.VISIBLE);
                    image_cost.setText("");
                }else {
                    OrderInfo orderInfo = new OrderInfo(roomID, stuff_name.getText().toString(), stuff_cost.getText().toString(), stuff_num.getText().toString());
                    orderInfos.add(orderInfo);
                    myAdapter.notifyDataSetChanged();
                    //서버와 받는거 부분 에러..!! 디버깅 필요.!!
                    //서버에게 보내는건 나중에 "주문서 등록"버튼 누를때, orderInfos를 for문으로 보내는게 더 나을듯해보임.
//                    MyItemSendServer();
                    //디자인 회색처리 해주세요
                    stuff_name.setText("");
                    stuff_cost.setText("");
                    stuff_num.setText("1");
                }
                // ListView 크기 조절 - write(normal) list view
                int totalHeight = 0;
                for (int i = 0; i < orderInfos.size(); i++) {
                    View listItem = myAdapter.getView(i, null, listView);
                    listItem.measure(0, 0);
                    totalHeight += listItem.getMeasuredHeight();
                    Log.i("view sizezze normal", String.valueOf(listItem.getMeasuredHeight()));  //result 342
                }
                ViewGroup.LayoutParams params = listView.getLayoutParams();
                params.height = totalHeight + (listView.getDividerHeight() * (myAdapter.getCount() - 1));
                listView.setLayoutParams(params);

                // ListView 크기 조절 - image view
                int totalHeightImage = 0;
                for (int i = 0; i < imageInfos.size(); i++) {
                    View listItem = imageAdapter.getView(i, null, listViewImage);
                    listItem.measure(0, 0);
                    totalHeightImage += listItem.getMeasuredHeight();
                    Log.i("view sizezze image", String.valueOf(listItem.getMeasuredHeight())); //result 623
                }
                ViewGroup.LayoutParams paramsImage = listViewImage.getLayoutParams();
                paramsImage.height = totalHeightImage + (listViewImage.getDividerHeight() * (imageAdapter.getCount() - 1));
                listViewImage.setLayoutParams(paramsImage);
            }
        });



        //나도 추가 버튼 (구현 아직 안됨) 추후 구현 필요
//        othersOrderAddButton = findViewById(R.id.order_othersorder_add_button1);
//        othersOrderAddButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "toast message",Toast.LENGTH_SHORT);
//
//            }
//        });

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

    // camera button
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면
        if (requestCode == 0 && resultCode == RESULT_OK) {
            filePath = data.getData();
            try {
                //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                relativeLayoutImage.setVisibility(View.VISIBLE);
                imagePreview.setImageBitmap(bitmap);
                isImage = true;
                linearLayoutWrite.setVisibility(View.INVISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

        public void ListReceiveServer(){
        class sendData extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //null handling 필요...
                //listInfos.add(new OrderInfo(roomID,"hello","123"));
                myAdapter2 = new MyAdapter2(getApplicationContext(), listInfos);
                listView2.setAdapter(myAdapter2);
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
                        OrderInfo listInfo = new OrderInfo();

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

                        listInfos.add(listInfo);
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