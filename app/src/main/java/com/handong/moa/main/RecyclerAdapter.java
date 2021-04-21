package com.handong.moa.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.handong.moa.R;
import com.handong.moa.chat.ChatMessageItem;
import com.handong.moa.chat.ChattingActivity;
import com.handong.moa.data.ServerInfo;
import com.handong.moa.receipt.ReceiptActivity;
import com.handong.moa.data.MyData;
import com.handong.moa.data.StuffInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> implements Filterable {
    // for card view info
    private Context context;
    private ArrayList<StuffInfo> stuff;
    private String type;
    private List<StuffInfo> tempListAll;

    // for getting link image
    private String title;
    private String contents;
    private String imageUrl;

    private boolean isThreeRoundButton = true;

    public void setThreeRoundButton(boolean threeRoundButton) {
        isThreeRoundButton = threeRoundButton;
    }

    // for is new
    private static final String urls = ServerInfo.getUrl() + "participant";
    private boolean isNew;


    public RecyclerAdapter(Context context, ArrayList<StuffInfo> stuff, String type) {
        this.context = context;
        this.stuff = stuff;
        this.type = type;
        this.tempListAll = new ArrayList<>(stuff);
    }

    //* setting the view
    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        MyViewHolder vHolder = new MyViewHolder(v);
        return vHolder;
    }

    //* 화면에 띄울 변수들
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView stuffTitle;
        TextView stuffDate;
        TextView stuffPlace;
        TextView stuffTime;
        TextView stuffPeopleNum;
        LinearLayout postLayout;

        TextView enterButton;

        ImageButton threeroundbutton;
        TextView time;
        TextView date;

        ImageView crownImage;

        public MyViewHolder(final View itemView) {
            super(itemView);

            //* for displaying screen info
            stuffTitle = (TextView) itemView.findViewById(R.id.post_title_textview);
            stuffPlace = (TextView) itemView.findViewById(R.id.post_place_textview);
            stuffTime = (TextView) itemView.findViewById(R.id.post_time_textview);
            time = (TextView) itemView.findViewById(R.id.post_timetitle_textview);
            stuffPeopleNum = (TextView) itemView.findViewById(R.id.post_pplnumber_textview);
            crownImage = (ImageView)itemView.findViewById(R.id.post_crown);

            threeroundbutton = itemView.findViewById(R.id.post_threeroundbutton);
            if(!isThreeRoundButton){
                threeroundbutton.setVisibility(View.GONE);
            }
            threeroundbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    PopupMenu popup= new PopupMenu(context, v);//v는 클릭된 뷰를 의미
                    final int position = getAdapterPosition();

                    popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()){
                                case R.id.popupLeave:
                                    popupLeave(position, v);
                                    break;
                                case R.id.popupReport:
                                    popupReport(position, v);
                                    break;
                                default:
                                    break;
                            }
                            return false;
                        }
                    });
                    popup.show();//Popup Menu 보이기
                }
            });

            enterButton = itemView.findViewById(R.id.post_createbutton_textview);
            enterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    int position = getAdapterPosition();
                    isNewCheckerServer(position);
                }
            });
        }
    }

    //* initial card view info
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        //Log.i("email22","hello");
        Log.i("yebin",MyData.mail);
        Log.i("yebin2",stuff.get(position).getCreator_email());
        if(MyData.mail.equals(stuff.get(position).getCreator_email())){
            Log.i("yebin","yebin");
            holder.crownImage.setVisibility(View.VISIBLE);
        }
        else{
            holder.crownImage.setVisibility(View.GONE);
        }

        holder.stuffTitle.setText(stuff.get(position).getTitle());
        //날짜
        String order_date = stuff.get(position).getOrderDate();

        //시간
        String order_time = stuff.get(position).getOrderTime();

        if (order_date == null && order_time != null){
            holder.time.setText(order_time);
        }
        else if (order_time == null || order_date == null){
            holder.time.setText("");
            holder.stuffTime.setText("");
        }
        else if (order_date !=null && order_time != null){
            String time_combined = order_date + " " +  order_time;
            holder.stuffTime.setText(time_combined);
        }



        //장소들
        holder.stuffPlace.setText(stuff.get(position).getPlace());
        //사람수
        holder.stuffPeopleNum.setText(Integer.toString(stuff.get(position).getNumUsers()));
    }

    public void popupLeave(final int position, View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext())
                .setIcon(R.drawable.logosmall)
                .setTitle("방을 나가시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        leaveRoom(position);
                    }
                })
                .setNegativeButton("취소", null);
        builder.show();
    }

    public void popupReport(final int position, final View v){
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("plain/text");
        String[] address = {"212600212@handong.edu"};
        email.putExtra(Intent.EXTRA_EMAIL, address);
        email.putExtra(Intent.EXTRA_SUBJECT, "[신고]"+"[방번호:"+stuff.get(position).getRoomId()+"]");
        email.putExtra(Intent.EXTRA_TEXT, "");

        Log.i("Email", "before start activity");

        v.getContext().startActivity(email.addFlags(FLAG_ACTIVITY_NEW_TASK));

    }

    public void leaveRoom(int position){
        LearveRoomServer(stuff.get(position).getRoomId());
    }

    public void sendEmail(int position, final View v){
        Log.i("Email", "start");

        TextView title = (TextView) v.findViewById(R.id.report_title);
        TextView content = (TextView) v.findViewById(R.id.report_content);

        String str = "[신고]"+"[방번호:"+stuff.get(position).getRoomId()+"]"+title.getText();

        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("plain/text");
        String[] address = {"212600212@handong.edu"};
        email.putExtra(Intent.EXTRA_EMAIL, address);
        email.putExtra(Intent.EXTRA_SUBJECT, str);
        email.putExtra(Intent.EXTRA_TEXT, content.getText());

        Log.i("Email", "before start activity");

        v.getContext().startActivity(email.addFlags(FLAG_ACTIVITY_NEW_TASK));

        Log.i("Email", str);
    }

    public void isNewCheckerServer(final int position){
        class sendData extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
//                if(isNew) {
//                    Intent intent = new Intent(context, ReceiptActivity.class);
//                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
//                    Log.i("roomID", stuff.get(position).getRoomId());
//                    intent.putExtra("test_id", stuff.get(position).getRoomId());
//                    context.startActivity(intent);
//                }else{
                Intent intent = new Intent(context, ChattingActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                Log.i("roomID", stuff.get(position).getRoomId());
                intent.putExtra("room_id", stuff.get(position).getRoomId());
                intent.putExtra("isNew",isNew);
                context.startActivity(intent);
//                }
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

                    JSONObject jsonInput = new JSONObject();
                    jsonInput.put("room_id", stuff.get(position).getRoomId());
                    jsonInput.put("user_email", MyData.mail);

                    RequestBody reqBody = RequestBody.create(
                            MediaType.parse("application/json; charset=utf-8"),
                            jsonInput.toString()
                    );

                    Request request = new Request.Builder()
                            .post(reqBody)
                            .url(urls + File.separator + stuff.get(position).getRoomId())
                            .build();

                    Response responses = null;
                    responses = client.newCall(request).execute();

                    //json array로 받아서 파싱수 thing에 저장해준다.
                    // 가장 큰 JSONObject를 가져옵니다.
                    JSONObject obj = new JSONObject(responses.body().string());
                    isNew = obj.getBoolean("is_new");
//                    Log.i("db22", chattingInfo.getOgTitle());
                    if(isNew){
                        Log.i("isNew", "true");
                    }
                    else {
                        Log.i("isNew", "false");
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

    //* the number of card view
    @Override
    public int getItemCount() {
        return this.stuff.size();
    }

    //* for search
    @Override
    public Filter getFilter() {
        return filter;
    }

    //* for search
    Filter filter = new Filter() {
        //runs on background thread
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<StuffInfo> filteredList = new ArrayList<>();

            if (charSequence.toString().isEmpty()) {
                Log.i("Searching", "is Empty");
                filteredList.clear();
            } else {
                Log.i("Searching", "Text: " + charSequence.toString());
                for (StuffInfo temp : tempListAll)
                    if (temp.getTitle().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filteredList.add(temp);
                    }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }

        //runs on a ui thread
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            stuff.clear();
            stuff.addAll((Collection<? extends StuffInfo>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    //send message on firebase
    private void sendMessageFirebase(String name, String content, String image, String roomID, String uid, String url){
        FirebaseDatabase firebaseDatabase;
        DatabaseReference roodIdReference;

        Calendar calendar = Calendar.getInstance(); //현재 시간을 가지고 있는 객체
        String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);

        ChatMessageItem messageItem = new ChatMessageItem(name, content, time, image, uid, url);

        firebaseDatabase = FirebaseDatabase.getInstance();
        roodIdReference = firebaseDatabase.getReference(roomID);
        roodIdReference.push().setValue(messageItem);
    }

    //* user exit room
    public void LearveRoomServer(final String roomID){
        class sendData extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                String content = MyData.name + "님이 퇴장 하셨습니다.";
                sendMessageFirebase("ENTER_EXIT", content, "none", roomID, "ENTER_EXIT", "none");
                //Intent intent3 = new Intent(ChattingActivity.this, MainActivity.class);
                //startActivity(intent3);
            }
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject jsonInput = new JSONObject();

                    jsonInput.put("user_email", MyData.mail);
                    jsonInput.put("room_id", roomID);

                    RequestBody reqBody = RequestBody.create(
                            MediaType.parse("application/json; charset=utf-8"),
                            jsonInput.toString()
                    );

                    Request request = new Request.Builder()
                            .delete(reqBody)
                            .url(urls)
                            .build();

                    Response responses = null;
                    responses = client.newCall(request).execute();
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