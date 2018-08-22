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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kestone.dellpartnersummit.Activities.MainActivity;
import com.kestone.dellpartnersummit.ApiHandler.ApiUrl;
import com.kestone.dellpartnersummit.ConnectionCheck.ConnectionCheck;
import com.kestone.dellpartnersummit.PoJo.UserData;
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
public class AskAQuestionFragment extends Fragment {

    private ArrayList<UserData> speakerList = new ArrayList<>();
    private ArrayList<String> speakerNameList = new ArrayList<>();
    private ArrayList<String> trackList = new ArrayList<>();
    private Spinner spinner, spinner2;
    private String track = "", speaker = "";
    private TextView speakerTv,trackTv;
    private Dialog dialog;


    public AskAQuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ask_aquestion, container, false);

        RelativeLayout topLl = (RelativeLayout) view.findViewById(R.id.topLl);
        RelativeLayout topLl2 = (RelativeLayout) view.findViewById(R.id.topLl2);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        spinner2 = (Spinner) view.findViewById(R.id.spinner2);
        speakerTv = (TextView) view.findViewById(R.id.speakerTv);
        trackTv = (TextView) view.findViewById(R.id.trackTv);

        final EditText txtFeedback = (EditText) view.findViewById(R.id.txtFeedback);



       // if (UserDetails.getUserType().equals("CIO")) {
            topLl.setVisibility(View.GONE);
            //topLl2.setVisibility(View.GONE);
            Progress.showProgress(getContext());
            if (ConnectionCheck.connectionStatus(getContext())) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("UserType", "Speaker");
                    new SpeakerDataFetch(ApiUrl.Users, jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Progress.closeProgress();
                Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }

//        } else {
//            topLl.setVisibility(View.VISIBLE);
//           // topLl2.setVisibility(View.VISIBLE);
//
//            Progress.showProgress(getContext());
//            if (ConnectionCheck.connectionStatus(getContext())) {
//                JSONObject jsonObject = new JSONObject();
//                try {
//                    jsonObject.put("UserType", "Speaker");
//                    new SpeakerDataFetch(ApiUrl.Users, jsonObject.toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            } else {
//                Progress.closeProgress();
//                Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
//            }
//
//        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    track = "";
                } else {
                    track = trackList.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    speaker = "";
                } else {
                    UserData userData = speakerList.get(position - 1);
                    speaker = userData.getEmailID();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        speakerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(speakerList.size()>0){
                    populateSpeaker();
                }else {
                    Toast.makeText(getContext(), "No Speaker available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        trackTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(trackList.size()>0){
                    populateAlert();
                }else {
                    Toast.makeText(getContext(), "No Tracks available", Toast.LENGTH_SHORT).show();
                }
            }
        });


        view.findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             //   if (UserDetails.getUserType().equals("CIO")&& speaker.length()>0) {

                    if (txtFeedback.getText().length() == 0 || speaker.length()==0) {
                        Toast.makeText(getContext(), "All details are mandatory", Toast.LENGTH_SHORT).show();
                    } else {
                       // Log.d("AskParams", speaker + " " + track + " " + txtFeedback.getText().toString());
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("RefSpeakerID", speaker);
                            jsonObject.put("Track", "");
                            jsonObject.put("Question", txtFeedback.getText().toString());
                            jsonObject.put("EmailID", UserDetails.getEmailID());

                            if (ConnectionCheck.connectionStatus(getContext())) {
                                Progress.showProgress(getContext());
                                Log.d("AskParams", jsonObject.toString());
                                new PostQuestion(ApiUrl.AskAQuestion, jsonObject.toString());
                            } else {
                                Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
//                }else {
//
//
//                    if (speaker.length() == 0 || track.length() == 0 || txtFeedback.getText().length() == 0) {
//                        Toast.makeText(getContext(), "All details are mandatory", Toast.LENGTH_SHORT).show();
//                    } else {
//                        //Log.d("AskParams", speaker + " " + track + " " + txtFeedback.getText().toString());
//                        JSONObject jsonObject = new JSONObject();
//                        try {
//
//
//
//                            jsonObject.put("RefSpeakerID", speaker);
//                            jsonObject.put("Track", track);
//                            jsonObject.put("Question", txtFeedback.getText().toString());
//                            jsonObject.put("EmailID", UserDetails.getEmailID());
//
//                            Log.d("AskParams", jsonObject.toString());
//
//                            if (ConnectionCheck.connectionStatus(getContext())) {
//                                Progress.showProgress(getContext());
//                                new PostQuestion(ApiUrl.AskAQuestion, jsonObject.toString());
//                            } else {
//                                Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//
//
//                }
            }
        });

        return view;
    }


    private class SpeakerDataFetch {

        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        private SpeakerDataFetch(String url, String postBody) {


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

                        final String myResponse = response.body().string();
                        Log.d("Speaker Response", myResponse);

                        try {
                            JSONArray jsonArray = new JSONArray(myResponse);
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                UserData speakerData = new UserData();
                                speakerData.setID(jsonObject.getString("ID"));
                                speakerData.setName(jsonObject.getString("Name"));
                                speakerData.setDesignation(jsonObject.getString("Designation"));
                                speakerData.setOrganization(jsonObject.getString("Organization"));
                                speakerData.setEmailID(jsonObject.getString("EmailID"));
                                speakerData.setMobile(jsonObject.getString("Mobile"));
                                speakerData.setPassportNo(jsonObject.getString("PassportNo"));
                                speakerData.setRegistrationType(jsonObject.getString("RegistrationType"));
                                speakerData.setImageURL(jsonObject.getString("ImageURL"));

                                speakerNameList.add(jsonObject.getString("Name"));
                                speakerList.add(speakerData);

                            }

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.spinner_center_row, speakerNameList);
                                    spinner2.setAdapter(adapter);

                                    Progress.showProgress(getContext());
                                    if (ConnectionCheck.connectionStatus(getActivity())) {
                                        new GetTracksData(ApiUrl.Tracks);
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

    private class PostQuestion {

        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        private PostQuestion(String url, String postBody) {

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

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Progress.closeProgress();
                        }
                    });

                    if (response.isSuccessful()) {

                        String myResponse = response.body().string();
                        Log.d("AskAQuestionRes", myResponse);

                        try {
                            JSONObject jsonObject = new JSONObject(myResponse);
                            if (jsonObject.getString("retval").equals("Question submitted successfully")) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "Question submitted successfully", Toast.LENGTH_SHORT).show();
                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.container, new ActivityStreamFragment()).commit();
                                        MainActivity.mTitleTv.setText("Activity Stream");
                                    }
                                });
                            }


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

    public void populateSpeaker() {
        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_user_type);
        TextView headTv = (TextView) dialog.findViewById(R.id.headTv);
        headTv.setText("Select Speaker");

        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new SpeakerAdapter());


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

                    }
                });

            }
        }
    }

    class SpeakerAdapter extends RecyclerView.Adapter<SpeakerAdapter.MyHolder>{

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_user_cell, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
            holder.nameTv.setText(speakerNameList.get(position));
        }


        @Override
        public int getItemCount() {
            return speakerNameList.size();
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
                        speakerTv.setText(speakerNameList.get(getAdapterPosition()));
                        UserData userData = speakerList.get(getAdapterPosition());
                        speaker = userData.getEmailID();

                    }
                });

            }
        }
    }

}
