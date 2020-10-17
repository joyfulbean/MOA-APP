package com.example.moa_cardview.item_page;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.moa_cardview.R;
import com.example.moa_cardview.chat.ChattingActivity;
import com.example.moa_cardview.data.StuffInfo;
import com.squareup.picasso.Picasso;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


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

            // when click the enterButton, enter the room
            enterButton = itemView.findViewById(R.id.post_createbutton);
            enterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    Intent intent = new Intent(enterButton.getContext(), ChattingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    int position = getAdapterPosition();
                    intent.putExtra("test_id", stuff.get(position).getRoomId());

                    enterButton.getContext().startActivity(intent);
                }
            });
        }
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
            context.startActivity(intent);
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
}