package com.hgu.moa.item;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


import com.hgu.moa.R;
import com.hgu.moa.data.ServerInfo;
import com.hgu.moa.data.StuffInfo;
import com.hgu.moa.main.RecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Food#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Food extends Fragment {

    // for server
    private static final String urls = ServerInfo.getUrl() + "room" + "?category=음식"; //
    public ArrayList<StuffInfo> thingA = new ArrayList<>();
    public ArrayList<StuffInfo> thingB = new ArrayList<>();
    private boolean AorB = true;

    // for refreshing
    private boolean isRefreshing = false;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // for recycler adapter, for view
    public static RecyclerAdapter foodRecyclerAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ImageButton upbutton;
    private ImageButton upperActionButton;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Food() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Thing.
     */
    // TODO: Rename and change types and number of parameters
    public static Stuff newInstance(String param1, String param2) {
        Stuff fragment = new Stuff();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    //fragment 의 oncreat 함수
    //https://brunch.co.kr/@henen/21
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //* declaring view
        View view = inflater.inflate(R.layout.fragment_food,container,false);



        //* declaring recycler & linear layout manager
        recyclerView = (RecyclerView) view.findViewById(R.id.main_thing);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        upbutton = (ImageButton) view.findViewById(R.id.storepage_upbutton);

        //upbutton showing when scroll
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && upbutton.getVisibility() == View.INVISIBLE) {
                    upbutton.setVisibility(View.VISIBLE);
                } else if (dy == 0 && upbutton.getVisibility() != View.INVISIBLE) {
                    upbutton.setVisibility(View.INVISIBLE);
                }
            }
        });


        upperActionButton = (ImageButton) view.findViewById(R.id.storepage_upbutton);
        upperActionButton.setOnClickListener(new View.OnClickListener() { // 이미지 버튼 이벤트 정의
            @Override
            public void onClick(View v) { //클릭 했을경우
                // TODO Auto-generated method stub
                //버튼 클릭 시 발생할 이벤트내용
                linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                linearLayoutManager.scrollToPositionWithOffset(0,0);

                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(linearLayoutManager);

            }
        });

        //* declaring swipe refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);

        //* call server
        receiveServer();

        //* Inflate the layout for this fragment
        return view;
    }
    //* for refresh action
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /**
         * Create an ArrayAdapter to contain the data for the ListView. Each item in the ListView
         * uses the system-defined simple_list_item_1 layout that contains one TextView.
         */

        /**
         * Implement {@link SwipeRefreshLayout.OnRefreshListener}. When users do the "swipe to
         * refresh" gesture, SwipeRefreshLayout invokes
         * {@link SwipeRefreshLayout.OnRefreshListener#onRefresh onRefresh()}. In
         * {@link SwipeRefreshLayout.OnRefreshListener#onRefresh onRefresh()}, call a method that
         * refreshes the content. Call the same method in response to the Refresh action from the
         * action bar.
         * https://devgyugyu.tistory.com/5 -> indexoutofboundsexception when refreshing
         */
        //* swipe action
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("LOG_TAG", "Refresh menu item selected");
                if(!isRefreshing) {
                    isRefreshing = true;
                    if(AorB) {
                        receiveServer();
                        thingB.clear();
                        AorB = false;
                    }else{
                        receiveServer();
                        thingA.clear();
                        AorB = true;
                    }
                    isRefreshing = false;
                }
            }
        });
    }

    //* Stop the refreshing indicator
    private void onRefreshComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
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
                    foodRecyclerAdapter = new RecyclerAdapter(getActivity().getApplicationContext(), thingA, "food");
                    foodRecyclerAdapter.setThreeRoundButton(false);
                    recyclerView.setAdapter(foodRecyclerAdapter);
                }else{
                    thingA.clear();
                    foodRecyclerAdapter = new RecyclerAdapter(getActivity().getApplicationContext(), thingB, "food");
                    foodRecyclerAdapter.setThreeRoundButton(false);
                    recyclerView.setAdapter(foodRecyclerAdapter);
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
