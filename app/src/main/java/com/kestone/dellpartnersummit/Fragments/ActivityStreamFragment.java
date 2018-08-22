package com.kestone.dellpartnersummit.Fragments;

import android.app.Activity;
import android.content.Intent;
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

import com.google.firebase.analytics.FirebaseAnalytics;
import com.kestone.dellpartnersummit.Activities.AddActivity;
import com.kestone.dellpartnersummit.Adapter.ActivityStreamAdapter;
import com.kestone.dellpartnersummit.ApiHandler.ApiUrl;
import com.kestone.dellpartnersummit.ConnectionCheck.ConnectionCheck;
import com.kestone.dellpartnersummit.PoJo.StreamData;
import com.kestone.dellpartnersummit.PoJo.UserDetails;
import com.kestone.dellpartnersummit.ProgressView.Progress;
import com.kestone.dellpartnersummit.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ActivityStreamFragment extends Fragment {

    ArrayList<StreamData> streamDataList;
    ActivityStreamAdapter activityStreamAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseAnalytics mFirebaseAnalytics;

    public ActivityStreamFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_activity_stream, container, false);

        //FirebaseAnalytics initialization
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        streamDataList = new ArrayList<>();
        activityStreamAdapter = new ActivityStreamAdapter(getContext(), streamDataList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(activityStreamAdapter);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddActivity.class);
                startActivityForResult(intent, 100);

            }
        });


        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Progress.showProgress(getActivity());
                if (ConnectionCheck.connectionStatus(getContext())) {
                    streamDataList.clear();
                    activityStreamAdapter.notifyDataSetChanged();
                    new GetStreamData(ApiUrl.ActivityStream);
                } else {
                    Progress.closeProgress();
                    Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = dateFormat.format(Calendar.getInstance().getTime());

        Date convertedDate = new Date();
        Date startDate = new Date();
        Date endDate = new Date();
        try {
            convertedDate = dateFormat.parse(date);
            startDate = dateFormat.parse(UserDetails.getCheckinStartDatetime());
            endDate = dateFormat.parse(UserDetails.getCheckinEndDatetime());

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Log.d("convertedDate", convertedDate + "");
        Log.d("startDate", startDate + "");
        Log.d("endDate", endDate + "");

        Log.d("startDate", date);
        Log.d("getCheckinStartDatetime", UserDetails.getCheckinStartDatetime());
        Log.d("getCheckinEndDatetime", UserDetails.getCheckinEndDatetime());


//        if (UserDetails.IsCheckedIn.equals("N") && startDate.compareTo(convertedDate) * convertedDate.compareTo(endDate) >= 0) {
//
//            PermissionHelper permissionHelper = new PermissionHelper(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
//            permissionHelper.request(new PermissionHelper.PermissionCallback() {
//                @Override
//                public void onPermissionGranted() {
//
//                    Intent intent = new Intent(getActivity(), AttendanceActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                    getActivity().finish();
//
//
//                }
//
//                @Override
//                public void onPermissionDenied() {
//                    Log.d("Permission", "onPermissionDenied() called");
//
//                }
//
//                @Override
//                public void onPermissionDeniedBySystem() {
//                    Log.d("Permission", "onPermissionDeniedBySystem() called");
//                }
//            });
//
//
//        } else {
            Progress.showProgress(getActivity());
            if (ConnectionCheck.connectionStatus(getContext())) {
                new GetStreamData(ApiUrl.ActivityStream);

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, UserDetails.getEmailID());
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, UserDetails.getName());
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "ActivityStream");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            } else {
                Progress.closeProgress();
                Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
       // }


        return view;
    }

    private class GetStreamData {
        private String url;

        private GetStreamData(String url) {
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

                            streamDataList.clear();

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    activityStreamAdapter.notifyDataSetChanged();

                                }
                            });


                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                StreamData streamData = new StreamData();
                                streamData.setID(jsonObject.getString("ID"));
                                streamData.setBoothCheckin(jsonObject.getString("BoothCheckin"));
                                streamData.setPostedText(jsonObject.getString("PostedText"));
                                streamData.setPostedImage(jsonObject.getString("PostedImage"));
                                streamData.setPunchTime(jsonObject.getString("PunchTime"));
                                streamData.setName(jsonObject.getString("Name"));
                                streamData.setDesignation(jsonObject.getString("Designation"));
                                streamData.setOrganization(jsonObject.getString("Organization"));
                                streamData.setEmailID(jsonObject.getString("EmailID"));
                                streamData.setImageURL(jsonObject.getString("ImageURL"));
                                streamDataList.add(streamData);

                            }


                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    activityStreamAdapter.notifyDataSetChanged();

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {

            if (AddActivity.status.equals("No")) {

                //Toast.makeText(getContext(), "Okk", Toast.LENGTH_SHORT).show();

            } else if (AddActivity.status.equals("Yes")) {

                Progress.showProgress(getActivity());
                if (ConnectionCheck.connectionStatus(getContext())) {
                    new GetStreamData(ApiUrl.ActivityStream);
                } else {
                    Progress.closeProgress();
                    Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
