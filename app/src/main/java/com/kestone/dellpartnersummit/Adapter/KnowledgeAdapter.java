package com.kestone.dellpartnersummit.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kestone.dellpartnersummit.PoJo.KnowledgeData;
import com.kestone.dellpartnersummit.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class KnowledgeAdapter extends RecyclerView.Adapter<KnowledgeAdapter.MyHolder> {

    private Context context;
    private ArrayList<KnowledgeData> knowledgeList;

    public KnowledgeAdapter(Context context, ArrayList<KnowledgeData> knowledgeList){
        this.context = context;
        this.knowledgeList = knowledgeList;
    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.knowledge_cell,parent,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

        final KnowledgeData knowledgeData = knowledgeList.get(position);
        holder.titleTv.setText(knowledgeData.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, KnowledgeActivity.class);
//                intent.putExtra("title",knowledgeData.getTitle());
//                intent.putExtra("Url",knowledgeData.getLinkedURL());
//                context.startActivity(intent);

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(knowledgeData.getLinkedURL()));
                context.startActivity(browserIntent);

            }
        });

        if(knowledgeData.getDocumentType().length()>0){
            Picasso.with(context).load(knowledgeData.getDocumentType()).into(holder.iconIv);
        }

    }

    @Override
    public int getItemCount() {
        return knowledgeList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        private TextView titleTv;
        private ImageView iconIv;

        public MyHolder(View itemView) {
            super(itemView);

            this.titleTv = (TextView) itemView.findViewById(R.id.titleTv);
            this.iconIv = (ImageView) itemView.findViewById(R.id.iconIv);
        }
    }
}
