package com.example.moa_cardview.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.moa_cardview.profile.LoginActivity;

public class LoadingActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 0.7초동안 유지되는 첫 로고 화면
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}