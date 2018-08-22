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

import com.kestone.dellpartnersummit.Adapter.HelpAdapter;
import com.kestone.dellpartnersummit.ApiHandler.ApiUrl;
import com.kestone.dellpartnersummit.ConnectionCheck.ConnectionCheck;
import com.kestone.dellpartnersummit.PoJo.HelpData;
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

public class HelpDeskFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<HelpData> helpList;
    private HelpAdapter helpAdapter;


    public HelpDeskFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help_desk, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        helpList = new ArrayList<>();
        helpAdapter = new HelpAdapter(getContext(), helpList);
        recyclerView.setAdapter(helpAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Progress.showProgress(getContext());
                if(ConnectionCheck.connectionStatus(getActivity())){
                    new GetHelpData(ApiUrl.HelpDesk);
                }else {
                    Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Progress.showProgress(getContext());
        if(ConnectionCheck.connectionStatus(getActivity())){
            new GetHelpData(ApiUrl.HelpDesk);
        }else {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        return view;

    }

    private class GetHelpData {
        private String url;

        public GetHelpData(String url) {
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
                    if (response.isSuccessful()) {

                        String myResponse = response.body().string();
                        Log.d("Stream Response", myResponse);

                        try {
                            JSONArray jsonArray = new JSONArray(myResponse);

                            helpList.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                HelpData helpData = new HelpData();
                                helpData.setID(jsonObject.getString("ID"));
                                helpData.setTitle(jsonObject.getString("Title"));
                                helpData.setName(jsonObject.getString("Name"));
                                helpData.setEmailID(jsonObject.getString("EmailID"));
                                helpData.setPhone(jsonObject.getString("Phone"));
                                helpData.setDesignation(jsonObject.getString("Designation"));
                                helpList.add(helpData);

                            }


                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    helpAdapter.notifyDataSetChanged();

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
