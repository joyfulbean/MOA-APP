package com.handong.moa.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.handong.moa.R;
import com.handong.moa.chat.MakingRoomActivity;
import com.handong.moa.main.MainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    //* for tab & view page
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ChipNavigationBar chipNavigationBar;

    //* for creating room
    private FloatingActionButton floatingActionButton;

    //* for my room page and my info page
    private MyRoom myRoom;
    private MyInfo myInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //* view page and tab bar
        viewPager = findViewById(R.id.profilepage_viewpager);
        tabLayout = findViewById(R.id.profilepage_tablayout);

        tabLayout.setupWithViewPager(viewPager);

        myRoom = new MyRoom();
        myInfo = new MyInfo();

        //* change page (my room & my info)
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(myRoom, "MyRoom");
        viewPagerAdapter.addFragment(myInfo, "MyInfo");
        viewPager.setAdapter(viewPagerAdapter);

        ArrayList<String> iText = new ArrayList<String>();

        iText.add("참여중");
        iText.add("나의 정보");

        for (int i = 0; i < 2; i++) {
            tabLayout.getTabAt(i).setText(iText.get(i));
        }


        //* under tab bar (store & profile)
        chipNavigationBar = findViewById(R.id.bottom_navi);
        chipNavigationBar.setItemSelected(R.id.profile, true);
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                switch (id) {
                    case R.id.profile:
//                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
//                        overridePendingTransition(0, 0);
//                        finish();
                    case R.id.store:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
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
                Intent intent = new Intent(ProfileActivity.this, MakingRoomActivity.class);
                startActivity(intent);
                finish();
            }
        });
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

