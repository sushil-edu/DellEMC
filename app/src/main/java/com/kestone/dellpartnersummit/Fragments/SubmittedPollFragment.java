package com.kestone.dellpartnersummit.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kestone.dellpartnersummit.Adapter.PollsAdapter;
import com.kestone.dellpartnersummit.ApiHandler.ApiUrl;
import com.kestone.dellpartnersummit.ConnectionCheck.ConnectionCheck;
import com.kestone.dellpartnersummit.PoJo.PollsData;
import com.kestone.dellpartnersummit.ProgressView.Progress;
import com.kestone.dellpartnersummit.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubmittedPollFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private PollsAdapter pollsAdapter;
    private ArrayList<PollsData> pollsList;


    public SubmittedPollFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_submitted_poll, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container,new PollsFragment()).commit();
            }
        });

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pollsList = new ArrayList<>();
        pollsAdapter = new PollsAdapter(getContext(),pollsList);
        recyclerView.setAdapter(pollsAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Progress.showProgress(getContext());
                if(ConnectionCheck.connectionStatus(getActivity())){
                    new GetData(ApiUrl.PollsList);
                }else {
                    Progress.closeProgress();
                    Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Progress.showProgress(getContext());
        if(ConnectionCheck.connectionStatus(getActivity())){
            new GetData(ApiUrl.PollsList);
        }else {
            Progress.closeProgress();
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    public class GetData {
        private String url;

        public GetData(String url) {
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });

                    if(response.isSuccessful()){

                        String myResponse = response.body().string();
                        Log.d("Polls Response", myResponse);
                        pollsList.clear();

                        try {
                            JSONArray jsonArray = new JSONArray(myResponse);
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jObj = jsonArray.getJSONObject(i);
                                PollsData pollsData = new PollsData();
                                pollsData.setID(jObj.getString("ID"));
                                pollsData.setTitle(jObj.getString("Title"));
                                pollsData.setQuestion(jObj.getString("Question"));
                                pollsData.setOption1(jObj.getString("Option1"));
                                pollsData.setOption2(jObj.getString("Option2"));
                                pollsData.setOption3(jObj.getString("Option3"));
                                pollsData.setOption4(jObj.getString("Option4"));
                                pollsData.setIsActive(jObj.getString("IsActive"));
                                pollsList.add(pollsData);
                            }

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pollsAdapter.notifyDataSetChanged();
                                }
                            });


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(),response.message(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

    }


}
