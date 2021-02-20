package com.example.moa_cardview.receipt;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReceiptActivity extends AppCompatActivity {

    private static final String urls = "http://54.180.8.235:5000/room";
    private static final String MyItemSend_urls = "http://54.180.8.235:5000/receipt";
    private static final String image_url = "http://54.180.8.235:5000/receipt/image";

    private StuffInfo stuffRoomInfo = new StuffInfo();

    private final int GET_GALLERY_IMAGE = 200;

    private ImageButton ReceiptButton;
    private String roomID;

    private ArrayList<OrderInfo> orderInfos = new ArrayList<>();
    private ArrayList<OrderInfo> listInfos = new ArrayList<>();
    private ArrayList<String> listname = new ArrayList<>();

    AutoCompleteTextView stuff_name;
    EditText stuff_cost;
    TextView stuff_num;
    ListView listView;

    TextView myOrderAddButton;

    private AdapterView.OnItemClickListener listener;
    private MyAdapter myAdapter;

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
                verifyStoragePermissions(ReceiptActivity.this);
                //이미지를 선택
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        //receive 받아서 방 정보 세팅
        InfoReceiveServer();

        //주문 목록 자동완성 세팅
        ListReceiveServer();

        stuff_name = (AutoCompleteTextView) findViewById(R.id.order_myorder_product1);

        // AutoCompleteTextView 에 아답터를 연결한다.
        stuff_name.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, listname ));
        //선택했을때 자동 가격 완성
        stuff_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    for(int j = 0; j < listInfos.size(); j++){
                        Log.i("list",parent.getAdapter().getItem(position).toString());
                        if(parent.getAdapter().getItem(position).toString().equals(listInfos.get(j).getStuffName())) {
                            Log.i("listing",listInfos.get(j).getStuffName());
                            Log.i("listing",listInfos.get(j).getCost());
                            stuff_cost.setText(listInfos.get(j).getCost());
                            break;
                        }
                    }
            }
        });


        myAdapter = new MyAdapter(this, orderInfos, stuff_name, stuff_cost, stuff_num);
        listView.setAdapter(myAdapter);
        myAdapter.setListView(listView);

        //상품추가 버튼
        myOrderAddButton = findViewById(R.id.order_myorder_addbutton);
        myOrderAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isImage){ // image
                    if(image_cost.getText().toString().length() != 0) {
                        OrderInfo imageInfo = new OrderInfo(roomID, image_cost.getText().toString(), filePath);
                        imageInfos.add(imageInfo);
                        imageAdapter.notifyDataSetChanged();
                        //****서버에 이미지 정보 보내는 함수 필요*****
                        //****send image server (call function)*****
//                        imageTransfer(imageInfo);
                        //****INVISIBLE 할 때, 늘어났던 레이아웃 사이즈 다시 줄이기 구현 필요****
                        relativeLayoutImage.setVisibility(View.GONE); // image setting 부분 안보이게
                        isImage = false;
                        linearLayoutWrite.setVisibility(View.VISIBLE); // 다시 상품정보 입력란 보이게
                        image_cost.setText("");
                    }else {
                        Toast.makeText(ReceiptActivity.this, "총 가격을 입력하세요.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    if(stuff_name.getText().toString().length() != 0 && stuff_cost.getText().toString().length() != 0 && Integer.parseInt(stuff_num.getText().toString()) > 0) {
                        OrderInfo orderInfo = new OrderInfo(roomID, stuff_name.getText().toString(), stuff_cost.getText().toString(), stuff_num.getText().toString());
                        orderInfos.add(orderInfo);
                        myAdapter.notifyDataSetChanged();
                        stuff_name.setText("");
                        stuff_cost.setText("");
                        stuff_num.setText("1");
                    }else {
                        Toast.makeText(ReceiptActivity.this, "상품 이름, 가격, 개수를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                    }
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

        //* create room
        ReceiptButton = findViewById(R.id.createroom_createbutton);
        ReceiptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyImageSendServer();
                MyItemSendServer();
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

    //* galary button
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면
//        if (requestCode == 0 && resultCode == RESULT_OK) {
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
            Log.i("file", filePath.toString());
            try {
                //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                relativeLayoutImage.setVisibility(View.VISIBLE); //image setting 부분 보이게
                imagePreview.setImageBitmap(bitmap);
                isImage = true;
                linearLayoutWrite.setVisibility(View.GONE); //  상품정보 입력 안보이게
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //* setting the screen
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

    //* receive screen info
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

                    Request request = new Request.Builder()
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

    //* receive order list registered
    public void ListReceiveServer(){
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
                    Log.i("listTest", "list server start");
                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .get()
                            .url(MyItemSend_urls + "/" + roomID)
                            .build();

                    Response responses = null;
                    responses = client.newCall(request).execute();

                    JSONObject jObject = new JSONObject(responses.body().string());
                    JSONArray jArray = jObject.getJSONArray("receipts");

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject obj = jArray.getJSONObject(i);
                        OrderInfo listInfo = new OrderInfo();
                        listInfo.setCost(obj.getString("stuff_cost"));
                        listInfo.setStuffName(obj.getString("stuff_name"));
                        listname.add(obj.getString("stuff_name"));
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

                    JSONArray jArray = new JSONArray();
                    for(int i=0; i<orderInfos.size(); i++) {
                        JSONObject jsonInput = new JSONObject();
                        jsonInput.put("room_id", roomID);
                        jsonInput.put("user_email", MyData.mail);
                        jsonInput.put("stuff_name", orderInfos.get(i).getStuffName());
                        jsonInput.put("stuff_cost", Integer.parseInt(orderInfos.get(i).getCost()));
                        jsonInput.put("stuff_num", Integer.parseInt(orderInfos.get(i).getNum()));
                        jsonInput.put("stuff_img", "none");
                        jArray.put(jsonInput);
                    }
                    JSONObject jsonInputs = new JSONObject();
                    jsonInputs.put("data", jArray);

                    RequestBody reqBody = RequestBody.create(
                            MediaType.parse("application/json; charset=utf-8"),
                            jsonInputs.toString()
                    );

                    Request request = new Request.Builder()
                            .post(reqBody)
                            .url(MyItemSend_urls + "/" + roomID)
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

    //* send each image info to server
    public void MyImageSendServer(){
        for(int i=0; i<imageInfos.size(); i++){
            imageTransfer(imageInfos.get(i));
        }
    }
    public void imageTransfer(OrderInfo imageInfo){
        File imageFile = new File(uriToFilePath(imageInfo.getFilePath()));
        sendImageToServer(imageFile, imageInfo.getCost());
    }

    // 출처: https://stackoverflow.com/questions/20322528/uploading-images-to-server-android
    public String uriToFilePath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String imagePath = cursor.getString(column_index);
        Log.i("Data", imagePath);
        return imagePath;
    }

    public void sendImageToServer(File imamgeFile, String cost) {
        try{
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file",imamgeFile.getName(), RequestBody.create(MultipartBody.FORM,imamgeFile))
                    .build();

            String tempURL = image_url + "/" + roomID + "/" + MyData.getMail() + "/" + cost;

            Request request = new Request.Builder()
                    .url(tempURL)
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i("Data","fail: " + e.getMessage());
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String server_response = response.body().string();
                    Log.i("Data","Response: " + server_response);
                }
            });
        }catch (Exception e){
            Log.i("androidTest","okhttp3 request exception: "+e.getMessage());
        }
    }

    //출처: https://stackoverflow.com/questions/8854359/exception-open-failed-eacces-permission-denied-on-android?page=1&tab=votes#tab-top
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}