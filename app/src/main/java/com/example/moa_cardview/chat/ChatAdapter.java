package com.example.moa_cardview.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.moa_cardview.data.MyData;
import com.example.moa_cardview.R;

import java.util.ArrayList;

/**
 * Created by KPlo on 2018. 10. 28..
 */

public class ChatAdapter  extends BaseAdapter {
    private ArrayList<ChatMessageItem> messageItems;
    private LayoutInflater layoutInflater;

    public ChatAdapter(ArrayList<ChatMessageItem> messageItems, LayoutInflater layoutInflater) {
        this.messageItems = messageItems;
        this.layoutInflater = layoutInflater;
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
        if(item.getName().equals(MyData.name)){
            itemView = layoutInflater.inflate(R.layout.my_msgbox,viewGroup,false);
        }else{
            itemView = layoutInflater.inflate(R.layout.other_msgbox,viewGroup,false);
        }

        //만들어진 itemView에 값들 설정
        TextView msgboxName = itemView.findViewById(R.id.msgbox_name);
        TextView msgboxContent = itemView.findViewById(R.id.msgbox_content);
        TextView msgboxTime = itemView.findViewById(R.id.msgbox_time);

        msgboxName.setText(item.getName());
        msgboxContent.setText(item.getMessage());
        msgboxTime.setText(item.getTime());

        //Glide.with(itemView).load(item.getPofileUrl()).into(iv);
        return itemView;
    }
}
