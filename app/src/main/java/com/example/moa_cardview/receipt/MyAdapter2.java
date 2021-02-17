package com.example.moa_cardview.receipt;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.moa_cardview.R;
import com.example.moa_cardview.data.OrderInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyAdapter2 extends BaseAdapter{

    Context context = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<OrderInfo> orderinfos;
//    private static final String MyItemSend_urls = "http://54.180.8.235:5000/receipt";
//    private ArrayList<OrderInfo> listInfos = new ArrayList<>();
//    private MyAdapter myAdapter;

    public MyAdapter2(Context context, ArrayList<OrderInfo> data) {
        context = context;
        orderinfos = data;
        mLayoutInflater = LayoutInflater.from(context);
        Log.i("listinfo",orderinfos.get(0).getStuffName());
    }

    @Override
    public int getCount() {
        return orderinfos.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public OrderInfo getItem(int position) {
        return orderinfos.get(position);
    }

    @Override
    public View getView(final int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.othersorder_layout, null);

        TextView stuff_name = (TextView) view.findViewById(R.id.order_othersorder_product1);
        TextView stuff_cost = (TextView) view.findViewById(R.id.order_othersorder_price2);

//        //먼저 서버에 보내서 그동안 있던거 전부 받음, 그리고 나도 추가 눌러서 업데이트!
//        TextView addButton = (TextView) view.findViewById(R.id.order_othersorder_add_button1);
//        addButton.setOnClickListener((View.OnClickListener)context);
//        addButton.setTag("E" + position);
//
//        addButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                view = mLayoutInflater.inflate(R.layout.myorder_addlayout, null);
//                ListView listView = (ListView)view.findViewById(R.id.order_myorderothers_listview);
//                ListReceiveServer();
//                OrderInfo metoo = getItem(position);
//                listInfos.add(metoo);
//                myAdapter = new MyAdapter(context, listInfos);
//                listView.setAdapter(myAdapter);
//
//                //방법2 리턴값!!
//
//            }
//        });

        Log.i("orderinfos", orderinfos.get(position).getStuffName());
        stuff_name.setText(orderinfos.get(position).getStuffName());
        stuff_cost.setText(orderinfos.get(position).getCost());

        return view;
    }


////room id 연결 필요~
//    public void ListReceiveServer(){
//        class sendData extends AsyncTask<Void, Void, String> {
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//            }
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//            }
//            @Override
//            protected void onProgressUpdate(Void... values) {
//                super.onProgressUpdate(values);
//            }
//            @Override
//            protected void onCancelled(String s) {
//                super.onCancelled(s);
//            }
//            @Override
//            protected void onCancelled() {
//                super.onCancelled();
//            }
//            @Override
//            protected String doInBackground(Void... voids) {
//                try {
//                    Log.i("listTest", "list server start");
//                    OkHttpClient client = new OkHttpClient();
//
//                    Request request = new Request.Builder()
//                            .get()
//                            .url(MyItemSend_urls + "/" + "7")
//                            .build();
//
//                    Response responses = null;
//                    responses = client.newCall(request).execute();
//
//                    JSONObject jObject = new JSONObject(responses.body().string());
//                    JSONArray jArray = jObject.getJSONArray("data");
//
//                    for (int i = 0; i < jArray.length(); i++) {
//                        JSONObject obj = jArray.getJSONObject(i);
//                        OrderInfo listInfo = new OrderInfo();
//
//                        listInfo.setId(obj.getString("id"));
//                        listInfo.setRef_cnt(obj.getString("ref_cnt"));
//                        listInfo.setRegistered_on(obj.getString("registered_on"));
//                        listInfo.setCost(obj.getString("stuff_cost"));
//                        //listInfo.set(obj.getString("stuff_img"));
//                        listInfo.setStuffName(obj.getString("stuff_name"));
//                        listInfo.setNum(obj.getString("stuff_num"));
//                        listInfo.setUser_id(obj.getString("user_id"));
//                        Log.i("listTest", obj.getString("user_id"));
//                        Log.i("listTest", obj.getString("stuff_name"));
//
//                        listInfos.add(listInfo);
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//        }
//        sendData sendData = new sendData();
//        sendData.execute();
//    }



}
