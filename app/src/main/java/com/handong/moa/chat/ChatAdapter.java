package com.handong.moa.chat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.handong.moa.data.MyData;
import com.handong.moa.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import static android.content.Context.CLIPBOARD_SERVICE;
/**
 * Created by KPlo on 2018. 10. 28..
 */

public class ChatAdapter  extends BaseAdapter {
    private ArrayList<ChatMessageItem> messageItems;
    private LayoutInflater layoutInflater;
    private String MOAcontent;
    private Context context;
    private boolean copy;
    private ImageView msgImage;

    public boolean isCopy() {
        return copy;
    }

    public void setCopy(boolean copy) {
        this.copy = copy;
    }

    public ChatAdapter(ArrayList<ChatMessageItem> messageItems, LayoutInflater layoutInflater, Context context) {
        this.messageItems = messageItems;
        this.layoutInflater = layoutInflater;
        this.context = context;
    }

    @Override
    public int getCount() {
        return messageItems.size();
    }

    @Override
    public Object getItem(int position) {
        return messageItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        //현재 보여줄 번째의(position)의 데이터로 뷰를 생성
        ChatMessageItem item = messageItems.get(position);

        //재활용할 뷰는 사용하지 않음!!
        View itemView = null;

        //메세지가 내 메세지인지 auth 확인
        if(item.getImg().equals("none")){
            if (item.getUid().equals(MyData.uid)) {
                itemView = layoutInflater.inflate(R.layout.my_msgbox, viewGroup, false);
                setting(item, itemView, false, "none");
            } else if (item.getUid().equals("ENTER_EXIT")) {
                itemView = layoutInflater.inflate(R.layout.enter_exit_box, viewGroup, false);
                TextView msgboxContent = itemView.findViewById(R.id.msgbox_content); //텍스트뷰
                msgboxContent.setText(item.getMessage());
            } else {
                itemView = layoutInflater.inflate(R.layout.other_msgbox, viewGroup, false);
                setting(item, itemView, true, item.getUrl());
            }
        }
        else{
            if (item.getUid().equals(MyData.uid)) {
                itemView = layoutInflater.inflate(R.layout.my_imagebox, viewGroup, false);
                imgSetting(item, itemView);
            } else {
                itemView = layoutInflater.inflate(R.layout.other_imagebox, viewGroup, false);
                imgSetting(item, itemView);
            }
        }
        return itemView;
    }

    public void setting(ChatMessageItem item, View itemView, boolean isYou, String url){
        //만들어진 itemView에 값들 설정
        TextView msgboxName = itemView.findViewById(R.id.msgbox_name);
        TextView msgboxContent = itemView.findViewById(R.id.msgbox_content); //텍스트뷰
        TextView msgboxTime = itemView.findViewById(R.id.msgbox_time);
        if(isYou) {
            msgImage = itemView.findViewById(R.id.other_msgbox_profile_imageview);
            ChatLoadImageTask imageTask = new ChatLoadImageTask(url);
            imageTask.execute();
        }
        msgboxName.setText(item.getName());
        msgboxContent.setText(item.getMessage());
        msgboxTime.setText(item.getTime());
    }


    public void imgSetting(ChatMessageItem item, final View itemView){
        //만들어진 itemView에 값들 설정
        TextView msgboxName = itemView.findViewById(R.id.msgbox_name);
        final ImageView imgboxContent = itemView.findViewById(R.id.imgbox_content); //텍스트뷰
        TextView msgboxTime = itemView.findViewById(R.id.msgbox_time);

        msgboxName.setText(item.getName());
        msgboxTime.setText(item.getTime());

        //1. Firebase Storeage관리 객체 얻어오기
        FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();
        //2. 최상위노드 참조 객체 얻어오기
        StorageReference rootRef= firebaseStorage.getReference();
        //하위 폴더가 있다면 폴더명까지 포함하여
        StorageReference imgRef = rootRef.child(item.getImg());
        Log.i("item.getImg()",item.getImg());
        if(imgRef!=null){
            //참조객체로 부터 이미지의 다운로드 URL을 얻어오기
            imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    //다운로드 URL이 파라미터로 전달되어 옴.
                    Glide.with(itemView).load(uri).into(imgboxContent);
                }
            });

        }
    }

    public void copy(int position) {
        //현재 보여줄 번째의(position)의 데이터로 뷰를 생성
        ChatMessageItem item = messageItems.get(position);


        MOAcontent = item.getMessage(); // 텍스트뷰 글자 가져옴
        //클립보드 사용 코드
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("ID",MOAcontent); //클립보드에 ID라는 이름표로 id 값을 복사하여 저장
        clipboardManager.setPrimaryClip(clipData);

        //복사가 되었다면 토스트메시지 노출
        Toast.makeText(context, MOAcontent,Toast.LENGTH_SHORT).show();
    }

    //* for photo
    public class ChatLoadImageTask extends AsyncTask<Bitmap, Void, Bitmap> {
        private String url;

        public ChatLoadImageTask(String url) {
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
            msgImage.setImageBitmap(bit);
            msgImage.setBackground(new ShapeDrawable(new OvalShape()));
            msgImage.setClipToOutline(true);
        }
    }
}