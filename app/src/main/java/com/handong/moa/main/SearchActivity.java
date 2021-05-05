package com.handong.moa.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.handong.moa.R;
import com.handong.moa.data.ServerInfo;
import com.handong.moa.data.StuffInfo;
import com.handong.moa.item.All;
import com.handong.moa.item.Food;
import com.handong.moa.item.OTT;
import com.handong.moa.item.Stuff;
import com.handong.moa.item.Taxi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class SearchActivity extends AppCompatActivity {

    // for server
    private static final String urls = ServerInfo.getUrl() + "room"; //
    public ArrayList<StuffInfo> thingA = new ArrayList<>();
    public ArrayList<StuffInfo> thingB = new ArrayList<>();
    private boolean AorB = true;

    // for recycler adapter, for view
    public RecyclerAdapter allRecyclerAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    // for search action
    private ViewPager viewPager;
    private EditText searchContent;
    private ImageButton searchButton;
    private ImageButton deleteButton;
    private ImageButton backButton;

    //main stuff page
    private All all;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        receiveServer();

        //* declaring recycler & linear layout manager
        recyclerView = (RecyclerView) findViewById(R.id.main_thing);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //* search content
        searchContent = (EditText)findViewById(R.id.searchpage_search_edittext);
        searchContent.addTextChangedListener(new TextWatcher() { // Edit Text Change Listener
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                allRecyclerAdapter.getFilter().filter("");
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Text의 길이에 따라서 delete button이 보이거나 안보이게 설정해준다.
                if(charSequence.length() > 0) {
                    deleteButton.setVisibility(View.VISIBLE);
                } else {
                    deleteButton.setVisibility(View.INVISIBLE);
                }
                allRecyclerAdapter.getFilter().filter(charSequence);
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
                allRecyclerAdapter.getFilter().filter("");
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

    public void receiveServer(){
        class sendData extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                thingA.clear();
                thingB.clear();
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(AorB) {
                    thingB.clear();
                    allRecyclerAdapter = new RecyclerAdapter(SearchActivity.this, thingA);
                    allRecyclerAdapter.setThreeRoundButton(false);
                    recyclerView.setAdapter(allRecyclerAdapter);
                }else{
                    thingA.clear();
                    allRecyclerAdapter = new RecyclerAdapter(SearchActivity.this, thingB);
                    allRecyclerAdapter.setThreeRoundButton(false);
                    recyclerView.setAdapter(allRecyclerAdapter);
                }
            }
            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
            }
            @Override
            protected void onCancelled(String s) {
                super.onCancelled(s);
            }
            @Override
            protected void onCancelled() {
                super.onCancelled();
            }
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(urls)
                            .build();

                    Response responses = null;
                    responses = client.newCall(request).execute();

                    //json array로 받아서 파싱수 thing에 저장해준다.
                    // 가장 큰 JSONObject를 가져옵니다.
                    JSONObject jObject = new JSONObject(responses.body().string());
                    JSONArray jArray = jObject.getJSONArray("data");


                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject obj = jArray.getJSONObject(i);
                        StuffInfo temp = new StuffInfo();
                        temp.setTitle(obj.getString("title"));
                        temp.setOrderDate(obj.getString("order_date"));
                        temp.setOrderTime(obj.getString("order_time"));
                        temp.setPlace(obj.getString("place"));
                        temp.setNumUsers(obj.getInt("num_user"));
                        //temp.setStuffCost(obj.getString("stuff_cost")+"원");
                        temp.setRoomId(Integer.toString(obj.getInt("rid")));
                        Log.i("roomID", Integer.toString(obj.getInt("rid")));
                        temp.setCreator_email(obj.getString("creator_email"));
                        //Log.i("creator2",obj.getString("creator_email"));
                        //temp.setStuffLink(obj.getString("stuff_link"));
                        //temp.setImageUrl(obj.getString("image_url"));
                        //temp.setOgTitle(obj.getString("og_title"));

                        //Log.i("db", temp.getImageUrl());
                        System.out.println("all! ^^");
                        if(AorB) {
                            thingA.add(temp);
                            System.out.println("^A^");
                        }else{
                            thingB.add(temp);
                            System.out.println("^B^");
                        }
                    }
                    responses.close();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }
        sendData sendData = new sendData();
        sendData.execute();
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