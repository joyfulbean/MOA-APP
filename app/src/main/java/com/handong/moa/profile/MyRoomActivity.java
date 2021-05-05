package com.handong.moa.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.handong.moa.R;
import com.handong.moa.data.MyData;
import com.handong.moa.data.ServerInfo;
import com.handong.moa.data.StuffInfo;
import com.handong.moa.main.MainActivity;
import com.handong.moa.main.RecyclerAdapter;
import com.handong.moa.main.SearchActivity;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyRoomActivity extends AppCompatActivity {

    // for server
    private static final String urls = ServerInfo.getUrl() + "room/my/";
    public ArrayList<StuffInfo> thingA = new ArrayList<>();
    public ArrayList<StuffInfo> thingB = new ArrayList<>();
    private boolean AorB = true;

    // for refresh function
    private boolean isRefreshing = false;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // for recycler adapter, for view
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private ChipNavigationBar chipNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_room);

        setNavigationBar();


        //* declaring recycler & linear layout manager
        recyclerView = (RecyclerView) findViewById(R.id.myroom_recyclerview);
        linearLayoutManager = new LinearLayoutManager(getApplication());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //* declaring swipe refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);

        //* call server
        recieveServer();

        //* swipe action
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("LOG_TAG", "Refresh menu item selected");
                if(!isRefreshing) {
                    isRefreshing = true;
                    if(AorB) {
                        recieveServer();
                        AorB = false;
                    }else{
                        recieveServer();
                        thingA.clear();
                        AorB = true;
                    }
                    isRefreshing = false;
                }
            }
        });
    }

    private void setNavigationBar() {
        //* under tab bar (store & profile)
        chipNavigationBar = findViewById(R.id.bottom_navi);
        chipNavigationBar.setItemSelected(R.id.myroom, true);
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                switch (id) {
                    case R.id.myroom:
//                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
//                        overridePendingTransition(0, 0);
//                        finish();
                    case R.id.store:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        break;
                    case R.id.search:
                        startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                        break;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        break;
                }
            }
        });
    }

    //* Stop the refreshing indicator
    private void onRefreshComplete() {
        // Stop the refreshing indicator
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void recieveServer(){
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
                    recyclerView.setAdapter(new RecyclerAdapter(getApplication().getApplicationContext(), thingA, "Thing"));
                }else{
                    thingA.clear();
                    recyclerView.setAdapter(new RecyclerAdapter(getApplication().getApplicationContext(), thingB, "Thing"));
                }
                onRefreshComplete();
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
                Log.i("GetMyRoomList", "Star");
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(urls + MyData.getMail())
                            .build();

                    Response responses = null;
                    responses = client.newCall(request).execute();

                    //json array로 받아서 파싱수 thing에 저장해준다.
                    // 가장 큰 JSONObject를 가져옵니다.
                    JSONObject jObject = new JSONObject(responses.body().string());
                    JSONArray jArray = jObject.getJSONArray("data");

                    Log.i("roomID", Integer.toString(jArray.length()));

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject obj = jArray.getJSONObject(i);
                        StuffInfo temp = new StuffInfo();
                        temp.setTitle(obj.getString("title"));
                        temp.setOrderDate(obj.getString("order_date"));
                        temp.setOrderTime(obj.getString("order_time"));
                        temp.setPlace(obj.getString("place"));
                        temp.setNumUsers(obj.getInt("num_user"));
                        temp.setRoomId(Integer.toString(obj.getInt("rid")));
                        Log.i("roomID", Integer.toString(obj.getInt("rid")));
                        temp.setStuffLink(obj.getString("stuff_link"));
                        temp.setCreator_email(obj.getString("creator_email"));
                        //Log.i("creator",obj.getString("creator_email"));
                        if(AorB) {
                            thingA.add(temp);
                        }else{
                            thingB.add(temp);
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
}