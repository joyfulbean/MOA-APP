package com.hgu.moa.receipt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hgu.moa.R;
import com.hgu.moa.chat.ChattingActivity;
import com.hgu.moa.data.MyData;
import com.hgu.moa.data.OrderInfo;
import com.hgu.moa.data.ServerInfo;
import com.hgu.moa.data.StuffInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReceiptActivity extends AppCompatActivity {

    private static final String urls = ServerInfo.getUrl() + "room";
    private static final String MyItemSend_urls = ServerInfo.getUrl() + "receipt";
    private static final String image_url = ServerInfo.getUrl() + "receipt/image";

    private StuffInfo stuffRoomInfo = new StuffInfo();

    private final int GET_GALLERY_IMAGE = 200;

    private ImageButton ReceiptButton;
    private ImageButton backButton;
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

    // for keypad
    private InputMethodManager imm;
    private ImageButton imageDeleteButton;

    // add - 2021.04.07
    private ImageButton writeButton;
    private ImageButton imageButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        // add - 2021.04.07
        final Drawable drawableWriteDark = getResources().getDrawable(R.drawable.write_darkgray_btn);
        final Drawable drawableWriteLight = getResources().getDrawable(R.drawable.write_lightgray_btn);

        final Drawable drawableImageeDark = getResources().getDrawable(R.drawable.photo_darkgray_btn);
        final Drawable drawableImageLight = getResources().getDrawable(R.drawable.photo_lightgray_btn);

        final Drawable drawDeleteImage = getResources().getDrawable(R.drawable.camerabuttongrey);

        writeButton = findViewById(R.id.write_darkgray_btn);
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayoutImage.setVisibility(View.GONE); // image setting ?????? ????????????
                isImage = false;
                linearLayoutWrite.setVisibility(View.VISIBLE); // ?????? ???????????? ????????? ?????????
                writeButton.setImageDrawable(drawableWriteDark);
                imageButton.setImageDrawable(drawableImageLight);
            }
        });
        imageButton = findViewById(R.id.photo_lightgray_btn);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePreview.setImageDrawable(drawDeleteImage);
                linearLayoutWrite.setVisibility(View.GONE); // ?????? ???????????? ????????? ?????????
                isImage = true;
                relativeLayoutImage.setVisibility(View.VISIBLE); // image setting ?????? ????????????
                writeButton.setImageDrawable(drawableWriteLight);
                imageButton.setImageDrawable(drawableImageeDark);
            }
        });
        //????????? ??????
        imageDeleteButton = findViewById(R.id.order_myorderimage_beforeadd_closebutton);
        imageDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePreview.setImageDrawable(drawDeleteImage);
            }
        });

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
        // ????????? ?????????????????? ????????? ????????? ????????????.
//        CustomDialog customDialog = new CustomDialog(ReceiptActivity.this);
//        customDialog.callFunction();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        stuff_cost = findViewById(R.id.order_myorder_price1);
        stuff_num = findViewById(R.id.order_myorder_count1);
        listView = (ListView)findViewById(R.id.order_myorderothers_listview);

        // camera button ?????????, ???????????? image view, cost info, relative layout
        imagePreview = findViewById(R.id.image_preview);
        image_cost = findViewById(R.id.order_myorderimage_price_image);
        relativeLayoutImage = findViewById(R.id.image);
        linearLayoutWrite = findViewById(R.id.write_suff_info);

        // for image list view
        listViewImage = findViewById(R.id.order_image_listview);
        imageAdapter = new ImageAdapter(this, imageInfos);
        listViewImage.setAdapter(imageAdapter);
        imageAdapter.setListView(listViewImage);


        // camera button click
        imagePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyStoragePermissions(ReceiptActivity.this);
                //???????????? ??????
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        //receive ????????? ??? ?????? ??????
        InfoReceiveServer();

        //?????? ?????? ???????????? ??????
        ListReceiveServer();

        stuff_name = (AutoCompleteTextView) findViewById(R.id.order_myorder_product1);

        // AutoCompleteTextView ??? ???????????? ????????????.
        stuff_name.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, listname ));
        //??????????????? ?????? ?????? ??????
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



        //???????????? ??????
        myOrderAddButton = findViewById(R.id.order_myorder_addbutton);
        myOrderAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isImage){ // image
                    if(image_cost.getText().toString().length() != 0 && imagePreview.getDrawable() != drawDeleteImage) {
                        OrderInfo imageInfo = new OrderInfo(roomID, image_cost.getText().toString(), filePath);
                        imageInfos.add(imageInfo);
                        imageAdapter.notifyDataSetChanged();
                        //****????????? ????????? ?????? ????????? ?????? ??????*****
                        //****send image server (call function)*****
//                        imageTransfer(imageInfo);
                        //****INVISIBLE ??? ???, ???????????? ???????????? ????????? ?????? ????????? ?????? ??????****
                        relativeLayoutImage.setVisibility(View.GONE); // image setting ?????? ????????????
                        isImage = false;
                        linearLayoutWrite.setVisibility(View.VISIBLE); // ?????? ???????????? ????????? ?????????
                        image_cost.setText("");
                    }else {
                        Toast.makeText(ReceiptActivity.this, "?????? ?????? ?????? ??? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ReceiptActivity.this, "?????? ??????, ??????, ????????? ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
                    }
                }
                // ListView ?????? ?????? - write(normal) list view
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

                // ListView ?????? ?????? - image view
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

                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
                intent.putExtra("isNew",false);
                startActivity(intent);
                finish();
            }
        });

        //* back page
        backButton = findViewById(R.id.order_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //?????? plus mius ?????? ?????????
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
        //request????????? 0?????? OK??? ???????????? data??? ????????? ?????? ?????????
//        if (requestCode == 0 && resultCode == RESULT_OK) {
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
            Log.i("file", filePath.toString());
            try {
                //Uri ????????? Bitmap?????? ???????????? ImageView??? ?????? ?????????.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                relativeLayoutImage.setVisibility(View.VISIBLE); //image setting ?????? ?????????
                imagePreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //* setting the screen
    /*public void settingScreen(){
        // for recipe page
        TextView chatpage_title_textview = (TextView)findViewById(R.id.order_product);
        chatpage_title_textview.setText(stuffRoomInfo.getTitle());

        TextView chatpage_date_textview = (TextView)findViewById(R.id.order_date);
        chatpage_date_textview.setText(stuffRoomInfo.getOrderDate());

        TextView chatpage_time_textview = (TextView)findViewById(R.id.order_time);
        chatpage_time_textview.setText(stuffRoomInfo.getOrderTime());

        TextView chatpage_place_textview = (TextView)findViewById(R.id.order_place);
        chatpage_place_textview.setText(stuffRoomInfo.getPlace());
    }*/

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
//                settingScreen();
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

                    //json array??? ????????? ????????? thing??? ???????????????.
                    // ?????? ??? JSONObject??? ???????????????.
                    JSONObject obj = new JSONObject(responses.body().string());
                    stuffRoomInfo.setTitle(obj.getString("title"));
                    stuffRoomInfo.setOrderDate(obj.getString("order_date"));
                    stuffRoomInfo.setOrderTime(obj.getString("order_time"));
                    stuffRoomInfo.setPlace(obj.getString("place"));
                    stuffRoomInfo.setNumUsers(obj.getInt("num_user"));
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

    // ??????: https://stackoverflow.com/questions/20322528/uploading-images-to-server-android
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

    //??????: https://stackoverflow.com/questions/8854359/exception-open-failed-eacces-permission-denied-on-android?page=1&tab=votes#tab-top
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