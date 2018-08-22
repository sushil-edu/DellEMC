package com.kestone.dellpartnersummit.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.badoualy.datepicker.DatePickerTimeline;
import com.kestone.dellpartnersummit.ApiHandler.ApiUrl;
import com.kestone.dellpartnersummit.ConnectionCheck.ConnectionCheck;
import com.kestone.dellpartnersummit.PoJo.UserDetails;
import com.kestone.dellpartnersummit.ProgressView.Progress;
import com.kestone.dellpartnersummit.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestSpekerMeeting extends AppCompatActivity {

    private TextView dayTv, monthTv, hourTv, minuteTv;
    String dayStr = "", monthStr = "", hoursStr = "", minutesStr = "", amStr = "";
    String status = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_speker_meeting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView userType = (TextView) findViewById(R.id.userType);
        userType.setText(getIntent().getStringExtra("Type"));

        TextView speakerNameTv = (TextView) findViewById(R.id.speakerNameTv);
        speakerNameTv.setText(getIntent().getStringExtra("Name"));


        dayTv = (TextView) findViewById(R.id.dayTv);
        monthTv = (TextView) findViewById(R.id.monthTv);
        hourTv = (TextView) findViewById(R.id.hourTv);
        minuteTv = (TextView) findViewById(R.id.minuteTv);

        dayTv.setText(UserDetails.getCheckinStartDatetime().substring(8, 10));
        dayStr = UserDetails.getCheckinStartDatetime().substring(8, 10);
        String month = new DateFormatSymbols().getMonths()[Integer.parseInt(UserDetails.getCheckinStartDatetime().substring(5, 7))-1];
        monthStr = UserDetails.getCheckinStartDatetime().substring(5, 7);
        monthTv.setText(month.substring(0, 3));

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


        hourTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                SharedPreferences sharedPreferences = getSharedPreferences("Schedule", Context.MODE_PRIVATE);
//                String myResponse = sharedPreferences.getString("myResponse", "");
//                if (myResponse.length() > 0) {

                    populateTimeAlert();

//                } else {
//                    if (ConnectionCheck.connectionStatus(RequestSpekerMeeting.this)) {
//
//                        Progress.showProgress(RequestSpekerMeeting.this);
//                        new GetSchedule(ApiUrl.NetworkingTimeSlots);
//
//                    } else {
//                        Toast.makeText(RequestSpekerMeeting.this, "No Internet", Toast.LENGTH_SHORT).show();
//                    }
//                }
            }
        });

        minuteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                SharedPreferences sharedPreferences = getSharedPreferences("Schedule", Context.MODE_PRIVATE);
//                String myResponse = sharedPreferences.getString("myResponse", "");
//                if (myResponse.length() > 0) {

                    populateTimeAlert();

//                } else {
//                    if (ConnectionCheck.connectionStatus(RequestSpekerMeeting.this)) {
//
//                        Progress.showProgress(RequestSpekerMeeting.this);
//                        new GetSchedule(ApiUrl.NetworkingTimeSlots);
//
//                    } else {
//                        Toast.makeText(RequestSpekerMeeting.this, "No Internet", Toast.LENGTH_SHORT).show();
//                    }
//                }
            }
        });

        findViewById(R.id.meetingRequestBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dayStr.length() > 0 && monthStr.length() > 0
                        && hoursStr.length() > 0 && minutesStr.length() > 0) {

                    Progress.showProgress(RequestSpekerMeeting.this);
                    //Toast.makeText(getContext(), toMailId, Toast.LENGTH_SHORT).show();
                    JSONObject jsonObject = new JSONObject();
                    try {

                        jsonObject.put("FromEmailID", UserDetails.EmailID);
                        jsonObject.put("ToEmailID", getIntent().getStringExtra("Email"));
                        jsonObject.put("Day", dayStr);
                        jsonObject.put("Month", monthStr);
                        jsonObject.put("Hours", hoursStr);
                        jsonObject.put("Minutes", minutesStr);

                        Log.d("NetworkingParams", jsonObject.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Progress.closeProgress();
                    }

                    if (ConnectionCheck.connectionStatus(RequestSpekerMeeting.this)) {
                        if(getIntent().getStringExtra("Tag").equals("Reschedule")){
                            Log.d("Reschedule", " Request");
                            new PostNetwork(ApiUrl.Reschedule, jsonObject.toString());
                        }else {
                            new PostNetwork(ApiUrl.Networking, jsonObject.toString());
                        }
                    } else {
                        Progress.closeProgress();
                        Toast.makeText(RequestSpekerMeeting.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(RequestSpekerMeeting.this, "Fill all details", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("Status", status);
        setResult(RESULT_OK,returnIntent);
        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
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

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RequestSpekerMeeting.this, "Meeting Scheduled", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                status = "ok";
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RequestSpekerMeeting.this, response.message(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            });
        }
    }

    public void populateCalendar() {

        final Dialog dialog = new Dialog(RequestSpekerMeeting.this);
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

        final Dialog dialog = new Dialog(RequestSpekerMeeting.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_time_spinner);
        dialog.setCancelable(true);

        final Spinner hourSpinner = (Spinner) dialog.findViewById(R.id.hourSpinner);
        final Spinner minuteSpinner = (Spinner) dialog.findViewById(R.id.minuteSpinner);

        ArrayAdapter adapter = new ArrayAdapter(RequestSpekerMeeting.this, android.R.layout.simple_spinner_dropdown_item, hourList);
        hourSpinner.setAdapter(adapter);

        ArrayAdapter mAdapter = new ArrayAdapter(RequestSpekerMeeting.this, android.R.layout.simple_spinner_dropdown_item, minuteList);
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
