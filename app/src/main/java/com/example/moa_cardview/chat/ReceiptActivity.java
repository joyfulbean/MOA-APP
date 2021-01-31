package com.example.moa_cardview.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.moa_cardview.R;

public class ReceiptActivity extends AppCompatActivity {

    private ImageButton ReceiptButton;
    private String roomID;

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

        //* create room
        ReceiptButton = findViewById(R.id.createroom_createbutton);
        ReceiptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChattingActivity.class);
                intent.putExtra("room_id",roomID);
                startActivity(intent);
                finish();
            }
        });
    }
}