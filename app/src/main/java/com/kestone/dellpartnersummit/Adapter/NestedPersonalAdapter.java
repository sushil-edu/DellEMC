package com.kestone.dellpartnersummit.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kestone.dellpartnersummit.Activities.SpeakerDetails;
import com.kestone.dellpartnersummit.PoJo.NestedPersonalData;
import com.kestone.dellpartnersummit.R;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.util.ArrayList;

public class NestedPersonalAdapter extends RecyclerView.Adapter<NestedPersonalAdapter.MyHolder> {

    private Context context;
    private JSONArray jsonArray;
    private ArrayList<NestedPersonalData> nestedList;

    public NestedPersonalAdapter(Context context, ArrayList<NestedPersonalData> nestedList) {

        this.context = context;
        this.nestedList = nestedList;

    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nested_agenda_cell, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

        final NestedPersonalData nestedSpeakerData = nestedList.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SpeakerDetails.class);
                intent.putExtra("Name", nestedSpeakerData.getSpeakerName());
                intent.putExtra("Designation", nestedSpeakerData.getSpeakerDesignation());
                intent.putExtra("Organization", nestedSpeakerData.getSpeakerOrganization());
                intent.putExtra("Email", nestedSpeakerData.getSpeakerEmailID());
                intent.putExtra("Image", nestedSpeakerData.getSpeakerImageURL());
                intent.putExtra("Type", nestedSpeakerData.getSpeakerType());
                intent.putExtra("details", "");
                context.startActivity(intent);
            }
        });

        holder.typeTv.setText(nestedSpeakerData.getSpeakerType());
        holder.nameTv.setText(nestedSpeakerData.getSpeakerName());
        holder.designationTv.setText(nestedSpeakerData.getSpeakerDesignation());
        holder.organisationTv.setText(nestedSpeakerData.getSpeakerOrganization());

        if(nestedSpeakerData.getSpeakerImageURL().length()>0){
            Picasso.with(context).load(nestedSpeakerData.getSpeakerImageURL())
                    .resize(80,80)
                    .placeholder(R.drawable.ic_user)
                    .into(holder.profileIv);

        }
    }

    @Override
    public int getItemCount() {
        return nestedList.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {

        private TextView typeTv, nameTv, designationTv, organisationTv;
        private CircularImageView profileIv;

        public MyHolder(View itemView) {
            super(itemView);

            typeTv = (TextView) itemView.findViewById(R.id.typeTv);
            nameTv = (TextView) itemView.findViewById(R.id.nameTv);
            designationTv = (TextView) itemView.findViewById(R.id.designationTv);
            organisationTv = (TextView) itemView.findViewById(R.id.organisationTv);
            profileIv = (CircularImageView) itemView.findViewById(R.id.profileIv);

        }
    }


}
