package com.kestone.dellpartnersummit.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.badoualy.datepicker.DatePickerTimeline;
import com.kestone.dellpartnersummit.Adapter.UserSelectAdapter;
import com.kestone.dellpartnersummit.ApiHandler.ApiResponse;
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
import java.text.DateFormatSymbols;
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

public class NetworkScheduleFragment extends Fragment {

    private TextView typeTv, nameTv, dayTv, monthTv, hourTv, minuteTv, labelTv;
    private RelativeLayout speakerRLl, nameRLl;
    public static String toMailId;
    private ArrayList<UserData> speakerList = new ArrayList<>();
    String dayStr = "", monthStr = "", hoursStr = "", minutesStr = "";
    private long Diff;

    public NetworkScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_network_schedule, container, false);

        typeTv = (TextView) view.findViewById(R.id.typeTv);
        nameTv = (TextView) view.findViewById(R.id.nameTv);
        dayTv = (TextView) view.findViewById(R.id.dayTv);
        monthTv = (TextView) view.findViewById(R.id.monthTv);
        hourTv = (TextView) view.findViewById(R.id.hourTv);
        minuteTv = (TextView) view.findViewById(R.id.minuteTv);
        labelTv = (TextView) view.findViewById(R.id.labelTv);

        dayTv.setText(UserDetails.getCheckinStartDatetime().substring(8, 10));
        dayStr = UserDetails.getCheckinStartDatetime().substring(8, 10);
        String month = new DateFormatSymbols().getMonths()[Integer.parseInt(UserDetails.getCheckinStartDatetime().substring(5, 7))-1];
        monthStr = UserDetails.getCheckinStartDatetime().substring(5, 7);
        monthTv.setText(month.substring(0, 3));

        Button meetingRequestBtn = (Button) view.findViewById(R.id.meetingRequestBtn);
        meetingRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (nameTv.getText().length() > 0 && dayStr.length() > 0 && monthStr.length() > 0
                        && hoursStr.length() > 0 && minutesStr.length() > 0) {

                    Progress.showProgress(getContext());
                    //Toast.makeText(getContext(), toMailId, Toast.LENGTH_SHORT).show();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("FromEmailID", UserDetails.EmailID);
                        jsonObject.put("ToEmailID", toMailId);
                        jsonObject.put("Day", UserDetails.getCheckinStartDatetime().substring(8, 10));
                        jsonObject.put("Month", UserDetails.getCheckinStartDatetime().substring(5, 7));
                        jsonObject.put("Hours", hoursStr);
                        jsonObject.put("Minutes", minutesStr);

                        Log.d("NetworkingParams", jsonObject.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Progress.closeProgress();
                    }

                    if (ConnectionCheck.connectionStatus(getContext())) {
                        if (UserDetails.getEmailID().equals(toMailId)) {
                            Progress.closeProgress();
                            Toast.makeText(getContext(), "Can't Schedule Meeting with yourself", Toast.LENGTH_SHORT).show();
                        } else {
                            new PostNetwork(ApiUrl.Networking, jsonObject.toString());
                        }
                    } else {
                        Progress.closeProgress();
                        Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getContext(), "Fill all details", Toast.LENGTH_SHORT).show();
                }
            }
        });

        nameRLl = (RelativeLayout) view.findViewById(R.id.nameRLl);
        speakerRLl = (RelativeLayout) view.findViewById(R.id.speakerRLl);


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


        nameRLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Progress.showProgress(getContext());
                if (ConnectionCheck.connectionStatus(getContext())) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("UserType", typeTv.getText().toString());
                        if (typeTv.getText().toString().equals("Delegate")) {

                                new SpeakerDataFetch(ApiUrl.Users, jsonObject.toString());

                        } else {
                            new SpeakerDataFetch(ApiUrl.Users, jsonObject.toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Progress.closeProgress();
                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        });

        speakerRLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.speaker_delegate_select);
                dialog.setCancelable(true);

                TextView speakerTv = (TextView) dialog.findViewById(R.id.speakerTV);
                TextView delegatesTv = (TextView) dialog.findViewById(R.id.delegateTv);

                speakerTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        typeTv.setText("Speaker");
                        nameTv.setText("");
                        labelTv.setText("Select Speaker");
                        dialog.dismiss();
                    }
                });
                delegatesTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        typeTv.setText("Delegate");
                        nameTv.setText("");
                        labelTv.setText("Select Delegate");
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        dayTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateCalendar();
            }
        });

        monthTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateCalendar();
            }
        });

//        dayTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                SharedPreferences sharedPreferences = getContext().getSharedPreferences("Schedule", Context.MODE_PRIVATE);
//                String myResponse = sharedPreferences.getString("myResponse", "");
//                if (myResponse.length() > 0) {
//                    populateCalendar(myResponse);
//                } else {
//                    if (ConnectionCheck.connectionStatus(getContext())) {
//
//                        Progress.showProgress(getContext());
//                        new GetSchedule(ApiUrl.NetworkingTimeSlots);
//
//                    } else {
//                        Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });
//
//        monthTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                SharedPreferences sharedPreferences = getContext().getSharedPreferences("Schedule", Context.MODE_PRIVATE);
//                String myResponse = sharedPreferences.getString("myResponse", "");
//                if (myResponse.length() > 0) {
//                    populateCalendar(myResponse);
//                } else {
//                    if (ConnectionCheck.connectionStatus(getContext())) {
//
//                        Progress.showProgress(getContext());
//                        new GetSchedule(ApiUrl.NetworkingTimeSlots);
//
//                    } else {
//                        Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//            }
//        });

        hourTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                SharedPreferences sharedPreferences = getContext().getSharedPreferences("Schedule", Context.MODE_PRIVATE);
//                String myResponse = sharedPreferences.getString("myResponse", "");
//                if (myResponse.length() > 0) {

                populateTimeAlert();

//                } else {
//                    if (ConnectionCheck.connectionStatus(getContext())) {
//
//                        Progress.showProgress(getContext());
//                        new GetSchedule(ApiUrl.NetworkingTimeSlots);
//
//                    } else {
//                        Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
//                    }
//                }
            }
        });

        minuteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                SharedPreferences sharedPreferences = getContext().getSharedPreferences("Schedule", Context.MODE_PRIVATE);
//                String myResponse = sharedPreferences.getString("myResponse", "");
//                if (myResponse.length() > 0) {

                populateTimeAlert();

//                } else {
//                    if (ConnectionCheck.connectionStatus(getContext())) {
//
//                        Progress.showProgress(getContext());
//                        new GetSchedule(ApiUrl.NetworkingTimeSlots);
//
//                    } else {
//                        Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
//                    }
//                }

            }
        });


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
                        Log.d("NetworkPostRes", myRepsonse);

                        try {
                            JSONObject jObj = new JSONObject(myRepsonse);
                            if (jObj.getString("retval").equals("Request added successfully")) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "Meeting Scheduled", Toast.LENGTH_SHORT).show();
                                        getActivity().getSupportFragmentManager()
                                                .beginTransaction().replace(R.id.container, new NetworkingFragment()).commit();
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

                    if (response.isSuccessful()) {
                        speakerList.clear();

                        final String myReponse = response.body().string();
                        Log.d("Speaker Response", myReponse);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                populateAlert(myReponse);
                            }
                        });

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

    public void populateAlert(String myResponse) {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_user_type);
        TextView headTv = (TextView) dialog.findViewById(R.id.headTv);
        headTv.setText("Select " + typeTv.getText().toString());


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

                speakerList.add(speakerData);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new UserSelectAdapter(getContext(), nameTv, speakerList, dialog));

        dialog.show();
    }

    public void populateCalendar() {

            final Dialog dialog = new Dialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.alert_date_time);
            dialog.setCancelable(true);
            DatePickerTimeline datePicker = (DatePickerTimeline) dialog.findViewById(R.id.datePicker);
            datePicker.setFirstVisibleDate(2016, 10, 23);
            datePicker.setLastVisibleDate(2016, 10 , 26);
            datePicker.setSelectedDate(0, 0, 0);
            datePicker.setOnDateSelectedListener(new DatePickerTimeline.OnDateSelectedListener() {
                @Override
                public void onDateSelected(int year, int month, int day, int index) {

                    dayTv.setText(day + "");

                    dayStr = day + "";
                    monthStr = month + 1 + "";

                    String monthStr = new DateFormatSymbols().getMonths()[month];
                    monthTv.setText(monthStr.substring(0, Math.min(monthStr.length(), 3)));
                    dialog.dismiss();
                }
            });

            dialog.show();

    }

    public void populateTimeAlert() {


        ArrayList<Integer> hourList = new ArrayList<>();
        ArrayList<Integer> minuteList = new ArrayList<>();

//            for (int i = jobj.getInt("SHours"); i <= jobj.getInt("EHours"); i++) {
//                hourList.add(i);
//            }
        hourList.add(1);
        hourList.add(2);
        hourList.add(3);
        hourList.add(4);
        hourList.add(5);
        hourList.add(6);
        hourList.add(7);
        hourList.add(8);
        hourList.add(9);
        hourList.add(10);
        hourList.add(11);
        hourList.add(12);
        hourList.add(13);
        hourList.add(14);
        hourList.add(15);
        hourList.add(16);
        hourList.add(17);
        hourList.add(18);
        hourList.add(19);
        hourList.add(20);
        hourList.add(21);
        hourList.add(22);
        hourList.add(23);
        hourList.add(24);


//            for (int i = 0; i < 6; i++) {
//                minuteList.add(i * 10);
//            }

        minuteList.add(00);
        minuteList.add(15);
        minuteList.add(30);
        minuteList.add(45);


        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_time_spinner);
        dialog.setCancelable(true);

        final Spinner hourSpinner = (Spinner) dialog.findViewById(R.id.hourSpinner);
        final Spinner minuteSpinner = (Spinner) dialog.findViewById(R.id.minuteSpinner);

        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, hourList);
        hourSpinner.setAdapter(adapter);

        ArrayAdapter mAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, minuteList);
        minuteSpinner.setAdapter(mAdapter);

        Button mBtnSet = (Button) dialog.findViewById(R.id.mBtnSet);
        mBtnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hourTv.setText(hourSpinner.getSelectedItem().toString());
                minuteTv.setText(minuteSpinner.getSelectedItem().toString());

                hoursStr = hourSpinner.getSelectedItem().toString();
                minutesStr = minuteSpinner.getSelectedItem().toString();

                dialog.dismiss();
            }
        });

        dialog.show();


    }


}
