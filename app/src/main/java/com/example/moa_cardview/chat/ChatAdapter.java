package com.example.moa_cardview.chat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moa_cardview.data.MyData;
import com.example.moa_cardview.R;

import java.util.ArrayList;

import static android.content.Context.CLIPBOARD_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

/**
 * Created by KPlo on 2018. 10. 28..
 */

public class ChatAdapter  extends BaseAdapter {
    private ArrayList<ChatMessageItem> messageItems;
    private LayoutInflater layoutInflater;
    private String MOAcontent;
    private Context context;
    private boolean copy;

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
        if(item.getImg().equals("none")) {
            if (item.getName().equals(MyData.name)) {
                itemView = layoutInflater.inflate(R.layout.my_msgbox, viewGroup, false);
                setting(item, itemView);
            } else if (item.getName().equals("MOA")) {
                itemView = layoutInflater.inflate(R.layout.moabox, viewGroup, false);
                setting(item, itemView);
            } else if (item.getName().equals("ENTER_EXIT")) {
                itemView = layoutInflater.inflate(R.layout.enter_exit_box, viewGroup, false);
                TextView msgboxContent = itemView.findViewById(R.id.msgbox_content); //텍스트뷰
                msgboxContent.setText(item.getMessage());
            } else {
                itemView = layoutInflater.inflate(R.layout.other_msgbox, viewGroup, false);
                setting(item, itemView);
            }
        }
        else{
            if (item.getName().equals(MyData.name)) {
                itemView = layoutInflater.inflate(R.layout.my_msgbox, viewGroup, false);
                imgSetting(item, itemView);
            } else if (item.getName().equals("MOA")) {
                itemView = layoutInflater.inflate(R.layout.moabox, viewGroup, false);
                imgSetting(item, itemView);
            } else if (item.getName().equals("ENTER_EXIT")) {
                itemView = layoutInflater.inflate(R.layout.enter_exit_box, viewGroup, false);
                TextView msgboxContent = itemView.findViewById(R.id.msgbox_content); //텍스트뷰
                msgboxContent.setText(item.getMessage());
            } else {
                itemView = layoutInflater.inflate(R.layout.other_msgbox, viewGroup, false);
                imgSetting(item, itemView);
            }
        }
        //Glide.with(itemView).load(item.getPofileUrl()).into(iv);
        return itemView;
    }

    public void setting(ChatMessageItem item, View itemView){
        //만들어진 itemView에 값들 설정
        TextView msgboxName = itemView.findViewById(R.id.msgbox_name);
        TextView msgboxContent = itemView.findViewById(R.id.msgbox_content); //텍스트뷰
        TextView msgboxTime = itemView.findViewById(R.id.msgbox_time);

        msgboxName.setText(item.getName());
        msgboxContent.setText(item.getMessage());
        msgboxTime.setText(item.getTime());
    }


    public void imgSetting(ChatMessageItem item, View itemView){
        //만들어진 itemView에 값들 설정
        TextView msgboxName = itemView.findViewById(R.id.msgbox_name);
        ImageView imgboxContent = itemView.findViewById(R.id.imgbox_content); //텍스트뷰
        TextView msgboxTime = itemView.findViewById(R.id.msgbox_time);

        msgboxName.setText(item.getName());
        //imgboxContent.setText(item.getMessage());
        msgboxTime.setText(item.getTime());


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
}

//https://kutar37.tistory.com/entry/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EC%B1%84%ED%8C%85%EC%95%B1-%EB%A7%8C%EB%93%A4%EA%B8%B0-ListView-Adapter
