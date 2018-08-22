package com.kestone.dellpartnersummit.Fragments;

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
import android.widget.Toast;

import com.kestone.dellpartnersummit.Adapter.PersonalAgendaAdapter;
import com.kestone.dellpartnersummit.ApiHandler.ApiUrl;
import com.kestone.dellpartnersummit.ConnectionCheck.ConnectionCheck;
import com.kestone.dellpartnersummit.PoJo.MyAgendaData;
import com.kestone.dellpartnersummit.PoJo.UserDetails;
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

public class PersonalAgendaFragment extends Fragment {

    private PersonalAgendaAdapter personalAgendaAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<MyAgendaData> myAgendaList;

    public PersonalAgendaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personal_agenda, container, false);


        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        myAgendaList = new ArrayList<>();
        personalAgendaAdapter = new PersonalAgendaAdapter(getContext(),myAgendaList);
        recyclerView.setAdapter(personalAgendaAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (ConnectionCheck.connectionStatus(getContext())) {
                    //Progress.showProgress(getContext());
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("EmailID", UserDetails.EmailID);

                        Log.d("PersonalAgendaParams", ApiUrl.MyAgenda + " " + jsonObject.toString());
                        new PersonalAgendaFetch(ApiUrl.MyAgenda, jsonObject.toString());


                    } catch (JSONException e) {
                        e.printStackTrace();
                        //Progress.closeProgress();
                    }
                } else {
                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (ConnectionCheck.connectionStatus(getContext())) {
            //Progress.showProgress(getContext());
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("EmailID", UserDetails.EmailID);
                Log.d("PersonalAgendaParams", ApiUrl.MyAgenda + " " + jsonObject.toString());
                new PersonalAgendaFetch(ApiUrl.MyAgenda, jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
                //Progress.closeProgress();
            }
        } else {
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
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

                    //Progress.closeProgress();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });

                    if (response.isSuccessful()) {

                        String myResponse = response.body().string();
                        Log.d("PersonalAgendaRes", myResponse);

                        myAgendaList.clear();
                        try {
                            JSONArray jsonArray = new JSONArray(myResponse);

                            for(int i = 0; i<jsonArray.length();i++){

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                MyAgendaData agendaData = new MyAgendaData();
                                agendaData.setID(jsonObject.getString("ID"));
                                agendaData.setDate(jsonObject.getString("Date"));
                                agendaData.setTime(jsonObject.getString("Time"));
                                agendaData.setTitleTrack(jsonObject.getString("TitleTrack"));
                                agendaData.setIsMultitrack(jsonObject.getString("IsMultitrack"));
                                agendaData.setGroupingID(jsonObject.getString("GroupingID"));
                                agendaData.setDescription(jsonObject.getString("Description"));
                                agendaData.setIsRateable(jsonObject.getString("IsRateable"));
                                agendaData.setRating(jsonObject.getString("Rating"));
                                agendaData.setSpeakers(jsonObject.getJSONArray("Speakers"));
//                                agendaData.setSpeakerName(jsonObject.getString("SpeakerName"));
//                                agendaData.setSpeakerDesignation(jsonObject.getString("SpeakerDesignation"));
//                                agendaData.setSpeakerOrganization(jsonObject.getString("SpeakerOrganization"));
                                agendaData.setRefAgendaId(jsonObject.getString("RefAgendaID"));
                                agendaData.setLocation(jsonObject.getString("Location"));
                                agendaData.setHeading(jsonObject.getString("Heading"));
                               // agendaData.setImageURL(jsonObject.getString("SpeakerImageURL"));

                                myAgendaList.add(agendaData);
                            }

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    personalAgendaAdapter.notifyDataSetChanged();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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


}
