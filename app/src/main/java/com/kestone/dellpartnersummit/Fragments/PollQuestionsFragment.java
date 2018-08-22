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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kestone.dellpartnersummit.Adapter.PollsQuestionAdapter;
import com.kestone.dellpartnersummit.ApiHandler.ApiUrl;
import com.kestone.dellpartnersummit.ConnectionCheck.ConnectionCheck;
import com.kestone.dellpartnersummit.PoJo.PollsData;
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

public class PollQuestionsFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private PollsQuestionAdapter pollsQuestionAdapter;
    private ArrayList<PollsData> pollsList;
    public static String TRACK_NAME="";
    private TextView trackTv;
    private Dialog dialog;
    private Spinner spinner;
    ArrayAdapter<String> adapter;
    private RelativeLayout topLl;
    final ArrayList<String> trackList = new ArrayList<String>();

    public PollQuestionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_poll_questions, container, false);

        Button mSubmitBtn = (Button) view.findViewById(R.id.mSubmitBtn);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        TextView selectTv = (TextView) view.findViewById(R.id.selectTv);
        trackTv = (TextView) view.findViewById(R.id.trackTv);
        topLl = (RelativeLayout) view.findViewById(R.id.topLl);

        adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_row, trackList);
        adapter.notifyDataSetChanged();
        spinner.setAdapter(adapter);


        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pollsList = new ArrayList<>();
        pollsQuestionAdapter = new PollsQuestionAdapter(getContext(),pollsList, mSubmitBtn);
        recyclerView.setAdapter(pollsQuestionAdapter);

      //  if(UserDetails.getUserType().equals("CIO")){
            spinner.setVisibility(View.GONE);
            selectTv.setVisibility(View.GONE);
            topLl.setVisibility(View.GONE);

            Progress.showProgress(getContext());
            if(ConnectionCheck.connectionStatus(getActivity())){
                new PollsFetch(ApiUrl.PollsList,"{\"IsActive\":\"Y\"}");
            }else {
                Progress.closeProgress();
                Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
//
//        }else {
//            spinner.setVisibility(View.VISIBLE);
//            selectTv.setVisibility(View.GONE);
        //    topLl.setVisibility(View.VISIBLE);
//
//            Progress.showProgress(getContext());
//            if(ConnectionCheck.connectionStatus(getActivity())){
//                new GetTracksData(ApiUrl.Tracks);
//            }else {
//                Progress.closeProgress();
//                Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
//            }
//        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0){
                    TRACK_NAME = trackList.get(position);
                    Progress.showProgress(getContext());
                    if(ConnectionCheck.connectionStatus(getActivity())){
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("IsActive", "Y");
                            new PollsFetch(ApiUrl.PollsList,jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Progress.closeProgress();
                        Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    TRACK_NAME = trackList.get(position);
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

    private class PollsFetch {

        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        PollsFetch(String url, String postBody) {


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
                                    pollsQuestionAdapter.notifyDataSetChanged();
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

                            for(int i =0; i<jsonArray.length();i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                trackList.add(jsonObject.getString("TrackName"));
                            }

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    adapter.notifyDataSetChanged();

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
                        TRACK_NAME = trackList.get(getAdapterPosition());
                        Progress.showProgress(getContext());
                        if(ConnectionCheck.connectionStatus(getActivity())){
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("IsActive", "Y");
                                new PollsFetch(ApiUrl.PollsList,jsonObject.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            Progress.closeProgress();
                            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }
    }

}
