package com.kestone.dellpartnersummit.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kestone.dellpartnersummit.Activities.AgendaDetails;
import com.kestone.dellpartnersummit.ApiHandler.ApiUrl;
import com.kestone.dellpartnersummit.ConnectionCheck.ConnectionCheck;
import com.kestone.dellpartnersummit.PoJo.AgendaData;
import com.kestone.dellpartnersummit.PoJo.NestedSpeakerData;
import com.kestone.dellpartnersummit.PoJo.UserDetails;
import com.kestone.dellpartnersummit.ProgressView.Progress;
import com.kestone.dellpartnersummit.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.MyHolder> {

    private Context context;
    private Activity activity;
    private ArrayList<AgendaData> agendaList;

    public AgendaAdapter(Context context, ArrayList<AgendaData> agendaList) {
        this.context = context;
        this.agendaList = agendaList;
        this.activity = (Activity) context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.agenda_cell, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

        final AgendaData agendaData = agendaList.get(position);
        if (agendaData.getIsMultitrack().equals("N")) {
            holder.addIv.setVisibility(View.GONE);
        } else holder.addIv.setVisibility(View.VISIBLE);

        if (agendaData.getIsRateable().equals("N")) {
            holder.avgRatingBar.setVisibility(View.GONE);
            holder.rateTv.setVisibility(View.GONE);
        } else {
            holder.rateTv.setVisibility(View.VISIBLE);
            holder.avgRatingBar.setVisibility(View.VISIBLE);
        }


        holder.titleTv.setText(agendaData.getTitleTrack());
        holder.timeTv.setText(agendaData.getTime());
        holder.locationTv.setText("Location: "+agendaData.getLocation());
        holder.headingTv.setText(agendaData.getHeading());

        if(agendaData.getRating().length()>0){
            holder.avgRatingBar.setRating(Float.parseFloat(agendaData.getRating()));
        }else {
            holder.avgRatingBar.setRating(Float.parseFloat("0.0"));
        }

        holder.addIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionCheck.connectionStatus(context)) {
                    Progress.showProgress(context);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("RefEmailID", UserDetails.EmailID);
                        jsonObject.put("AgendaID", agendaData.getID());
                        new AddToMyAgenda(ApiUrl.AddMyAgenda, jsonObject.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Progress.closeProgress();
                    }
                } else {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AgendaDetails.class);
                intent.putExtra("title", agendaData.getTitleTrack());
                intent.putExtra("image", "");
                intent.putExtra("description", agendaData.getDescription());
                intent.putExtra("location", agendaData.getLocation());
                intent.putExtra("date", agendaData.getDate());
                intent.putExtra("time", agendaData.getTime());
                intent.putExtra("details", "");
                context.startActivity(intent);
            }
        });

        JSONArray jsonArray = agendaList.get(position).getSpeakers();
        ArrayList<NestedSpeakerData> nestedList = new ArrayList<>();
        nestedList.clear();

        if (jsonArray.length() > 0) {

            for(int i =0; i<jsonArray.length(); i++){

                NestedSpeakerData nestedSpeakerData = new NestedSpeakerData();
                try {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    nestedSpeakerData.setSpeakerName(jsonObject.getString("SpeakerName"));
                    nestedSpeakerData.setSpeakerDesignation(jsonObject.getString("SpeakerDesignation"));
                    nestedSpeakerData.setSpeakerOrganization(jsonObject.getString("SpeakerOrganization"));
                    nestedSpeakerData.setSpeakerImageURL(jsonObject.getString("SpeakerImageURL"));
                    nestedSpeakerData.setSpeakerType(jsonObject.getString("SpeakerType"));
                    nestedSpeakerData.setSpeakerEmailID(jsonObject.getString("SpeakerEmailID"));
                    nestedList.add(nestedSpeakerData);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            NestedAgendaAdapter nestedAgendaAdapter = new NestedAgendaAdapter(context, nestedList);
            holder.nestedReyclerView.setAdapter(nestedAgendaAdapter);
        }else {
            NestedAgendaAdapter nestedAgendaAdapter = new NestedAgendaAdapter(context, nestedList);
            holder.nestedReyclerView.setAdapter(nestedAgendaAdapter);
        }

    }

    @Override
    public int getItemCount() {
        return agendaList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        private TextView addIv;
        private TextView titleTv, timeTv, rateTv,locationTv,headingTv;
        private RatingBar avgRatingBar;
        private CardView card;
        private RecyclerView nestedReyclerView;

        public MyHolder(View itemView) {
            super(itemView);
            addIv = (TextView) itemView.findViewById(R.id.addIv);
            titleTv = (TextView) itemView.findViewById(R.id.titleTv);
            timeTv = (TextView) itemView.findViewById(R.id.timeTv);
            headingTv = (TextView) itemView.findViewById(R.id.headingTv);
            avgRatingBar = (RatingBar) itemView.findViewById(R.id.avgRatingBar);
            card = (CardView) itemView.findViewById(R.id.card);
            rateTv = (TextView) itemView.findViewById(R.id.rateTv);
            nestedReyclerView = (RecyclerView) itemView.findViewById(R.id.nestedReyclerView);
            locationTv = (TextView) itemView.findViewById(R.id.locationTv);


            nestedReyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            nestedReyclerView.setHasFixedSize(true);


            avgRatingBar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        // TODO perform your action here
                        final Dialog dialog = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.alert_rating);
                        Button btnRating = (Button) dialog.findViewById(R.id.btnRating);
                        final EditText txtFeedback = (EditText) dialog.findViewById(R.id.txtFeedback);
                        txtFeedback.setTextSize(12f);
                        final RatingBar mRatingBar = (RatingBar) dialog.findViewById(R.id.mRatingBar);
                        TextView labelTv = (TextView) dialog.findViewById(R.id.labelTv);
                        AgendaData agendaData = agendaList.get(getAdapterPosition());
                        labelTv.setText(agendaData.getTitleTrack());

                        mRatingBar.setRating(avgRatingBar.getRating());
                        btnRating.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AgendaData agendaData = agendaList.get(getAdapterPosition());

                                if (ConnectionCheck.connectionStatus(context)) {
                                    Progress.showProgress(context);
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("RefEmailID", UserDetails.EmailID);
                                        jsonObject.put("AgendaID", agendaData.getID());
                                        jsonObject.put("Rating", mRatingBar.getRating() + "");
                                        jsonObject.put("Feedback", txtFeedback.getText().toString());

                                        Log.d("RateAgendaParams", jsonObject.toString());
                                        new RatemyAgenda(ApiUrl.RateSession, jsonObject.toString(), avgRatingBar, getAdapterPosition(), mRatingBar.getRating());
                                        dialog.cancel();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Progress.closeProgress();
                                    }
                                } else {
                                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                        dialog.show();
                    }
                    return true;
                }
            });


        }
    }


    private class AddToMyAgenda {

        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        private AddToMyAgenda(String url, String postBody) {

            try {
                postRequest(url, postBody);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void postRequest(String postUrl, String postBody) throws IOException {

            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(JSON, postBody);

            Request request = new Request.Builder()
                    .url(postUrl)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    call.cancel();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {

                    Progress.closeProgress();

                    if (response.isSuccessful()) {

                        String myResponse = response.body().string();
                        Log.d("AddToMyAgendaRes", myResponse);

                        try {
                            JSONObject jsonObject = new JSONObject(myResponse);

                            if (jsonObject.getString("retval").equals("Agenda Added successfully")) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Agenda Added successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            });
        }
    }

    private class RatemyAgenda {

        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        private int position;
        private RatingBar ratingBar;
        private float rating;

        private RatemyAgenda(String url, String postBody, RatingBar ratingBar, int position, float rating) {

            try {
                postRequest(url, postBody);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.position = position;
            this.ratingBar = ratingBar;
            this.rating = rating;
        }

        private void postRequest(String postUrl, String postBody) throws IOException {

            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(JSON, postBody);

            Request request = new Request.Builder()
                    .url(postUrl)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    call.cancel();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {

                    Progress.closeProgress();

                    if (response.isSuccessful()) {


                        String myResponse = response.body().string();
                        Log.d("RateMyAgendaRes", myResponse);

                        try {
                            JSONObject jsonObject = new JSONObject(myResponse);

                            if (jsonObject.getString("retval").equals("Session rated successfully")) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Rating Submitted", Toast.LENGTH_SHORT).show();
                                        AgendaData agendaData = agendaList.get(position);
                                        agendaData.setRating(rating + "");
                                        ratingBar.setRating(rating);

                                    }
                                });
                            } else {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Some Problem Occured", Toast.LENGTH_SHORT).show();
                                        AgendaData agendaData = agendaList.get(position);
                                        ratingBar.setRating(Float.parseFloat(agendaData.getRating()));
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            });
        }
    }

}
