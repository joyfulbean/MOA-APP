package com.example.moa_cardview.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.moa_cardview.main.MainActivity;
import com.example.moa_cardview.data.MyData;
import com.example.moa_cardview.R;
import com.example.moa_cardview.data.RoomMemberData;
import com.example.moa_cardview.data.StuffInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ChattingActivity extends AppCompatActivity {
    // for server
    private static final String urls = "http://54.180.8.235:5000/room";
    private static final String roomMemberUrls = "http://54.180.8.235:5000/participant";
    private StuffInfo chattingInfo = new StuffInfo();
    private String roomID;

    // for displaying chat page info
    private LinearLayout expandLayout;
    private RelativeLayout previewLayout; //미리보기 링크
    private String linkUrl;
    private ImageButton arrowButton, popupCloseButton;
    private ImageView previewImage;
    private RelativeLayout wholereceiptButton;
    private Dialog epicDialog;

    // for displaying the message box list
    private EditText messageContent;
    private ImageButton messageSendButton;
    private ListView messageList;
    private ArrayList<ChatMessageItem> messageItems = new ArrayList<>();
    private ChatAdapter messageAdapter;
    private FirebaseDatabase firebaseDatabase;                           //Firebase Database 관리 객체참조변수
    private DatabaseReference roodIdReference;                           //'chat'노드의 참조객체 참조변수

    // for displaying the people list
    private DrawerLayout drawerLayout;
    private View drawerView;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private List<RoomMemberData> roomMembers =  new ArrayList<>();;

    private Toolbar toolbar;
    private ImageButton backButton;
    private ImageButton exitRoom;

    // checking the new Member
    private String isNew = "0";

    // for displaying plus option
    private ConstraintLayout expandLayoutPlus;
    private ImageButton plusButton;
    private ImageButton cameraButton;
    private ImageButton galleryButton;

    //업로드할 이미지 파일의 경로 Uri
    private Uri imgUri;

    //상수
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1001;
    private static final int MY_STORAGE_ACCESS = 101;
    private static final int CAMERA_CAPTURE = 102;
    public static final int REQUEST_CODE = 100;

    public ChattingActivity() { }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        //get the room id
        Intent secondIntent = getIntent();
        String message = secondIntent.getStringExtra("room_id");
        if(message == null) {
            roomID = "1";
        }
        else{
            roomID = message;
        }
        setMyProfile();     // 대화 참여자 페이지에서 내 프로필 설정
        receiveServer();    // chatting room 정보 가져 오기


        //whole receipt popup
        epicDialog = new Dialog(this);

        wholereceiptButton = findViewById(R.id.chatpage_wholereceipt_button);

        wholereceiptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Popup", "Clicked");
                View dlgview = View.inflate(ChattingActivity.this, R.layout.whole_receipt_popup, null);
                AlertDialog.Builder dialog = new AlertDialog.Builder(ChattingActivity.this)
                    .setView(dlgview);
                dialog.show();

//                popupCloseButton = (ImageButton) dlgview.findViewById(R.id.wholereceipt_popup_closebutton);
//                popupCloseButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        dialog.this.dismiss();
//                    }
//                });
            }
        });

        //* plus option functions
        expandLayoutPlus = findViewById(R.id.expandable_layout_plusoption);
        plusButton = findViewById(R.id.chatpage_plus_button);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expandLayoutPlus.getVisibility()==View.GONE) {
                    expandLayoutPlus.setVisibility(View.VISIBLE);
                } else {
                    expandLayoutPlus.setVisibility(View.GONE);
                }
            }
        });

        //camera
        cameraButton = findViewById(R.id.chatpage_camerabutton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_CAPTURE);
            }
        });
        //camera permission check
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        //아직 부여받지 않았으므로 요청
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
            }
            //퍼미션 부여 받음
            else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }

        //gallery
        galleryButton = findViewById(R.id.chatpage_photobutton);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,MY_STORAGE_ACCESS);
            }
        });

        previewImage = findViewById(R.id.post_preview_iv);


        //* post bar, chat page information
        expandLayout = findViewById(R.id.chatpage_expandable_layout);
        arrowButton = findViewById(R.id.chatpage_arrow_button);
        arrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expandLayout.getVisibility()==View.GONE) {
                    expandLayout.setVisibility(View.VISIBLE);
                    arrowButton.setImageResource(R.drawable.upbutton);
                    //Picasso.get().load(chattingInfo.getImageUrl()).into(previewImage);
                } else {
                    expandLayout.setVisibility(View.GONE);
                    arrowButton.setImageResource(R.drawable.downbutton);
                }
            }
        });

//        //* Link 미리보기 누르면 Web으로 연결
//        previewLayout = findViewById(R.id.chatpage_postpreview_layout);
//        previewLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openWeb(getApplicationContext(), linkUrl);
//            }
//        });

        //* firebase setting (Firebase DB관리 객체와 'roomID'노드 참조객체 얻어오기)
        if (FirebaseApp.getApps(this).size() == 0){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        firebaseDatabase = FirebaseDatabase.getInstance();
        roodIdReference = firebaseDatabase.getReference(roomID);

        //* send message
        messageContent = findViewById(R.id.chatpage_typein_edittext);
        messageSendButton = findViewById(R.id.chatpage_sendbutton);
        messageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = MyData.name;
                String content = messageContent.getText().toString();
                sendMessageFirebase(name, content, "none");

                //flush EditText
                messageContent.setText("");

                //hide soft keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
            }
        });

        //* show message list
        messageList = findViewById(R.id.chatpage_message_listview);
        messageAdapter = new ChatAdapter(messageItems,getLayoutInflater(),getApplicationContext());
        messageList.setAdapter(messageAdapter);
        messageList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int i, long id) { // view:클릭한 뷰 position: id:position이랑 일반적으로 같다
                final int position = i;
                messageAdapter.copy(position);
                return false; //true하면 일반클릭과 롱클릭 둘다 먹고 false하면 롱클릭만 먹는다
            }
        });


        roodIdReference.keepSynced(true);
        roodIdReference.addChildEventListener(new ChildEventListener() {
            //새로 추가된 것만 줌 ValueListener는 하나의 값만 바뀌어도 처음부터 다시 값을 줌
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //새로 추가된 데이터(값 : MessageItem 객체) 가져오기
                ChatMessageItem messageItem = dataSnapshot.getValue(ChatMessageItem.class);

                //새로운 메세지를 리스뷰에 추가하기 위해 ArrayList에 추가
                messageItems.add(messageItem);

                //리스트뷰를 갱신
                messageAdapter.notifyDataSetChanged();
                messageList.setSelection(messageItems.size()-1); //리스트뷰의 마지막 위치로 스크롤 위치 이동
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        messageContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length()<1){
                    messageSendButton.setVisibility(View.GONE);
                }else messageSendButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });;

        //* show people list - Drawer Layout Open and Close with buttons
        drawerList = findViewById(R.id.chatpage_ppl_listview);                      //Drawer View에서 방 참가자 리스트
        drawerLayout = (DrawerLayout)findViewById(R.id.chatpage_drawerlayout);
        drawerView = (View)findViewById(R.id.chatpage_drawer);

        ImageButton peopleListButton = (ImageButton)findViewById(R.id.chatpage_drawer_button);
        peopleListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roomMemberReceiveServer();
                drawerLayout.openDrawer(drawerView);
            }
        });

        ImageButton closeButton = (ImageButton)findViewById(R.id.chatpage_drawer_closebutton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawers();
            }
        });

        //* hide tool bar State Title
        toolbar = (Toolbar) findViewById(R.id.chatpage_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //* back page
        backButton = findViewById(R.id.chatpage_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(ChattingActivity.this, MainActivity.class);
                startActivity(intent2);
                finish();
            }
        });

        //* exit room
        exitRoom = findViewById(R.id.chatpage_exitroom);
        exitRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendServer();
            }
        });

    }

    //send message on firebase
    private void sendMessageFirebase(String name, String content, String image){
        Calendar calendar = Calendar.getInstance(); //현재 시간을 가지고 있는 객체
        String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);

        ChatMessageItem messageItem = new ChatMessageItem(name, content, time, image);

        firebaseDatabase = FirebaseDatabase.getInstance();
        roodIdReference = firebaseDatabase.getReference(roomID);
        roodIdReference.push().setValue(messageItem);
    }

    //for camera
    private Bitmap imgRotate(Bitmap bmp){
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Bitmap resizedBitmap = Bitmap.createBitmap(bmp,0,0,width,height,matrix,true);
        bmp.recycle();

        return resizedBitmap;
    }

    //for camera and gallery
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MY_STORAGE_ACCESS){
            if (resultCode == RESULT_OK) {
                try {
                    //선택한 이미지의 경로 얻어오기
                    imgUri= data.getData();
                    Glide.with(this).load(imgUri).into(previewImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else if(requestCode == CAMERA_CAPTURE){
            //찍은 사진 가져와서 붙여주기
            if(resultCode == RESULT_OK && data.hasExtra("data")){
                try{
                    //촬영한 이미지 가져오기
                    Bitmap img = (Bitmap) data.getExtras().get("data");
                    img = imgRotate(img);
                    imgUri= data.getData();
                    clickUpload();
                    Glide.with(this).load(imgUri).into(previewImage);

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    //upload image on firebase
    public void clickUpload() {
        //firebase storage에 업로드하기
        //1. FirebaseStorage을 관리하는 객체 얻어오기
        FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();

        //2. 업로드할 파일의 node를 참조하는 객체
        //파일 명이 중복되지 않도록 날짜를 이용
        SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMddhhmmss");
        String filename= sdf.format(new Date())+ ".png";//현재 시간으로 파일명 지정 20191023142634
        //원래 확장자는 파일의 실제 확장자를 얻어와서 사용해야함. 그러려면 이미지의 절대 주소를 구해야함.

        StorageReference imgRef = firebaseStorage.getReference(roomID + "/" + filename);
        //uploads라는 폴더가 없으면 자동 생성

        //참조 객체를 통해 이미지 파일 업로드
//        imgRef.putFile(imgUri);
        //업로드한 파일의 경로를 firebaseDB에 저장하면 게시판 같은 앱도 구현할 수 있음.
        UploadTask uploadTask =imgRef.putFile(imgUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ChattingActivity.this, "업로드", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //for camera permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Camera Permission is approved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Camera Permission is disapproved ", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    //* when set my profile
    private void setMyProfile(){
        ImageView myImage = findViewById(R.id.chatpage_myprofile_iv);
        TextView myName = findViewById(R.id.chatpage_myname_tv);

        myImage.setImageResource(R.drawable.profileicon2);
        myName.setText(MyData.getName());
    }

    //* when set chat page information
    public void settingScreen(){
        TextView chatpage_title_textview = (TextView)findViewById(R.id.chatpage_title_textview);
        chatpage_title_textview.setText(chattingInfo.getTitle());

        TextView chatpage_date_textview = (TextView)findViewById(R.id.chatpage_date_textview);
        chatpage_date_textview.setText(chattingInfo.getOrderDate());

        TextView chatpage_time_textview = (TextView)findViewById(R.id.chatpage_time_textview);
        chatpage_time_textview.setText(chattingInfo.getOrderTime());

        TextView chatpage_place_textview = (TextView)findViewById(R.id.chatpage_place_textview);
        chatpage_place_textview.setText(chattingInfo.getPlace());

        TextView chatpage_name_textview = findViewById(R.id.chatpage_name_textview);
        chatpage_name_textview.setText(chattingInfo.getCreatorName());

//        TextView chatpage_pplnumber_textview = findViewById(R.id.chatpage_pplnumber_textview);
//        chatpage_pplnumber_textview.setText(chattingInfo.getNumUsers());

//        TextView ogTitle = findViewById(R.id.chatpage_postpreview_tv1);
//        ogTitle.setText(chattingInfo.getOgTitle());
//
//        TextView ogContent = findViewById(R.id.chatpage_postpreview_tv2);
//        ogContent.setText(chattingInfo.getStuffLink());

        linkUrl = chattingInfo.getStuffLink();
    }

    //* when show people list
    class MyAdapter extends ArrayAdapter<RoomMemberData> {

        Context context;
        List<RoomMemberData> rRoomMembers = new ArrayList<>();

        MyAdapter (Context c, List<RoomMemberData> roomMembers) {
            super(c, R.layout.chatpage_ppl_row, R.id.chatpage_pplname_tv, roomMembers);
            this.context = c;
            this.rRoomMembers = roomMembers;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.chatpage_ppl_row, parent, false);
            ImageView images = row.findViewById(R.id.chatpage_otherprofile_iv);
            TextView name = row.findViewById(R.id.chatpage_pplname_tv);
            ImageButton phone = row.findViewById(R.id.chatpage_phone_button);

            //setting resources on views
            images.setImageResource( R.drawable.profileicon2);
            name.setText(rRoomMembers.get(position).getName());

            if(rRoomMembers.get(position).getPhonNumber().equals("null")) {
                Log.i("phone", "null input");
                phone.setVisibility(View.GONE);
            }else {
                Log.i("phone", rRoomMembers.get(position).getPhonNumber());
            }

            phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callFunction(rRoomMembers.get(position).getPhonNumber());
                }
            });
            return row;
        }
    }

    //* open web
    public void openWeb(Context context, String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;

        // Parse the URI and create the intent.
        Uri webPage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webPage);

        // Find an activity to hand the intent and start that activity.
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));
        } else {
            Toast.makeText(context, "Wrong Address. Try again.", Toast.LENGTH_LONG).show();
            Log.d("ImplicitIntents", "Can't handle this intent!");
        }
    }

    //* for call
    public void callFunction(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:/" + phoneNum));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Wrong Number. Try again.", Toast.LENGTH_LONG).show();
            Log.d("ImplicitIntents", "Can't handle this intent!");
        }
    }

    //* receive room member info
    public void roomMemberReceiveServer(){
        class sendData extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                roomMembers.clear();
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                MyAdapter adapter = new MyAdapter(ChattingActivity.this, roomMembers);
                drawerList.setAdapter(adapter);
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
                    Log.i("menuMem", "started");
                    OkHttpClient client = new OkHttpClient();

//                    JSONObject jsonInput = new JSONObject();
//                    jsonInput.put("room_id", roomID);
//                    jsonInput.put("me", MyData.mail);
//
//                    RequestBody reqBody = RequestBody.create(
//                            MediaType.parse("application/json; charset=utf-8"),
//                            jsonInput.toString()
//                    );

                    Request request = new Request.Builder()
//                            .post(reqBody)
                            .url(roomMemberUrls + File.separator + roomID)
                            .build();

                    Response responses = null;
                    responses = client.newCall(request).execute();

                    JSONObject jObject = new JSONObject(responses.body().string());
                    JSONArray jArray = jObject.getJSONArray("data");

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject obj = jArray.getJSONObject(i);
                        RoomMemberData temp = new RoomMemberData();
                        temp.setName(obj.getString("name"));
                        temp.setMail(obj.getString("email"));
                        temp.setPhonNumber(obj.getString("phone"));
                        temp.setPhotoUrl(obj.getString("photo_url"));

                        Log.i("menuMem", obj.getString("name"));
                        roomMembers.add(temp);
                    }
                    Log.i("menuMem", "leave for");

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

    //* user enter room and receive chatting info
    public void receiveServer(){
        class sendData extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                settingScreen();
                // 아니면 여기서 추가를해줘도 될 듯 하네
                if(isNew.equals("1")) {
                    String content = MyData.name + "님이 입장 했습니다.";
                    sendMessageFirebase("ENTER_EXIT", content, "none");
                    isNew = "0";
                }
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
                    chattingInfo.setTitle(obj.getString("title"));
//                    chattingInfo.setOrderDate(obj.getString("order_date"));
//                    chattingInfo.setOrderTime(obj.getString("order_time"));
                    chattingInfo.setPlace(obj.getString("place"));
                    chattingInfo.setNumUsers(obj.getString("num_user"));
//                    chattingInfo.setStuffCost(obj.getString("stuff_cost")+"원");
//                    chattingInfo.setStuffLink(obj.getString("stuff_link"));
//                    chattingInfo.setCreatorName(obj.getString("creator_name"));
//                    chattingInfo.setImageUrl(obj.getString("image_url"));
//                    chattingInfo.setOgTitle(obj.getString("og_title"));
//                    isNew = obj.getString("is_new");
//                    Log.i("db22", chattingInfo.getOgTitle());
                    if(isNew.equals("1")){
                        Log.i("isNew", "true");
                    }
                    else {
                        Log.i("isNew", "false");
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

    //* user exit room
    public void sendServer(){
        class sendData extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                String content = MyData.name + "님이 퇴장 하셨습니다.";
                sendMessageFirebase("ENTER_EXIT", content, "none");
                Intent intent3 = new Intent(ChattingActivity.this, MainActivity.class);
                startActivity(intent3);
            }
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject jsonInput = new JSONObject();

                    jsonInput.put("user_email", MyData.mail);
                    jsonInput.put("room_id", roomID);

                    RequestBody reqBody = RequestBody.create(
                            MediaType.parse("application/json; charset=utf-8"),
                            jsonInput.toString()
                    );

                    Request request = new Request.Builder()
                            .delete(reqBody)
                            .url(urls)
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