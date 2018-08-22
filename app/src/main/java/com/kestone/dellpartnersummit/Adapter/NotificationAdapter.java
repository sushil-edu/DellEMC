package com.kestone.dellpartnersummit.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kestone.dellpartnersummit.R;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyHolder> {
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_cell,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return 10;
    }

     class MyHolder extends RecyclerView.ViewHolder {

        private MyHolder(View itemView) {
            super(itemView);
        }
    }
}
