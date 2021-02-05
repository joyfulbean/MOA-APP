package com.example.moa_cardview.main;

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
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.moa_cardview.R;
import com.example.moa_cardview.chat.ChattingActivity;
import com.example.moa_cardview.chat.ReceiptActivity;
import com.example.moa_cardview.data.MyData;
import com.example.moa_cardview.data.StuffInfo;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    private ArrayList<StuffInfo> stuff = new ArrayList<>();
    private String type;
    private List<StuffInfo> tempListAll;

    // for getting link image
    private String title;
    private String contents;
    private String imageUrl;

    // for is new
    private static final String urls = "http://54.180.8.235:5000/participant";
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
        TextView stuffPrice;
        TextView stuffPeopleNum;
        TextView stuffUrlTitle;
        ImageView stuffImage;
        RelativeLayout expandLayout;
        RelativeLayout previewLayout;
        RelativeLayout postLayout;
        ImageButton enterButton;
        TextView ogTitle;
        TextView ogContent;
        ImageButton threeroundbutton;


        public MyViewHolder(final View itemView) {
            super(itemView);

            //* for displaying screen info
            stuffTitle = (TextView) itemView.findViewById(R.id.post_title_textview);
            stuffDate = (TextView) itemView.findViewById(R.id.post_date_textview);
            stuffPlace = (TextView) itemView.findViewById(R.id.post_place_textview);
            stuffPrice = (TextView) itemView.findViewById(R.id.post_cost_textview);
            stuffPeopleNum = (TextView) itemView.findViewById(R.id.post_pplnumber_textview);
            stuffUrlTitle = (TextView) itemView.findViewById(R.id.post_preview_tv1);
            stuffImage = itemView.findViewById(R.id.post_preview_iv);

            expandLayout = itemView.findViewById(R.id.expandable_layout);

            ogTitle = itemView.findViewById(R.id.post_preview_tv1);
            ogContent = itemView.findViewById(R.id.post_preview_tv2);

            //* link -> web
            previewLayout = itemView.findViewById(R.id.post_preview_layout);
            previewLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openWeb(context, stuff.get(getAdapterPosition()).getStuffLink());
                }
            });

            threeroundbutton = itemView.findViewById(R.id.post_threeroundbutton);
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

            // expand card view when click it
            postLayout = itemView.findViewById(R.id.post_layout);
            postLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StuffInfo items = stuff.get(getAdapterPosition());
                    items.setExpandable(!items.isExpandable());
                    notifyItemChanged(getAdapterPosition());

                }
            });
// 여기서 유저가 새로들어가는 인물인지 아닌지에 대한 정보를 chatting방에 넘겨줘야할 듯...
            // when click the enterButton, enter the room
            enterButton = itemView.findViewById(R.id.post_createbutton);
            enterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    int position = getAdapterPosition();
                    isNewCheckerServer(position);
                }
            });
        }
    }

    public void popupLeave(final int position, View v){
        //AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
        View dialogView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.dialog_leave, null);

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
        /*
        Log.i("Email", "popup report");
        //AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
        final View dialogView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.dialog_report, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext())
                .setPositiveButton("신고하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("Email", "신고하기 clicked");
                        sendEmail(position, dialogView);
                    }
                })
                .setNegativeButton("취소", null)
                .setView(dialogView);
        builder.show();
        */

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
        //sendServer(stuff.get(position).getRoomId());
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
                // 아니면 여기서 추가를해줘도 될 듯 하네
                if(isNew) {
                    Intent intent = new Intent(context, ReceiptActivity.class);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    Log.i("roomID", stuff.get(position).getRoomId());
                    intent.putExtra("test_id", stuff.get(position).getRoomId());
                    context.startActivity(intent);
                }else{
                    Intent intent = new Intent(context, ChattingActivity.class);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    Log.i("roomID", stuff.get(position).getRoomId());
                    intent.putExtra("room_id", stuff.get(position).getRoomId());
                    intent.putExtra("isNew",isNew);
                    context.startActivity(intent);
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

    //* link -> web
    public void openWeb(Context context, String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;

        // Parse the URI and create the intent.
        Uri webPage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webPage);

        // Find an activity to hand the intent and start that activity.
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));
        } else {
            Toast.makeText(context, "Wrong Address. Try again.", Toast.LENGTH_LONG).show();
            Log.d("ImplicitIntents", "Can't handle this intent!");
        }
    }

    //* initial card view info
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.stuffTitle.setText(stuff.get(position).getTitle());
        holder.stuffDate.setText(stuff.get(position).getOrderDate());
        holder.stuffPlace.setText(stuff.get(position).getPlace());
        holder.stuffPrice.setText(stuff.get(position).getStuffCost());
        holder.stuffPeopleNum.setText(stuff.get(position).getNumUsers());
        holder.ogTitle.setText(stuff.get(position).getOgTitle());
        holder.ogContent.setText(stuff.get(position).getStuffLink());


        boolean isExpandable = stuff.get(position).isExpandable();

        if (isExpandable)
            Picasso.get().load(stuff.get(position).getImageUrl()).into(holder.stuffImage);

        holder.expandLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);
        //여기서 이미지를 띄우면 될 듯
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

        /*
    //* user exit room
    public void sendServer(){
        class sendData extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Calendar calendar = Calendar.getInstance(); //현재 시간을 가지고 있는 객체
                String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE); //14:16

                //firebase DB에 저장할 값(MessageItem객체) 설정
                String content = MyData.name + "님이 퇴장 하셨습니다.";
                ChatMessageItem messageItem = new ChatMessageItem("ENTER_EXIT", content, time);
                //'char'노드에 MessageItem객체를 통해 데이터를 저장하기.
                FirebaseDatabase firebaseDatabase;                           //Firebase Database 관리 객체참조변수
                DatabaseReference roodIdReference;
                firebaseDatabase = FirebaseDatabase.getInstance();
                roodIdReference = firebaseDatabase.getReference(roomID);
                roodIdReference.push().setValue(messageItem);

                Intent intent3 = new Intent(ChattingActivity.this, MainActivity.class);
                startActivity(intent3);
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
    */
}