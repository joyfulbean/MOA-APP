package com.handong.moa.item;



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

import com.handong.moa.R;
import com.handong.moa.data.ServerInfo;
import com.handong.moa.data.StuffInfo;
import com.handong.moa.main.RecyclerAdapter;

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
 * Use the {@link Stuff#newInstance} factory method to
 * create an instance of this fragment.
 */
public class All extends Fragment {
    // for server
    private static final String urls = ServerInfo.getUrl() + "room"; //
    public ArrayList<StuffInfo> thingA = new ArrayList<>();
    public ArrayList<StuffInfo> thingB = new ArrayList<>();
    private boolean AorB = true;

    // for recycler adapter, for view
    public static RecyclerAdapter allRecyclerAdapter;
    private RecyclerView recyclerView;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public All() {
        // Required empty public constructor
    }

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
        setHasOptionsMenu(true);
    }

    //* for refresh action
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        receiveServer();
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
                allRecyclerAdapter = new RecyclerAdapter(getActivity().getApplicationContext(), "Thing");
                allRecyclerAdapter.setThreeRoundButton(false);
                recyclerView.setAdapter(allRecyclerAdapter);
                if(AorB) {
                    thingB.clear();
                    allRecyclerAdapter.setStuff(thingA);
                    allRecyclerAdapter.setSearchListAll(thingA);
                }else{
                    thingA.clear();
                    allRecyclerAdapter.setStuff(thingB);
                    allRecyclerAdapter.setSearchListAll(thingB);
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
}
