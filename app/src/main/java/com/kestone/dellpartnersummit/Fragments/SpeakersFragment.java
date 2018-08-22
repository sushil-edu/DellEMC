package com.kestone.dellpartnersummit.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.kestone.dellpartnersummit.Adapter.SpeakersAdapter;
import com.kestone.dellpartnersummit.ApiHandler.ApiUrl;
import com.kestone.dellpartnersummit.ConnectionCheck.ConnectionCheck;
import com.kestone.dellpartnersummit.PoJo.SpeakerData;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class SpeakersFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<SpeakerData> speakerList;
    private SpeakersAdapter speakerAdapter;
    private EditText searchEt;
    private String myReponse;
    JSONArray jsonArray;


    public SpeakersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_speakers, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        searchEt = (EditText) view.findViewById(R.id.searchEt);
        searchEt.setTextSize(12f);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        speakerList = new ArrayList<>();
        speakerAdapter = new SpeakersAdapter(getContext(), speakerList);
        recyclerView.setAdapter(speakerAdapter);


        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Progress.showProgress(getContext());
                if (ConnectionCheck.connectionStatus(getActivity())) {

                    speakerList.clear();
                    speakerAdapter.notifyDataSetChanged();

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("UserType","Speaker");
                        jsonObject.put("RefEmailID", UserDetails.EmailID);
                        new SpeakerDataFetch(ApiUrl.Users, jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Progress.closeProgress();
                    Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Progress.showProgress(getContext());
        if (ConnectionCheck.connectionStatus(getActivity())) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("UserType","Speaker");
                jsonObject.put("RefEmailID", UserDetails.EmailID);
                new SpeakerDataFetch(ApiUrl.Users, jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Progress.closeProgress();
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (jsonArray.length() > 0) {
                        populateSearch(myReponse, s.toString());
                    }
                } else if (s.length() == 0) {
                    if (jsonArray.length() > 0) {
                        populateAll(myReponse);
                    }
                }
            }
        });

        return view;
    }

    public class SpeakerDataFetch {

        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        public SpeakerDataFetch(String url, String postBody) {


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
                        speakerList.clear();

                        myReponse = response.body().string();
                        Log.d("Speaker Response", myReponse);
                        populateAll(myReponse);


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

    public void populateAll(String myResponse) {

        speakerList.clear();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                speakerAdapter.notifyDataSetChanged();
            }
        });

        try {
            jsonArray = new JSONArray(myResponse);
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                SpeakerData speakerData = new SpeakerData();
                speakerData.setID(jsonObject.getString("ID"));
                speakerData.setName(jsonObject.getString("Name"));
                speakerData.setDesignation(jsonObject.getString("Designation"));
                speakerData.setOrganization(jsonObject.getString("Organization"));
                speakerData.setEmailID(jsonObject.getString("EmailID"));
                speakerData.setMobile(jsonObject.getString("Mobile"));
                speakerData.setPassportNo(jsonObject.getString("PassportNo"));
                speakerData.setRegistrationType(jsonObject.getString("RegistrationType"));
                speakerData.setImageURL(jsonObject.getString("ImageURL"));
                speakerData.setProfileDesc(jsonObject.getString("ProfileDesc"));

                speakerList.add(speakerData);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                speakerAdapter.notifyDataSetChanged();
            }
        });
    }

    public void populateSearch(String myReponse, String search) {
        speakerList.clear();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                speakerAdapter.notifyDataSetChanged();
            }
        });

        try {
            jsonArray = new JSONArray(myReponse);
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                if (jsonObject.getString("Name").toLowerCase().contains(search.toLowerCase())) {

                    SpeakerData speakerData = new SpeakerData();
                    speakerData.setID(jsonObject.getString("ID"));
                    speakerData.setName(jsonObject.getString("Name"));
                    speakerData.setDesignation(jsonObject.getString("Designation"));
                    speakerData.setOrganization(jsonObject.getString("Organization"));
                    speakerData.setEmailID(jsonObject.getString("EmailID"));
                    speakerData.setMobile(jsonObject.getString("Mobile"));
                    speakerData.setPassportNo(jsonObject.getString("PassportNo"));
                    speakerData.setRegistrationType(jsonObject.getString("RegistrationType"));
                    speakerData.setImageURL(jsonObject.getString("ImageURL"));
                    speakerData.setProfileDesc(jsonObject.getString("ProfileDesc"));

                    speakerList.add(speakerData);


                }

            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    speakerAdapter.notifyDataSetChanged();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
