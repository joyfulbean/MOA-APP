package com.handong.moa.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.handong.moa.R;
import com.handong.moa.data.StuffInfo;
import com.handong.moa.item.All;
import com.handong.moa.item.Food;
import com.handong.moa.item.OTT;
import com.handong.moa.item.Stuff;
import com.handong.moa.item.Taxi;

import java.util.ArrayList;
import java.util.List;


public class SearchActivity extends AppCompatActivity {
    // for search action
    private ViewPager viewPager;
    private EditText searchContent;
    private ImageButton searchButton;
    private ImageButton deleteButton;
    private ImageButton backButton;

    //main stuff page
    private Stuff stuff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //* view page
        viewPager = findViewById(R.id.searchpage_viewpager);
        VPAdapter adapter = new VPAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        //* search content
        searchContent = (EditText)findViewById(R.id.searchpage_search_edittext);
        searchContent.addTextChangedListener(new TextWatcher() { // Edit Text Change Listener
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Stuff.stuffRecyclerAdapter.getFilter().filter("");
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Text의 길이에 따라서 delete button이 보이거나 안보이게 설정해준다.
                if(charSequence.length() > 0) {
                    deleteButton.setVisibility(View.VISIBLE);
                } else {
                    deleteButton.setVisibility(View.INVISIBLE);
                }
                Stuff.stuffRecyclerAdapter.getFilter().filter(charSequence);
            }
            @Override
            public void afterTextChanged(Editable editable) { //주로 엔터 키 같은 입력값을 받기위해 사용
            }
        });

        //* search button
        searchButton = findViewById(R.id.searchpage_searchbutton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText searchText = (EditText)findViewById(R.id.searchpage_search_edittext);
                //Search button 을 누르면 키보드가 내려가게 해준다.
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
            }
        });

        //* delete button
        deleteButton = findViewById(R.id.searchpage_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText searchText = (EditText)findViewById(R.id.searchpage_search_edittext);
                searchText.setText("");
                Stuff.stuffRecyclerAdapter.getFilter().filter("");
            }
        });

        //* back button
        backButton = findViewById(R.id.searchpage_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }
}