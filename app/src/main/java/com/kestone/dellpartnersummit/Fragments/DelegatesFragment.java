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
import android.widget.TextView;
import android.widget.Toast;

import com.kestone.dellpartnersummit.Adapter.DelegatesAdapter;
import com.kestone.dellpartnersummit.ApiHandler.ApiUrl;
import com.kestone.dellpartnersummit.ConnectionCheck.ConnectionCheck;
import com.kestone.dellpartnersummit.PoJo.DelegatesData;
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
import java.util.concurrent.TimeUnit;

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
public class DelegatesFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<DelegatesData> delegatesList;
    private DelegatesAdapter delegatesAdapter;
    private String myReponse;
    JSONArray jsonArray;
    private long Diff;


    public DelegatesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_delegates, container, false);


        EditText searchEt = (EditText) view.findViewById(R.id.searchEt);
        searchEt.setTextSize(12f);
        TextView noteTv = (TextView) view.findViewById(R.id.noteTv);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dtStr = dateFormat.format(Calendar.getInstance().getTime());
        Date convertedDate = new Date();
        Date startDate = new Date();
        Date endDate = new Date();
        try {
            convertedDate = dateFormat.parse(dtStr);
            startDate = dateFormat.parse(UserDetails.getCheckinStartDatetime());
            endDate = dateFormat.parse(UserDetails.getCheckinEndDatetime());
            Diff = TimeUnit.DAYS.convert(startDate.getTime()-convertedDate.getTime(), TimeUnit.MILLISECONDS);


            Log.d("strDateCompare", TimeUnit.DAYS.convert(startDate.getTime()-convertedDate.getTime(), TimeUnit.MILLISECONDS) +"");
            Log.d("endDateCompare", endDate.compareTo(convertedDate)+"");

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


//        String dateStr = UserDetails.CheckinStartDatetime.substring(0,4)+"/"+UserDetails.CheckinStartDatetime.substring(5,7)
//                +"/"+UserDetails.getCheckinStartDatetime().substring(8,10);
//
//        Date date = new Date(dateStr);
//
//        Calendar cal = Calendar.getInstance();
//        Date todayDate = cal.getTime();
//
//        cal.setTime(date);
//        cal.add(Calendar.DAY_OF_YEAR, -1);
//        Date oneDayBefore = cal.getTime();
//
//        cal.setTime(date);
//        cal.add(Calendar.DAY_OF_YEAR, -2);
//        Date twoDayBefore = cal.getTime();
//
//        cal.setTime(date);
//        cal.add(Calendar.DAY_OF_YEAR, 0);
//        Date zeroDayBefore = cal.getTime();
//
//        Log.d("todayDate", todayDate + "");
//
//
//        Log.d("dateStr0", (zeroDayBefore + "").substring(4,7)+"-"
//                +(zeroDayBefore + "").substring(8,10)+"-"+(zeroDayBefore + "").substring(30,34));
//        Log.d("dateStr1", (oneDayBefore + "").substring(4,7)+"-"
//                +(oneDayBefore + "").substring(8,10)+"-"+(oneDayBefore + "").substring(30,34));
//        Log.d("dateStr2", (twoDayBefore + "").substring(4,7)+"-"
//                +(twoDayBefore + "").substring(8,10)+"-"+(twoDayBefore + "").substring(30,34));
//
//        Log.d("date", (todayDate + "").substring(4,7)+"-"
//                +(todayDate + "").substring(8,10)+"-"+(todayDate + "").substring(30,34));
//
//        final String dateStr0 = (zeroDayBefore + "").substring(4,7)+"-"
//                +(zeroDayBefore + "").substring(8,10)+"-"+(zeroDayBefore + "").substring(30,34);
//
//        final String dateStr1 = (oneDayBefore + "").substring(4,7)+"-"
//                +(oneDayBefore + "").substring(8,10)+"-"+(oneDayBefore + "").substring(30,34);
//
//        final String dateStr2 = (twoDayBefore + "").substring(4,7)+"-"
//                +(twoDayBefore + "").substring(8,10)+"-"+(twoDayBefore + "").substring(30,34);
//
//        final String dateSr = (todayDate + "").substring(4,7)+"-"
//                +(todayDate + "").substring(8,10)+"-"+(todayDate + "").substring(30,34);


      //  if ((int)Diff<Integer.parseInt(UserDetails.ActivationDays) && (int)Diff>=0) {

            searchEt.setVisibility(View.VISIBLE);
            noteTv.setVisibility(View.GONE);

            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setHasFixedSize(true);
            delegatesList = new ArrayList<>();
            delegatesAdapter = new DelegatesAdapter(getContext(), delegatesList);
            recyclerView.setAdapter(delegatesAdapter);


            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Progress.showProgress(getContext());
                    if (ConnectionCheck.connectionStatus(getActivity())) {

                        delegatesList.clear();
                        delegatesAdapter.notifyDataSetChanged();

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("UserType","Delegate");
                            jsonObject.put("RefEmailID",UserDetails.EmailID);
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
                    jsonObject.put("UserType","Delegate");
                    jsonObject.put("RefEmailID",UserDetails.EmailID);
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


//        } else {
//            searchEt.setVisibility(View.INVISIBLE);
//            noteTv.setVisibility(View.VISIBLE);
//        }

        return view;
    }

    private class SpeakerDataFetch {

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
                        delegatesList.clear();

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


    void populateSearch(String myResponse, String s) {

        delegatesList.clear();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                delegatesAdapter.notifyDataSetChanged();
            }
        });

        try {
            jsonArray = new JSONArray(myResponse);
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                if (jsonObject.getString("Name").toLowerCase().contains(s.toLowerCase())) {

                    DelegatesData delegatesData = new DelegatesData();
                    delegatesData.setID(jsonObject.getString("ID"));
                    delegatesData.setName(jsonObject.getString("Name"));
                    delegatesData.setDesignation(jsonObject.getString("Designation"));
                    delegatesData.setOrganization(jsonObject.getString("Organization"));
                    delegatesData.setEmailID(jsonObject.getString("EmailID"));
                    delegatesData.setMobile(jsonObject.getString("Mobile"));
                    delegatesData.setPassportNo(jsonObject.getString("PassportNo"));
                    delegatesData.setRegistrationType(jsonObject.getString("RegistrationType"));
                    delegatesData.setImageURL(jsonObject.getString("ImageURL"));
                    delegatesData.setProfileDesc(jsonObject.getString("ProfileDesc"));

                    delegatesList.add(delegatesData);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                delegatesAdapter.notifyDataSetChanged();
            }
        });

    }


    void populateAll(String myResponse) {
        delegatesList.clear();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                delegatesAdapter.notifyDataSetChanged();
            }
        });


        try {
            jsonArray = new JSONArray(myResponse);
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                DelegatesData delegatesData = new DelegatesData();
                delegatesData.setID(jsonObject.getString("ID"));
                delegatesData.setName(jsonObject.getString("Name"));
                delegatesData.setDesignation(jsonObject.getString("Designation"));
                delegatesData.setOrganization(jsonObject.getString("Organization"));
                delegatesData.setEmailID(jsonObject.getString("EmailID"));
                delegatesData.setMobile(jsonObject.getString("Mobile"));
                delegatesData.setPassportNo(jsonObject.getString("PassportNo"));
                delegatesData.setRegistrationType(jsonObject.getString("RegistrationType"));
                delegatesData.setImageURL(jsonObject.getString("ImageURL"));
                delegatesData.setProfileDesc(jsonObject.getString("ProfileDesc"));

                delegatesList.add(delegatesData);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                delegatesAdapter.notifyDataSetChanged();
            }
        });

    }


}
