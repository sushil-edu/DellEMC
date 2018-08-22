package com.kestone.dellpartnersummit.Fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kestone.dellpartnersummit.Adapter.KnowledgeAdapter;
import com.kestone.dellpartnersummit.ApiHandler.ApiUrl;
import com.kestone.dellpartnersummit.ConnectionCheck.ConnectionCheck;
import com.kestone.dellpartnersummit.PoJo.KnowledgeData;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class KnowledgeBankFragment extends Fragment {
    private Spinner spinner;
    private ArrayList<String> trackList = new ArrayList<>();
    private ArrayList<KnowledgeData> knowledgeList = new ArrayList<>();
    private String track = "";
    private String knowledegeResponse;
    private KnowledgeAdapter knowledgeAdapter;
    private TextView trackTv;
    private Dialog dialog;


    public KnowledgeBankFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_knowledge_bank, container, false);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        trackTv = (TextView) view.findViewById(R.id.trackTv);

        RelativeLayout topLl = (RelativeLayout) view.findViewById(R.id.topLl);

        if (UserDetails.getUserType().equals("CIO")) {
            topLl.setVisibility(View.GONE);

            Progress.showProgress(getContext());
            if (ConnectionCheck.connectionStatus(getActivity())) {
                new GetKnowledge(ApiUrl.KnowledgeBank);
            } else {
                Progress.closeProgress();
                Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }

        } else {
            topLl.setVisibility(View.VISIBLE);

            Progress.showProgress(getContext());
            if (ConnectionCheck.connectionStatus(getActivity())) {
                new GetTracksData(ApiUrl.Tracks);
            } else {
                Progress.closeProgress();
                Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        knowledgeAdapter = new KnowledgeAdapter(getContext(), knowledgeList);
        recyclerView.setAdapter(knowledgeAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    knowledgeList.clear();
                    knowledgeAdapter.notifyDataSetChanged();
                } else {
                    knowledgeList.clear();
                    try {
                        JSONArray jsonArray = new JSONArray(knowledegeResponse);
                        for (int i = 0; i < jsonArray.length(); i++) {

                            KnowledgeData knowledgeData = new KnowledgeData();
                            JSONObject jObj = jsonArray.getJSONObject(i);
                            if (jObj.getString("Track").equals(trackList.get(position))) {
                                knowledgeData.setDocumentType(jObj.getString("DocumentType"));
                                knowledgeData.setLinkedURL(jObj.getString("LinkedURL"));
                                knowledgeData.setTitle(jObj.getString("Title"));
                                knowledgeData.setTrack(jObj.getString("Track"));

                                Log.d("Track", jObj.getString("Track"));

                                knowledgeList.add(knowledgeData);
                            }
                        }

                        knowledgeAdapter.notifyDataSetChanged();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        trackTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(trackList.size()>0){
                    populateAlert();
                }else
                {
                    Toast.makeText(getContext(), "No Tracks Available", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
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

                        String myResponse = response.body().string();
                        Log.d("Track Response", myResponse);
                        try {
                            JSONArray jsonArray = new JSONArray(myResponse);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                trackList.add(jsonObject.getString("TrackName"));
                            }

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.spinner_center_row, trackList);
                                    spinner.setAdapter(adapter);
                                    Progress.showProgress(getContext());
                                    if (ConnectionCheck.connectionStatus(getActivity())) {
                                        new GetKnowledgeData(ApiUrl.KnowledgeBank);
                                    } else {
                                        Progress.closeProgress();
                                        Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                    }
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

    private class GetKnowledgeData {
        private String url;

        private GetKnowledgeData(String url) {
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

                        knowledegeResponse = response.body().string();
                        Log.d("Knowledge Response", knowledegeResponse);


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

    private class GetKnowledge {
        private String url;

        private GetKnowledge(String url) {
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

                        knowledegeResponse = response.body().string();
                        Log.d("Knowledge Response", knowledegeResponse);

                        try {
                            JSONArray jsonArray = new JSONArray(knowledegeResponse);
                            for (int i = 0; i < jsonArray.length(); i++) {

                                KnowledgeData knowledgeData = new KnowledgeData();
                                JSONObject jObj = jsonArray.getJSONObject(i);

                                knowledgeData.setDocumentType(jObj.getString("DocumentType"));
                                knowledgeData.setLinkedURL(jObj.getString("LinkedURL"));
                                knowledgeData.setTitle(jObj.getString("Title"));
                                knowledgeData.setTrack(jObj.getString("Track"));

                                Log.d("Track", jObj.getString("Track"));

                                knowledgeList.add(knowledgeData);
                            }

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    knowledgeAdapter.notifyDataSetChanged();
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

    public void populateAlert() {
        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_user_type);
        TextView headTv = (TextView) dialog.findViewById(R.id.headTv);
        headTv.setText("Select Track");

        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new TrackAdapter());


        dialog.show();
    }

    class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.MyHolder>{

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_user_cell, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
            holder.nameTv.setText(trackList.get(position));
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
                        dialog.cancel();
                        trackTv.setText(trackList.get(getAdapterPosition()));
                        track = trackList.get(getAdapterPosition());

                        knowledgeList.clear();
                        try {
                            JSONArray jsonArray = new JSONArray(knowledegeResponse);
                            for (int i = 0; i < jsonArray.length(); i++) {

                                KnowledgeData knowledgeData = new KnowledgeData();
                                JSONObject jObj = jsonArray.getJSONObject(i);
                                if (jObj.getString("Track").equals(trackTv.getText().toString())) {
                                    knowledgeData.setDocumentType(jObj.getString("DocumentType"));
                                    knowledgeData.setLinkedURL(jObj.getString("LinkedURL"));
                                    knowledgeData.setTitle(jObj.getString("Title"));
                                    knowledgeData.setTrack(jObj.getString("Track"));

                                    Log.d("Track", jObj.getString("Track"));

                                    knowledgeList.add(knowledgeData);
                                }
                            }

                            knowledgeAdapter.notifyDataSetChanged();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        }
    }
}
