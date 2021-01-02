package com.example.moa_cardview.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.moa_cardview.R;

public class SearchAddressActivity extends AppCompatActivity {

    private ImageButton closeButton;
    private ImageButton currentLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_address);

        closeButton = findViewById(R.id.searchAddress_closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        currentLocationButton = findViewById(R.id.searchAddress_currentLocationButton);
        currentLocationButton.setOnClickListener(new View.OnClickListener() { // 이미지 버튼 이벤트 정의
            @Override
            public void onClick(View v) { //클릭 했을경우
                // TODO Auto-generated method stub
                //버튼 클릭 시 발생할 이벤트내용
                Intent intent = new Intent(SearchAddressActivity.this, PlaceSelectActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }
}