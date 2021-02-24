package com.handong.moa.chat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.handong.moa.R;
import com.handong.moa.data.MyData;
import com.handong.moa.data.OrderInfo;
import com.handong.moa.receipt.ImageAdapter;
import com.handong.moa.receipt.MyAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditOrderListActivity extends AppCompatActivity {
    private static final String postUrl = "http://54.180.8.235:5000/static/receipts/";
    private static final String MyItemSend_urls = "http://54.180.8.235:5000/receipt";
    private static final String image_url = "http://54.180.8.235:5000/receipt/image";

    private ArrayList<OrderInfo> eachOrderInfos;
    private ArrayList<OrderInfo> imgEachOrderInfos = new ArrayList<>();
    private ArrayList<String> imgUrls;
    private ArrayList<Integer> imgCost;
    private ArrayList<OrderInfo> insertImgOrderInfos = new ArrayList<>();

    private TextView name;
    private TextView cost;
    private TextView num;
    private TextView imageCost;
    private TextView myOrderAddButton;
    private ImageButton editFinishButton;
    private ImageButton backButton;
    private ImageButton cameraButton;
    private ImageView imagePreview;
    private final int GET_GALLERY_IMAGE = 200;

    private String roomId;
    private boolean isImage = false;
    private Uri filePath;

    private ListView listView;
    private LinearLayout listLayout;
    private MyAdapter listAdapter;

    private ListView insertImageListView;
    private RelativeLayout insertImageLayout;
    private ImageAdapter insertImageAdapter;

    private ListView imageListView;
    private EditImageAdapter bitmapListAdapter;
    private ArrayList<Bitmap> imageBitmap = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order_list);

        Intent intent = getIntent();
        eachOrderInfos = (ArrayList<OrderInfo>) intent.getSerializableExtra("each_orderInfos");
        imgCost = intent.getIntegerArrayListExtra("image_cost");
        imgUrls = intent.getStringArrayListExtra("image_url");
        for(int i=0; i<imgUrls.size(); i++){
            OrderInfo temp = new OrderInfo();
            temp.setFilePath(Uri.parse(imgUrls.get(i)));
            temp.setCost(Integer.toString(imgCost.get(i)));
            imgEachOrderInfos.add(temp);
        }
//        imgEachOrderInfos = (ArrayList<OrderInfo>) intent.getSerializableExtra("imgEachOrderInfos");
        roomId = intent.getStringExtra("room_id");

        //* back button
        backButton = findViewById(R.id.receipt_edit_backbutton);
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //* for write info
        name = findViewById(R.id.receipt_edit_product_name);
        cost = findViewById(R.id.receipt_edit_price_edittext);
        num = findViewById(R.id.receipt_edit_count);

        //* for write view
        listLayout = findViewById(R.id.receipt_edit_info_layout);
        listView = findViewById(R.id.edit_receipt_edit_info_listview);
        listAdapter = new MyAdapter(EditOrderListActivity.this, eachOrderInfos, name, cost, num);
        listView.setAdapter(listAdapter);
        listAdapter.setListView(listView);

        //* for image info
        imagePreview = findViewById(R.id.image_preview);
        imageCost = findViewById(R.id.order_myorderimage_price_image);

        //* for insert image view
        insertImageLayout = findViewById(R.id.image);
        insertImageListView = findViewById(R.id.edit_receipt_edit_image_listview_insert);
        insertImageAdapter = new ImageAdapter(this, insertImgOrderInfos);
        insertImageListView.setAdapter(insertImageAdapter);
        insertImageAdapter.setListView(insertImageListView);

        //* for image view
        imageListView = findViewById(R.id.edit_receipt_edit_image_listview);
        bitmapListAdapter = new EditImageAdapter(this, imgEachOrderInfos);
        bitmapListAdapter.setListView(imageListView);
        bitmapListAdapter.setImgBitmap(imageBitmap);
        imageListView.setAdapter(bitmapListAdapter);

        for(int i = 0; i < imgEachOrderInfos.size(); i++){
            EditLoadImageTask imageTask = new EditLoadImageTask(postUrl + roomId + File.separator + imgEachOrderInfos.get(i).getFilePath());
            imageTask.execute();
        }
        //* camera button
        cameraButton = findViewById(R.id.receipt_edit_camerabutton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyStoragePermissions(EditOrderListActivity.this);
                //이미지를 선택
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        // ListView 크기 조절 - write(normal) list view
        int totalHeight = 0;
        for (int i = 0; i < eachOrderInfos.size(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
            Log.i("view size normal", String.valueOf(listItem.getMeasuredHeight()));  //result 342
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);


        //* add order info
        myOrderAddButton = findViewById(R.id.receipt_edit_addbutton);
        myOrderAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isImage){ // image
                    if(imageCost.getText().toString().length() != 0) {
                        OrderInfo imageInfo = new OrderInfo(roomId, imageCost.getText().toString(), filePath);
                        insertImgOrderInfos.add(imageInfo);
                        insertImageAdapter.notifyDataSetChanged();
                        insertImageLayout.setVisibility(View.GONE); // image setting 부분 안보이게
                        isImage = false;
                        listLayout.setVisibility(View.VISIBLE); // 다시 상품정보 입력란 보이게
                        imageCost.setText("");
                    }else {
                        Toast.makeText(EditOrderListActivity.this, "총 가격을 입력하세요.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    if(name.getText().toString().length() != 0 && cost.getText().toString().length() != 0 && Integer.parseInt(num.getText().toString()) > 0) {
                        OrderInfo orderInfo = new OrderInfo(roomId, name.getText().toString(), cost.getText().toString(), num.getText().toString());
                        eachOrderInfos.add(orderInfo);
                        listAdapter.notifyDataSetChanged();
                        name.setText("");
                        cost.setText("");
                        num.setText("1");
                    }else {
                        Toast.makeText(EditOrderListActivity.this, "상품 이름, 가격, 개수를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
                // ListView 크기 조절 - write(normal) list view
                int totalHeight = 0;
                for (int i = 0; i < eachOrderInfos.size(); i++) {
                    View listItem = listAdapter.getView(i, null, listView);
                    listItem.measure(0, 0);
                    totalHeight += listItem.getMeasuredHeight();
                    Log.i("view size normal", String.valueOf(listItem.getMeasuredHeight()));  //result 342
                }
                ViewGroup.LayoutParams params = listView.getLayoutParams();
                params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
                listView.setLayoutParams(params);

                // ListView 크기 조절 - image view
                int totalHeightImage = 0;
                for (int i = 0; i < insertImgOrderInfos.size(); i++) {
                    View listItem = insertImageAdapter.getView(i, null, insertImageListView);
                    listItem.measure(0, 0);
                    totalHeightImage += listItem.getMeasuredHeight();
                    Log.i("view sizezze image", String.valueOf(listItem.getMeasuredHeight())); //result 623
                }
                ViewGroup.LayoutParams paramsImage = insertImageListView.getLayoutParams();
                paramsImage.height = totalHeightImage + (insertImageListView.getDividerHeight() * (insertImageAdapter.getCount() - 1));
                insertImageListView.setLayoutParams(paramsImage);
            }
        });

        //숫자 plus mius 버튼 활성화
        ImageButton minus = findViewById(R.id.receipt_edit_minusbutton);
        ImageButton plus = findViewById(R.id.order_plusbutton1);

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int total_num = Integer.parseInt(num.getText().toString()) - 1;
                num.setText(Integer.toString(total_num));
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int total_num = Integer.parseInt(num.getText().toString()) + 1;
                num.setText(Integer.toString(total_num));
            }
        });

        editFinishButton = findViewById(R.id.receipt_edit_finishbutton);
        editFinishButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                updateWriteInfoServer();
                deleteImageInfoServer();
                insertImageInfoServer();
                finish();
            }
        });
    }

    //* camera button
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
            Log.i("file", filePath.toString());
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                insertImageLayout.setVisibility(View.VISIBLE); //image setting 부분 보이게
                imagePreview.setImageBitmap(bitmap);
                isImage = true;
                listLayout.setVisibility(View.GONE); //  상품정보 입력 안보이게
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //* for show the registered image
    public class EditLoadImageTask extends AsyncTask<Bitmap, Void, Bitmap> {
        private String url;

        public EditLoadImageTask(String url) {
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
            bitmapListAdapter.notifyDataSetChanged();

            // ListView 크기 조절 - image view
            int totalHeightImage = 0;
            for (int i = 0; i < imageBitmap.size(); i++) {
                View listItem = bitmapListAdapter.getView(i, null, imageListView);
                listItem.measure(0, 0);
                totalHeightImage += listItem.getMeasuredHeight();
                Log.i("view sizezze image", String.valueOf(listItem.getMeasuredHeight())); //result 623
            }
            ViewGroup.LayoutParams paramsImage = imageListView.getLayoutParams();
            paramsImage.height = totalHeightImage + (imageListView.getDividerHeight() * (bitmapListAdapter.getCount() - 1));
            imageListView.setLayoutParams(paramsImage);
        }
    }

    //* for update write info
    public void updateWriteInfoServer(){
        class sendData extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    //기존 정보 다 삭제해버리기!

                    OkHttpClient client = new OkHttpClient();

                    JSONArray jArray = new JSONArray();
                    for(int i=0; i<eachOrderInfos.size(); i++) {
                        JSONObject jsonInput = new JSONObject();
                        jsonInput.put("room_id", roomId);
                        jsonInput.put("user_email", MyData.mail);
                        jsonInput.put("stuff_name", eachOrderInfos.get(i).getStuffName());
                        jsonInput.put("stuff_cost", Integer.parseInt(eachOrderInfos.get(i).getCost()));
                        jsonInput.put("stuff_num", Integer.parseInt(eachOrderInfos.get(i).getNum()));
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
                            .put(reqBody)
                            .url(MyItemSend_urls + "/" + roomId)
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

    //* for delete image info
    public void deleteImageInfoServer(){
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
                    ArrayList<OrderInfo> deleteImageInfo = bitmapListAdapter.getDeleteImageInfo();

                    for(int i=0; i<deleteImageInfo.size(); i++) {
                        OkHttpClient client = new OkHttpClient();

                        JSONObject jsonInput = new JSONObject();
//                        jsonInput.put("room_id", roomId);
//                        jsonInput.put("user_email", MyData.mail);
                        jsonInput.put("filename", deleteImageInfo.get(i).getFilePath().toString());

                        RequestBody reqBody = RequestBody.create(
                                MediaType.parse("application/json; charset=utf-8"),
                                jsonInput.toString()
                        );

                        Request request = new Request.Builder()
                                .delete(reqBody)
                                .url(image_url + File.separator + roomId)
                                .build();

                        Response responses = null;
                        responses = client.newCall(request).execute();
                        responses.close();
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

    //* send new image info to server
    public void insertImageInfoServer(){
        for(int i=0; i<insertImgOrderInfos.size(); i++){
            imageTransfer(insertImgOrderInfos.get(i));
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

            String tempURL = image_url + "/" + roomId + "/" + MyData.getMail() + "/" + cost;

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

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

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