package com.kestone.dellpartnersummit.Adapter;


import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kestone.dellpartnersummit.Fragments.NetworkScheduleFragment;
import com.kestone.dellpartnersummit.PoJo.UserData;
import com.kestone.dellpartnersummit.R;

import java.util.ArrayList;

public class UserSelectAdapter extends RecyclerView.Adapter<UserSelectAdapter.MyHolder> {


    private ArrayList<UserData> userList;
    private Context context;
    private TextView textView;
    private Dialog dialog;

    public UserSelectAdapter(Context context, TextView textView, ArrayList<UserData> userList, Dialog dialog) {
        this.context = context;
        this.textView = textView;
        this.userList = userList;
        this.dialog = dialog;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_user_cell, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        UserData userData = userList.get(position);
        holder.nameTv.setText(userData.getName());

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        private TextView nameTv;

        public MyHolder(View itemView) {
            super(itemView);
            nameTv = (TextView) itemView.findViewById(R.id.nameTv);
            nameTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserData userData = userList.get(getAdapterPosition());
                    textView.setText(userData.getName());
                    NetworkScheduleFragment.toMailId = userData.getEmailID() + "";
                    dialog.dismiss();
                }
            });

        }
    }
}
