package com.example.moa_cardview.chat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moa_cardview.R;

public class ShowReceiptEachInfoDialog extends Dialog implements View.OnClickListener{

    public ShowReceiptEachInfoDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public ShowReceiptEachInfoDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    public Activity activity;
    public Dialog dialog;
    RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter recyclerAdapter;
    ImageButton popupCloseButton;



    public ShowReceiptEachInfoDialog(Activity a, RecyclerView.Adapter recyclerAdapter) {
        super(a);
        this.activity = a;
        this.recyclerAdapter = recyclerAdapter;
        setupLayout();
    }

    private void setupLayout() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.individual_receipt_popup);
        recyclerView = findViewById(R.id.individualreceipt_popup_recyclerview);
        mLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.notifyDataSetChanged();
        //individualreceipt_popup_wholecost
//        popupCloseButton = (ImageButton) findViewById(R.id.individualreceipt_popup_closebutton);
//        popupCloseButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dismiss();
//            }
//        });
    }

    @Override
    public void onClick(View view) {

    }
}
