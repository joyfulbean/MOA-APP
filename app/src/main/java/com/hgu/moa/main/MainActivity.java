package com.hgu.moa.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.hgu.moa.chat.MakingRoomActivity;
import com.hgu.moa.data.MyData;
import com.hgu.moa.item.Stuff;
import com.hgu.moa.profile.MyRoomActivity;
import com.hgu.moa.profile.ProfileActivity;
import com.hgu.moa.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;


public class MainActivity extends AppCompatActivity {
    //for button...
    private FloatingActionButton floatingActionButton;
    private ImageButton upperActionButton;
    private RelativeLayout searchButton;

    // for tab page
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ChipNavigationBar chipNavigationBar;

    //main stuff page
    private Stuff stuff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadData();

        //* create the tab
        //https://godog.tistory.com/entry/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EB%B7%B0%ED%8E%98%EC%9D%B4%EC%A0%80-%ED%83%AD-%EB%A0%88%EC%9D%B4%EC%95%84%EC%9B%83-%EA%B5%AC%ED%98%84-1-%EC%A2%8C%EC%9A%B0%EB%A1%9C-%EB%B0%80%EC%96%B4%EC%84%9C-%ED%8E%98%EC%9D%B4%EC%A7%80-%EC%A0%84%ED%99%98?category=781741
        viewPager = findViewById(R.id.storepage_viewpager);
        VPAdapter adapter = new VPAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        //* tab
        tabLayout = findViewById(R.id.storepage_tablayout);
        tabLayout.setupWithViewPager(viewPager);

        //* tab bar info
        ArrayList<String> iText = new ArrayList<String>();
        iText.add("??????");
        iText.add("??????");
        iText.add("OTT");
        iText.add("??????");
        for(int i=0; i<4; i++) {
            tabLayout.getTabAt(i).setText(iText.get(i));
        }

        //* search action
        searchButton = findViewById(R.id.storepage_searchbar);
        searchButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });


        //* ??????????????? ????????????, ?????????????????? ???????????? ??????
//        chipNavigationBar = findViewById(R.id.bottom_navi);
//        chipNavigationBar.setItemSelected(R.id.store, true);
//        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(int id) {
//                switch (id){
//                    case R.id.store:
////                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
////                        overridePendingTransition(0, 0);
////                        finish();
//                        //todolist2: db?????? ???????????? ?????? 1
//                    case R.id.profile:
//                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
//                        overridePendingTransition(0, 0);
//                        finish();
//                        //todolist3: db?????? ???????????? ?????? 2
//                }
//            }
//        });
        //* ??????????????? ????????????, ?????????????????? ???????????? ??????
        chipNavigationBar = findViewById(R.id.bottom_navi);
        chipNavigationBar.setItemSelected(R.id.store, true);
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                switch (id){
                    case R.id.store:
//                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                        overridePendingTransition(0, 0);
//                        finish();
                        //todolist2: db?????? ???????????? ?????? 1
                    case R.id.profile:
                        Log.i("navid", "profile");
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        break;
                    case R.id.search:
                        Log.i("navid", "search");
                        startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                        finish();
                        break;
                    case R.id.myroom:
                        startActivity(new Intent(getApplicationContext(), MyRoomActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        break;
                }
            }
        });



        //* create room - plus button
        floatingActionButton = (FloatingActionButton) findViewById(R.id.create_room_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() { // ????????? ?????? ????????? ??????
            @Override
            public void onClick(View v) { //?????? ????????????
                // TODO Auto-generated method stub
                //?????? ?????? ??? ????????? ???????????????
                Intent intent = new Intent(MainActivity.this, MakingRoomActivity.class);
                startActivity(intent);
            }
        });
    }

    //* ??? phone??? ???????????? ?????? ??????????????? ????????????
    void loadData(){
        SharedPreferences preferences=getSharedPreferences("info",MODE_PRIVATE);
        MyData.phoneNumber=preferences.getString("phoneNumber", null);
        MyData.accountNumber = preferences.getString("accountNumber", null);
        MyData.accountName = preferences.getString("accountName", null);
        MyData.bankName = preferences.getString("bankName", null);
        MyData.account = preferences.getString("account", null);
    }
}