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
import com.kestone.dellpartnersummit.PoJo.DelegatesData;
import com.kestone.dellpartnersummit.PoJo.UserDetails;
import com.kestone.dellpartnersummit.R;

import java.util.ArrayList;

public class DelegatesAdapter extends RecyclerView.Adapter<DelegatesAdapter.MyHolder> {

    private Context context;
    private ArrayList<DelegatesData> delegatesList;

    public DelegatesAdapter(Context context, ArrayList<DelegatesData> delegatesList) {

        this.context = context;
        this.delegatesList = delegatesList;

    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delegates_cell, parent, false);
        return new MyHolder(view);

    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

        final DelegatesData delegatesData = delegatesList.get(position);
        holder.nameTv.setText(delegatesData.getName());
        holder.designationTv.setText(delegatesData.getDesignation());
        holder.organizationTv.setText(delegatesData.getOrganization());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UserDetails.getEmailID().equals(delegatesData.getEmailID())) {

                    Intent intent = new Intent(context, SpeakerDetails.class);
                    intent.putExtra("Name", delegatesData.getName());
                    intent.putExtra("Designation", delegatesData.getDesignation());
                    intent.putExtra("Organization", delegatesData.getOrganization());
                    intent.putExtra("Email", delegatesData.getEmailID());
                    intent.putExtra("Image", delegatesData.getImageURL());
                    intent.putExtra("Type", "Delegate");
                    intent.putExtra("details", delegatesData.getProfileDesc());
                    context.startActivity(intent);
                }
            }
        });

        holder.profileIv.setImageResource(R.drawable.ic_user);

        if (delegatesData.getImageURL().length() > 0) {
            Picasso.with(context).load(delegatesData.getImageURL())
                    .placeholder(R.drawable.ic_user)
                    .resize(100,100).into(holder.profileIv);
        }

    }


    @Override
    public int getItemCount() {
        return delegatesList.size();
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



        }
    }
}
