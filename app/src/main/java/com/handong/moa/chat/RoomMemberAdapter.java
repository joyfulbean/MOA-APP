package com.handong.moa.chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.handong.moa.R;
import com.handong.moa.data.RoomMemberData;

import java.util.ArrayList;
import java.util.List;

public class RoomMemberAdapter extends ArrayAdapter<RoomMemberData> {
    private Context context;
    private List<RoomMemberData> rRoomMembers = new ArrayList<>();
    private LayoutInflater layoutInflater = null;

    RoomMemberAdapter (Context c, List<RoomMemberData> roomMembers, LayoutInflater layoutInflater) {
        super(c, R.layout.chatpage_ppl_row, R.id.chatpage_pplname_tv, roomMembers);
        this.context = c;
        this.rRoomMembers = roomMembers;
//        this.layoutInflater = LayoutInflater.from(context);;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = layoutInflater.inflate(R.layout.chatpage_ppl_row, parent, false);
        ImageView images = row.findViewById(R.id.chatpage_otherprofile_iv);
        TextView name = row.findViewById(R.id.chatpage_pplname_tv);
        ImageButton phone = row.findViewById(R.id.chatpage_phone_button);
        ImageButton receipt = row.findViewById(R.id.chatpage_receipt_button);

        //setting resources on views
        images.setImageResource( R.drawable.profileicon2);
        name.setText(rRoomMembers.get(position).getName());

        if(rRoomMembers.get(position).getPhonNumber().equals("null") || rRoomMembers.get(position).getPhonNumber().isEmpty()) {
            Log.i("phone", "null input");
            phone.setVisibility(View.GONE);
        }else {
            Log.i("phone", rRoomMembers.get(position).getPhonNumber());
        }

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callFunction(rRoomMembers.get(position).getPhonNumber());
            }
        });

        return row;
    }

    //* for call
    public void callFunction(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:/" + phoneNum));

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "Wrong Number. Try again.", Toast.LENGTH_LONG).show();
            Log.d("ImplicitIntents", "Can't handle this intent!");
        }
    }
}
