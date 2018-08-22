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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kestone.dellpartnersummit.Activities.AgendaDetails;
import com.kestone.dellpartnersummit.Activities.CheckIn;
import com.kestone.dellpartnersummit.ApiHandler.ApiUrl;
import com.kestone.dellpartnersummit.ConnectionCheck.ConnectionCheck;
import com.kestone.dellpartnersummit.PoJo.MyAgendaData;
import com.kestone.dellpartnersummit.PoJo.NestedPersonalData;
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


public class PersonalAgendaAdapter extends RecyclerView.Adapter<PersonalAgendaAdapter.MyHolder> {

    private Context context;
    private ArrayList<MyAgendaData> myAgendaList;
    private Activity activity;

    public PersonalAgendaAdapter(Context context, ArrayList<MyAgendaData> myAgendaList) {
        this.context = context;
        this.myAgendaList = myAgendaList;
        this.activity = (Activity) context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.personal_agenda_cell, parent, false);
        //LinearLayout checkInLl = (LinearLayout) view.findViewById(R.id.checkInLl);


        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {

        final MyAgendaData agendaData = myAgendaList.get(position);

        if (agendaData.getIsRateable().equals("N")) {
            holder.ratingBar.setVisibility(View.GONE);
        } else holder.ratingBar.setVisibility(View.VISIBLE);

        holder.titleTv.setText(agendaData.getTitleTrack());
        holder.timeTv.setText(agendaData.getTime());
        holder.ratingBar.setRating(Float.parseFloat(agendaData.getRating()));
        holder.locationTv.setText("Location: "+ agendaData.getLocation());
        holder.headingTv.setText(agendaData.getHeading());

        holder.deleteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionCheck.connectionStatus(context)) {
                    Progress.showProgress(context);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("RefEmailID", UserDetails.EmailID);
                        jsonObject.put("AgendaID", agendaData.getRefAgendaId());

                        Log.d("DeleteAgendaParams", jsonObject.toString());
                        new DeleteFromyAgenda(ApiUrl.RemoveMyAgenda, jsonObject.toString(), position);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Progress.closeProgress();
                    }
                } else {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        holder.checkInLl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, CheckIn.class);
//                context.startActivity(intent);
//            }
//        });

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AgendaDetails.class);
                intent.putExtra("title", agendaData.getTitleTrack());
                intent.putExtra("image", "");
                intent.putExtra("date", agendaData.getDate());
                intent.putExtra("time", agendaData.getTime());
                intent.putExtra("description", agendaData.getDescription());
                intent.putExtra("location", agendaData.getLocation());
                intent.putExtra("details", "");
                context.startActivity(intent);
            }
        });


        JSONArray jsonArray = myAgendaList.get(position).getSpeakers();
        ArrayList<NestedPersonalData> nestedList = new ArrayList<>();
        nestedList.clear();

        if (jsonArray.length() > 0) {

            for (int i = 0; i < jsonArray.length(); i++) {

                NestedPersonalData nestedSpeakerData = new NestedPersonalData();
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    nestedSpeakerData.setSpeakerName(jsonObject.getString("SpeakerName"));
                    nestedSpeakerData.setSpeakerDesignation(jsonObject.getString("SpeakerDesignation"));
                    nestedSpeakerData.setSpeakerOrganization(jsonObject.getString("SpeakerOrganization"));
                    nestedSpeakerData.setSpeakerImageURL(jsonObject.getString("SpeakerImageURL"));
                    nestedSpeakerData.setSpeakerType(jsonObject.getString("SpeakerType"));
                    nestedList.add(nestedSpeakerData);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            NestedPersonalAdapter nestedPersonalAdapter = new NestedPersonalAdapter(context, nestedList);
            holder.nestedReyclerView.setAdapter(nestedPersonalAdapter);

        }else {
            NestedPersonalAdapter nestedPersonalAdapter = new NestedPersonalAdapter(context, nestedList);
            holder.nestedReyclerView.setAdapter(nestedPersonalAdapter);
        }

    }


    @Override
    public int getItemCount() {
        return myAgendaList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        private TextView timeTv, titleTv,locationTv,headingTv;
        private RatingBar ratingBar;
        //private LinearLayout checkInLl;
        private ImageView deleteIv;
        private CardView card;
        private RecyclerView nestedReyclerView;

        public MyHolder(final View itemView) {
            super(itemView);

            timeTv = (TextView) itemView.findViewById(R.id.timeTv);
            titleTv = (TextView) itemView.findViewById(R.id.titleTv);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            headingTv = (TextView) itemView.findViewById(R.id.headingTv);
            //checkInLl = (LinearLayout) itemView.findViewById(R.id.checkInLl);
            locationTv = (TextView) itemView.findViewById(R.id.locationTv);
            deleteIv = (ImageView) itemView.findViewById(R.id.deleteIv);
            card = (CardView) itemView.findViewById(R.id.card);
            nestedReyclerView = (RecyclerView) itemView.findViewById(R.id.nestedReyclerView);
            nestedReyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            nestedReyclerView.setHasFixedSize(true);


            ratingBar.setOnTouchListener(new View.OnTouchListener() {
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
                        final MyAgendaData agendaData = myAgendaList.get(getAdapterPosition());
                        labelTv.setText(agendaData.getTitleTrack());

                        mRatingBar.setRating(ratingBar.getRating());
                        btnRating.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                if (ConnectionCheck.connectionStatus(context)) {
                                    Progress.showProgress(context);
                                    JSONObject jsonObject = new JSONObject();
                                    try {

                                        jsonObject.put("RefEmailID", UserDetails.EmailID);
                                        jsonObject.put("AgendaID", agendaData.getRefAgendaId());
                                        jsonObject.put("Rating", mRatingBar.getRating() + "");
                                        jsonObject.put("Feedback", txtFeedback.getText().toString());

                                        Log.d("RateAgendaParams", jsonObject.toString());
                                        new RatemyAgenda(ApiUrl.RateSession, jsonObject.toString(), ratingBar, getAdapterPosition(), mRatingBar.getRating());
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

    private class DeleteFromyAgenda {

        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        private int position;

        private DeleteFromyAgenda(String url, String postBody, int position) {

            try {
                postRequest(url, postBody);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.position = position;
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
                        Log.d("DeleteFromMyAgendaRes", myResponse);

                        try {
                            JSONObject jsonObject = new JSONObject(myResponse);

                            if (jsonObject.getString("retval").equals("Agenda removed successfully")) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Agenda removed successfully", Toast.LENGTH_SHORT).show();
                                        myAgendaList.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, myAgendaList.size());

                                    }
                                });
                            } else {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Not Deleted", Toast.LENGTH_SHORT).show();
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
                                        MyAgendaData agendaData = myAgendaList.get(position);
                                        agendaData.setRating(rating + "");
                                        ratingBar.setRating(rating);

                                    }
                                });
                            } else {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Some Problem Occured", Toast.LENGTH_SHORT).show();
                                        MyAgendaData agendaData = myAgendaList.get(position);
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

