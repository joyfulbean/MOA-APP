package com.example.moa_cardview.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moa_cardview.R;
import com.example.moa_cardview.main.MainActivity;

public class PlaceSelectActivity extends AppCompatActivity {

    ImageView bt01;
    ImageView textImage, textImage2;
    TextView placeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_select);

        textImage = findViewById(R.id.imageView6);
        textImage2 = findViewById(R.id.imageView5);
        placeName = findViewById(R.id.createroom_stuffaddress_textview);

//        bt01 = findViewById(R.id.promotion_image2);
//        bt01.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                textImage.setVisibility(View.VISIBLE);
//                textImage2.setVisibility(View.VISIBLE);
//            }
//        });

        textImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("Mapp", "Map transferring data");
                Intent data = new Intent();
                //getContext
                data.putExtra("myData1", "경상북도 문경시 산북면 대상리 256");
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }
}