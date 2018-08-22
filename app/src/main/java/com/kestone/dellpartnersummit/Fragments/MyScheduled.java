package com.kestone.dellpartnersummit.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kestone.dellpartnersummit.Adapter.MyScheduledAdapter;
import com.kestone.dellpartnersummit.ApiHandler.ApiUrl;
import com.kestone.dellpartnersummit.ConnectionCheck.ConnectionCheck;
import com.kestone.dellpartnersummit.PoJo.MyScheduledData;
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
public class MyScheduled extends Fragment {

    private ArrayList<MyScheduledData> scheduleList;
    private MyScheduledAdapter myScheduledAdapter;

    public MyScheduled() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_scheduled, container, false);

        Fragment fragment = getParentFragment();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        scheduleList = new ArrayList<>();
        myScheduledAdapter = new MyScheduledAdapter(getContext(), scheduleList, fragment);
        recyclerView.setAdapter(myScheduledAdapter);

        Progress.showProgress(getContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("RefEmailID", UserDetails.EmailID);
            jsonObject.put("Status", "Scheduled");
        } catch (JSONException e) {
            e.printStackTrace();
            Progress.closeProgress();
        }

        if (ConnectionCheck.connectionStatus(getContext())) {
            new PostNetwork(ApiUrl.MyMeetings, jsonObject.toString());
        } else {
            Progress.closeProgress();
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }


        return view;
    }

    private class PostNetwork {

        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        private PostNetwork(String url, String postBody) {


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

                        String myRepsonse = response.body().string();
                        Log.d("MyScheduled", myRepsonse);

                        try {
                            JSONArray jsonArray = new JSONArray(myRepsonse);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                MyScheduledData myScheduledData = new MyScheduledData();

                                myScheduledData.setFromEmailID(jsonObject.getString("FromEmailID"));
                                myScheduledData.setToEmailID(jsonObject.getString("ToEmailID"));
                                myScheduledData.setDay(jsonObject.getString("Day"));
                                myScheduledData.setMonth(jsonObject.getString("Month"));
                                myScheduledData.setHours(jsonObject.getString("Hours"));
                                myScheduledData.setMinutes(jsonObject.getString("Minutes"));
                                myScheduledData.setLocation(jsonObject.getString("Location"));
                                myScheduledData.setName(jsonObject.getString("Name"));
                                myScheduledData.setDesignation(jsonObject.getString("Designation"));
                                myScheduledData.setOrganization(jsonObject.getString("Organization"));
                                myScheduledData.setImageUrl(jsonObject.getString("ImageURL"));
                                scheduleList.add(myScheduledData);
                            }

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    myScheduledAdapter.notifyDataSetChanged();
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
