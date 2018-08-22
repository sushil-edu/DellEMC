package com.kestone.dellpartnersummit.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;
import com.kestone.dellpartnersummit.Activities.SpeakerDetails;
import com.kestone.dellpartnersummit.PoJo.SpeakerData;
import com.kestone.dellpartnersummit.PoJo.UserDetails;
import com.kestone.dellpartnersummit.R;

import java.util.ArrayList;


public class SpeakersAdapter extends RecyclerView.Adapter<SpeakersAdapter.MyHolder> {

    private Context context;
    private ArrayList<SpeakerData> speakerList;

    public SpeakersAdapter(Context context, ArrayList<SpeakerData> speakerList) {

        this.speakerList = speakerList;
        this.context = context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.speaker_cell, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        final SpeakerData speakerData = speakerList.get(position);
        holder.nameTv.setText(speakerData.getName());
        holder.designationTv.setText(speakerData.getDesignation());
        holder.organizationTv.setText(speakerData.getOrganization());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!UserDetails.getEmailID().equals(speakerData.getEmailID())){
                    Intent intent = new Intent(context, SpeakerDetails.class);
                    intent.putExtra("Name",speakerData.getName());
                    intent.putExtra("Designation",speakerData.getDesignation());
                    intent.putExtra("Organization",speakerData.getOrganization());
                    intent.putExtra("Email",speakerData.getEmailID());
                    intent.putExtra("Image",speakerData.getImageURL());
                    intent.putExtra("Type","Speaker");
                    intent.putExtra("details",speakerData.getProfileDesc());
                    context.startActivity(intent);
                }
            }
        });
        holder.profileIv.setImageResource(R.drawable.ic_user);


        if (speakerData.getImageURL().length() > 0) {
           // Bitmap bitmap = (Bitmap) holder.profileIv.getTag();
            {
                //if (bitmap != null ||bitmap.isRecycled()) {
                    Picasso.with(context).load(speakerData.getImageURL()).resize(80,80)
                            .placeholder(R.drawable.ic_user)
                            .into(holder.profileIv);

                    //android:src="@drawable/ic_user"
               // }
           }
        }
    }

    @Override
    public int getItemCount() {
        return speakerList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView nameTv, organizationTv, designationTv;
        CircularImageView profileIv;

        public MyHolder(View itemView) {
            super(itemView);


            nameTv = (TextView) itemView.findViewById(R.id.nameTv);
            organizationTv = (TextView) itemView.findViewById(R.id.organizationTv);
            designationTv = (TextView) itemView.findViewById(R.id.designationTv);
            profileIv = (CircularImageView) itemView.findViewById(R.id.profileIv);

//            mSpeakerRv.setOnRippleCompleteListener(new AwesomeRippleView.OnRippleCompleteListener() {
//                @Override
//                public void onComplete(AwesomeRippleView awesomeRippleView) {
//                    Intent intent = new Intent(context, SpeakerDetails.class);
//                    context.startActivity(intent);
//                }
//            });



        }
    }
}
