package com.example.moa_cardview.item_page;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moa_cardview.R;
import com.example.moa_cardview.data.StuffInfo;

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
 * Use the {@link OTT#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OTT extends Fragment {

    private RecyclerView rv;
    private LinearLayoutManager llm;
    private static final String urls = "http://54.180.8.235:3306/room";
    public ArrayList<StuffInfo> thing = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OTT() {
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
        View view = inflater.inflate(R.layout.fragment_o_t_t,container,false);

        // Inflate the layout for this fragment
        return view;
    }


    public void recieveServer(){
        class sendData extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                rv.setAdapter(new RecyclerAdapter(getActivity().getApplicationContext(), thing, "Thing"));

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
                    Log.i("test1", String.valueOf(jObject));
                    JSONArray jArray = jObject.getJSONArray("rooms");
                    Log.i("test2", String.valueOf(jArray));


                    for (int i = 0; i < jArray.length(); i++) {

                        JSONObject obj = jArray.getJSONObject(i);
                        StuffInfo temp = new StuffInfo();
                        temp.setTitle(obj.getString("title"));
                        Log.i("test3", obj.getString("title"));
                        temp.setOrderDate(obj.getString("order_date"));
                        temp.setOrderTime(obj.getString("order_time"));
                        temp.setPlace(obj.getString("place"));
                        temp.setNumUsers(obj.getString("num_users"));
                        temp.setStuffCost(obj.getString("stuff_cost")+"원");
                        thing.add(temp);

                    }

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