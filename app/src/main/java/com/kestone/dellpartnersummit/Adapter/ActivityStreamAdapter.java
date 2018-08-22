package com.kestone.dellpartnersummit.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kestone.dellpartnersummit.Activities.GalleryActivity;
import com.kestone.dellpartnersummit.Activities.SpeakerDetails;
import com.kestone.dellpartnersummit.PoJo.UserDetails;
import com.squareup.picasso.Picasso;
import com.kestone.dellpartnersummit.PoJo.StreamData;
import com.kestone.dellpartnersummit.R;

import java.util.ArrayList;

public class ActivityStreamAdapter extends RecyclerView.Adapter<ActivityStreamAdapter.MyHolder> {
    private Context context;
    private ArrayList<StreamData> streamDataList;

    public ActivityStreamAdapter(Context context, ArrayList<StreamData> streamDataList) {
        this.context = context;
        this.streamDataList = streamDataList;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stream_cell, null);
        return new MyHolder(view);

    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

        final StreamData streamData = streamDataList.get(position);
        if(streamData.getBoothCheckin().length()==0){
            holder.mCheckInTv.setVisibility(View.GONE);
        }else {
            holder.mCheckInTv.setVisibility(View.VISIBLE);
            holder.mCheckInTv.setText(streamData.getBoothCheckin());
        }

        if(streamData.getPostedImage().length()==0){
            holder.mActivityIv.setVisibility(View.GONE);
        }else {
            holder.mActivityIv.setVisibility(View.VISIBLE);
            Picasso.with(context).load(streamData.getPostedImage()).into(holder.mActivityIv);
        }

        if(streamData.getPostedText().length()==0){
            holder.mActivityTv.setVisibility(View.GONE);
        }else {
            holder.mActivityTv.setVisibility(View.VISIBLE);
            holder.mActivityTv.setText(streamData.getPostedText());
        }

        //Picasso.with(context).load("").placeholder(R.drawable.ic_user).into(holder.userIv);

        holder.userIv.setImageResource(R.drawable.ic_user);

        if(streamData.getImageURL().length()>0){
            Log.d("Img",position+" " +streamData.getImageURL());
            Picasso.with(context).load(streamData.getImageURL()).placeholder(R.drawable.ic_user).resize(100,100).into(holder.userIv);
        }

        holder.nameTv.setText(streamData.getName());
        holder.timeTv.setText(streamData.getPunchTime()+"\n"+"ago");

        holder.designationTv.setText(streamData.getDesignation()+", "+streamData.getOrganization());

        holder.headingRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!UserDetails.getEmailID().equals(streamData.getEmailID())){
                    Intent intent = new Intent(context, SpeakerDetails.class);
                    intent.putExtra("Name",streamData.getName());
                    intent.putExtra("Designation",streamData.getDesignation());
                    intent.putExtra("Organization",streamData.getOrganization());
                    intent.putExtra("Email",streamData.getEmailID());
                    intent.putExtra("Image",streamData.getImageURL());
                    intent.putExtra("Type","Speaker");
                    intent.putExtra("details","");
                    context.startActivity(intent);
                }
            }
        });
        holder.mActivityIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GalleryActivity.class);
                intent.putExtra("link",streamData.getPostedImage());
                context.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return streamDataList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        private TextView mActivityTv,mCheckInTv,nameTv,designationTv, timeTv;
        private ImageView mActivityIv, userIv;
        private RelativeLayout headingRel;

        public MyHolder(View itemView) {
            super(itemView);

            mActivityIv = (ImageView) itemView.findViewById(R.id.mActivityIv);
            mActivityTv = (TextView) itemView.findViewById(R.id.mActivityTv);
            mCheckInTv = (TextView) itemView.findViewById(R.id.mCheckinTv);
            nameTv = (TextView) itemView.findViewById(R.id.nameTv);
            designationTv = (TextView) itemView.findViewById(R.id.designationTv);
            timeTv = (TextView) itemView.findViewById(R.id.timeTv);
            userIv = (ImageView) itemView.findViewById(R.id.userIv);
            headingRel = (RelativeLayout) itemView.findViewById(R.id.headingRel);

        }
    }

}
