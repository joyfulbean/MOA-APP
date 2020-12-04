package com.example.moa_cardview.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.moa_cardview.chat.MakingRoomActivity;
import com.example.moa_cardview.data.MyData;
import com.example.moa_cardview.profile.ProfileActivity;
import com.example.moa_cardview.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;


public class MainActivity extends AppCompatActivity {
    //for button...
    private FloatingActionButton floatingActionButton;
    private ImageButton upperActionButton;
    private ImageButton searchButton;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    // for tab page
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ChipNavigationBar chipNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadData();

        //splash 화면을 싱행시켜준다.
        //https://coding-factory.tistory.com/50
//
//        Intent intent2 = new Intent(this, LoadingActivity.class);
//        startActivity(intent2);

//        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
//        if(signInAccount != null){
//            ChatData.setName(signInAccount.getDisplayName());
//            ChatData.setMail(signInAccount.getEmail());
//        }
//        else{
//            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            startActivity(intent);
//        }
//
//
        /*
        upperActionButton = (ImageButton) findViewById(R.id.storepage_upbutton);
        upperActionButton.setOnClickListener(new View.OnClickListener() { // 이미지 버튼 이벤트 정의
            @Override
            public void onClick(View v) { //클릭 했을경우
                // TODO Auto-generated method stub
                //버튼 클릭 시 발생할 이벤트내용
                recyclerView = (RecyclerView) findViewById(R.id.main_thing);
                linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                linearLayoutManager.scrollToPositionWithOffset(0,0);

                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(linearLayoutManager);

            }
        });
         */

        //* search action
        searchButton = (ImageButton)findViewById(R.id.storepage_searchbar);
        searchButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
                //todolist 검색 기능 구현
            }
        });

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
        iText.add("상품");
        iText.add("음식");
        iText.add("OTT");
        iText.add("택시");
        for(int i=0; i<4; i++) {
            tabLayout.getTabAt(i).setText(iText.get(i));
        }

        //* 마이페이지 정보버튼, 메인페이지로 돌아오는 버튼
        chipNavigationBar = findViewById(R.id.bottom_navi);
        chipNavigationBar.setItemSelected(R.id.store, true);
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                switch (id){
                    case R.id.store:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        //todolist2: db에서 받아오는 코드 1
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0, 0);
                        //todolist3: db에서 받아오는 코드 2
                }
            }
        });

        //* create room - plus button
        floatingActionButton = (FloatingActionButton) findViewById(R.id.create_room_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() { // 이미지 버튼 이벤트 정의
            @Override
            public void onClick(View v) { //클릭 했을경우
                // TODO Auto-generated method stub
                //버튼 클릭 시 발생할 이벤트내용
                Intent intent = new Intent(MainActivity.this, MakingRoomActivity.class);
                startActivity(intent);
            }
        });
    }

    //* 내 phone에 저장되어 있는 프로필정보 읽어오기
    void loadData(){
        SharedPreferences preferences=getSharedPreferences("info",MODE_PRIVATE);
        MyData.phoneNumber=preferences.getString("phoneNumber", null);
        MyData.account=preferences.getString("account", null);
    }
}