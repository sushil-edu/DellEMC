package com.kestone.dellpartnersummit.Fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.kestone.dellpartnersummit.Adapter.AgendaAdapter;
import com.kestone.dellpartnersummit.ApiHandler.ApiUrl;
import com.kestone.dellpartnersummit.ConnectionCheck.ConnectionCheck;
import com.kestone.dellpartnersummit.PoJo.AgendaData;
import com.kestone.dellpartnersummit.PoJo.UserDetails;
import com.kestone.dellpartnersummit.ProgressView.Progress;
import com.kestone.dellpartnersummit.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class GlobalAgendaFragment extends Fragment {

    private ArrayList<AgendaData> agendaList;
    private ArrayList<String> trackList = new ArrayList<>();
    private AgendaAdapter agendaAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView trackTv;
    private Dialog dialog;
    private String track = "";
    String mainRes;

    public GlobalAgendaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_global_agenda, container, false);

        trackTv = (TextView) view.findViewById(R.id.trackTv);

        trackTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                populateAlert();
            }
        });

        if (UserDetails.getIsTrackApplicable().equals("Y")) {
            trackTv.setVisibility(View.VISIBLE);
        } else {
            trackTv.setVisibility(View.GONE);
        }

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        agendaList = new ArrayList<>();
        agendaAdapter = new AgendaAdapter(getContext(), agendaList);
        recyclerView.setAdapter(agendaAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Progress.showProgress(getContext());
                if (ConnectionCheck.connectionStatus(getActivity())) {
                    new GetTracksData(ApiUrl.EventDates);
                } else {
                    Progress.closeProgress();
                    Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        });

        Progress.showProgress(getContext());
        if (ConnectionCheck.connectionStatus(getActivity())) {
            new GetTracksData(ApiUrl.EventDates);
        } else {
            Progress.closeProgress();
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }


        return view;
    }

    private class PersonalAgendaFetch {

        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        private PersonalAgendaFetch(String url, String postBody) {

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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });

                    if (response.isSuccessful()) {

                        agendaList.clear();

                        mainRes = response.body().string();
                        Log.d("AgendaRsponse", mainRes);

                      getActivity().runOnUiThread(new Runnable() {
                          @Override
                          public void run() {

//                              if(UserDetails.getIsTrackApplicable().equals("Y")){

                              String month = new DateFormatSymbols().getMonths()[Integer.parseInt(trackList.get(0).substring(5, 7))-1];


                              String [] strArray = trackList.get(0).split("-");
                              StringBuilder sb = new StringBuilder();

                              for(int i=strArray.length-1;i>-1;i--)
                              {
                                  if(i==2){
                                      sb.append(strArray[i]+"-");
                                  }else if(i==1){
                                      sb.append(month.substring(0,3)+"-");
                                  }
                                  else {
                                      sb.append(strArray[i]);
                                  }
                              }

                              trackTv.setText(sb);
                                  populateTrackAgenda(mainRes,trackList.get(0) );
//                              }else {
//                                  populateAgenda(mainRes);
//                              }

                          }
                      });

                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            });
        }

    }

    private class GetTracksData {
        private String url;

        private GetTracksData(String url) {
            this.url = url;

            try {
                run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void run() throws IOException {

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
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
                        trackList.clear();

                        String myResponse = response.body().string();
                        Log.d("Track Response", myResponse);
                        try {
                            JSONArray jsonArray = new JSONArray(myResponse);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                trackList.add(jsonObject.getString("Date"));
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Progress.showProgress(getContext());

                                if (ConnectionCheck.connectionStatus(getContext())) {
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("EmailID", UserDetails.EmailID);
                                        new PersonalAgendaFetch(ApiUrl.GeneralAgenda, jsonObject.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Progress.closeProgress();
                                    }

                                } else {
                                    Progress.closeProgress();
                                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });


                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                }
            });
        }

    }


    public void populateAlert() {
        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_user_type);
        TextView headTv = (TextView) dialog.findViewById(R.id.headTv);
        headTv.setText("Select Date");

        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new TrackAdapter());


        dialog.show();
    }


    public void populateAgenda(String myResponse){

        agendaList.clear();

        try {
            JSONArray jsonArray = new JSONArray(myResponse);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                AgendaData agendaData = new AgendaData();
                agendaData.setID(jsonObject.getString("ID"));
                agendaData.setDate(jsonObject.getString("Date"));
                agendaData.setTime(jsonObject.getString("Time"));
                agendaData.setTitleTrack(jsonObject.getString("TitleTrack"));
                agendaData.setIsMultitrack(jsonObject.getString("IsMultitrack"));
                agendaData.setGroupingID(jsonObject.getString("GroupingID"));
                agendaData.setDescription(jsonObject.getString("Description"));
                agendaData.setIsRateable(jsonObject.getString("IsRateable"));
                agendaData.setRating(jsonObject.getString("Rating"));
                agendaData.setRefAgendaID(jsonObject.getString("RefAgendaID"));
                agendaData.setLocation(jsonObject.getString("Location"));
                agendaData.setSpeakers(jsonObject.getJSONArray("Speakers"));
                agendaData.setTrack(jsonObject.getString("Track"));
                agendaData.setHeading(jsonObject.getString("Heading"));

                agendaList.add(agendaData);

            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    agendaAdapter.notifyDataSetChanged();

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void populateTrackAgenda(String myResponse, String track){
        agendaList.clear();




        try {
            JSONArray jsonArray = new JSONArray(myResponse);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(jsonObject.getString("Date").equals(track)){

                    AgendaData agendaData = new AgendaData();
                    agendaData.setID(jsonObject.getString("ID"));
                    agendaData.setDate(jsonObject.getString("Date"));
                    agendaData.setTime(jsonObject.getString("Time"));
                    agendaData.setTitleTrack(jsonObject.getString("TitleTrack"));
                    agendaData.setIsMultitrack(jsonObject.getString("IsMultitrack"));
                    agendaData.setGroupingID(jsonObject.getString("GroupingID"));
                    agendaData.setDescription(jsonObject.getString("Description"));
                    agendaData.setIsRateable(jsonObject.getString("IsRateable"));
                    agendaData.setRating(jsonObject.getString("Rating"));
                    agendaData.setRefAgendaID(jsonObject.getString("RefAgendaID"));
                    agendaData.setLocation(jsonObject.getString("Location"));
                    agendaData.setSpeakers(jsonObject.getJSONArray("Speakers"));
                    agendaData.setTrack(jsonObject.getString("Track"));
                    agendaData.setHeading(jsonObject.getString("Heading"));

                    agendaList.add(agendaData);


                }

            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    agendaAdapter.notifyDataSetChanged();

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.MyHolder> {

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_user_cell, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {


            String month = new DateFormatSymbols().getMonths()[Integer.parseInt(trackList.get(position).substring(5, 7))-1];


            String [] strArray = trackList.get(position).split("-");
            StringBuilder sb = new StringBuilder();
            for(int i=strArray.length-1;i>-1;i--)
            {
                if(i==2){
                    sb.append(strArray[i]+"-");
                }else if(i==1){
                    sb.append(month.substring(0,3)+"-");
                }
                else {
                    sb.append(strArray[i]);
                }
            }


            holder.nameTv.setText(sb);
        }

        @Override
        public int getItemCount() {
            return trackList.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {

            private TextView nameTv;

            public MyHolder(View itemView) {
                super(itemView);
                nameTv = (TextView) itemView.findViewById(R.id.nameTv);
                nameTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        populateTrackAgenda(mainRes,trackList.get(getAdapterPosition()));
                        dialog.cancel();

                        String month = new DateFormatSymbols().getMonths()[Integer.parseInt(trackList.get(getAdapterPosition()).substring(5, 7))-1];


                        String [] strArray = trackList.get(getAdapterPosition()).split("-");
                        StringBuilder sb = new StringBuilder();
                        for(int i=strArray.length-1;i>-1;i--)
                        {
                            if(i==2){
                                sb.append(strArray[i]+"-");
                            }else if(i==1){
                                sb.append(month.substring(0,3)+"-");
                            }
                            else {
                                sb.append(strArray[i]);
                            }
                        }

                        trackTv.setText(sb);
                        track = trackList.get(getAdapterPosition());



                    }
                });

            }
        }
    }

}
