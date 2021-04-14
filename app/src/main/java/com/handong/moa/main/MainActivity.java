package com.handong.moa.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.handong.moa.chat.MakingRoomActivity;
import com.handong.moa.data.MyData;
import com.handong.moa.profile.ProfileActivity;
import com.handong.moa.R;
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

        //* search action
        searchButton = findViewById(R.id.storepage_searchbar);
        searchButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        //* create the main
        //https://godog.tistory.com/entry/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EB%B7%B0%ED%8E%98%EC%9D%B4%EC%A0%80-%ED%83%AD-%EB%A0%88%EC%9D%B4%EC%95%84%EC%9B%83-%EA%B5%AC%ED%98%84-1-%EC%A2%8C%EC%9A%B0%EB%A1%9C-%EB%B0%80%EC%96%B4%EC%84%9C-%ED%8E%98%EC%9D%B4%EC%A7%80-%EC%A0%84%ED%99%98?category=781741
        viewPager = findViewById(R.id.storepage_viewpager);
        stuff = new Stuff();
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(stuff, "Stuff");
        viewPager.setAdapter(viewPagerAdapter);

        //* 마이페이지 정보버튼, 메인페이지로 돌아오는 버튼
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
                        //todolist2: db에서 받아오는 코드 1
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
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
        MyData.accountNumber = preferences.getString("accountNumber", null);
        MyData.accountName = preferences.getString("accountName", null);
        MyData.bankName = preferences.getString("bankName", null);
        MyData.account = preferences.getString("account", null);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments = new ArrayList<>();
        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }
        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }
        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}