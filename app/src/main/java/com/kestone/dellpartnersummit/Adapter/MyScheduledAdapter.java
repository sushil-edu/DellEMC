package com.kestone.dellpartnersummit.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.badoualy.datepicker.DatePickerTimeline;
import com.kestone.dellpartnersummit.ApiHandler.ApiUrl;
import com.kestone.dellpartnersummit.ConnectionCheck.ConnectionCheck;
import com.kestone.dellpartnersummit.Fragments.MyScheduled;
import com.kestone.dellpartnersummit.PoJo.MyScheduledData;
import com.kestone.dellpartnersummit.PoJo.UserDetails;
import com.kestone.dellpartnersummit.ProgressView.Progress;
import com.kestone.dellpartnersummit.R;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

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


public class MyScheduledAdapter extends RecyclerView.Adapter<MyScheduledAdapter.MyHolder> {

    private Context context;
    private Activity activity;
    private ArrayList<MyScheduledData> scheduleList;
    private String monthStr = "", dayStr = "", hourStr = "", minuteStr = "";
    private MyScheduledData myScheduledData;
    private Fragment fragment;

    public MyScheduledAdapter(Context context, ArrayList<MyScheduledData> scheduleList, Fragment fragment) {
        this.context = context;
        this.scheduleList = scheduleList;
        this.activity = (Activity) context;
        this.fragment = fragment;
    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_scheduled_cell, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

        myScheduledData = scheduleList.get(position);

        holder.nameTv.setText(myScheduledData.getName());
        holder.designationTv.setText(myScheduledData.getDesignation());
        holder.organisationTv.setText(myScheduledData.getOrganization());

        String hourStr = myScheduledData.getHours();
        if (hourStr.length() == 1) {
            hourStr = "0" + hourStr;
        }

        String minuteStr = myScheduledData.getMinutes();
        if (minuteStr.length() == 1) {
            minuteStr = "0" + minuteStr;
        }

        holder.timeTv.setText(hourStr + ":" + minuteStr);
        String monthStr = new DateFormatSymbols().getMonths()[Integer.parseInt(myScheduledData.getMonth()) - 1];
        String dayStr = myScheduledData.getDay();
        if (dayStr.length() == 1) {
            dayStr = "0" + dayStr;
        }
        holder.dateTv.setText(monthStr.substring(0, 3) +
                " " + dayStr);

        holder.btnReschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SharedPreferences sharedPreferences = context.getSharedPreferences("Schedule", Context.MODE_PRIVATE);
//                String myResponse = sharedPreferences.getString("myResponse", "");
//                if (myResponse.length() > 0) {
//                    populateCalendar(myResponse);
//                } else {
//                    if (ConnectionCheck.connectionStatus(context)) {
//
//                        Progress.showProgress(context);
//                        new GetSchedule(ApiUrl.NetworkingTimeSlots);
//
//                    } else {
//                        Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
//                    }
//                }

                populateTimeAlert(myScheduledData.getDay(),myScheduledData.getMonth());
            }
        });

        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionCheck.connectionStatus(context)) {

                    Progress.showProgress(context);

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("FromEmailID", UserDetails.EmailID);
                        jsonObject.put("ToEmailID", myScheduledData.getFromEmailID());

                        Log.d("CancelMeetingParams", jsonObject.toString());

                        new CancelMeeting(ApiUrl.CancelMeeting, jsonObject.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Progress.closeProgress();
                    }

                } else {
                    Toast.makeText(context, "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(myScheduledData.getImageUrl().length()>0){
            Picasso.with(context).load(myScheduledData.getImageUrl()).placeholder(R.drawable.ic_user).into(holder.profileIV);
        }
    }


    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        private TextView nameTv, dateTv, timeTv, organisationTv, designationTv;
        private Button btnReschedule, cancelBtn;
        private CircularImageView profileIV;

        MyHolder(View itemView) {
            super(itemView);
            nameTv = (TextView) itemView.findViewById(R.id.nameTv);
            dateTv = (TextView) itemView.findViewById(R.id.dateTv);
            timeTv = (TextView) itemView.findViewById(R.id.timeTv);
            organisationTv = (TextView) itemView.findViewById(R.id.organisationTv);
            designationTv = (TextView) itemView.findViewById(R.id.designationTv);
            btnReschedule = (Button) itemView.findViewById(R.id.btnReschedule);
            cancelBtn = (Button) itemView.findViewById(R.id.cancelBtn);
            profileIV = (CircularImageView) itemView.findViewById(R.id.profileIv);
        }
    }

    private class PostReschedule {

        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        private PostReschedule(String url, String postBody) {


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
                        Log.d("Rescheduled Data", myRepsonse);

                        try {
                            JSONObject jObj = new JSONObject(myRepsonse);
                            if (jObj.getString("retval").equals("Request added successfully")) {

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        fragment.getChildFragmentManager().beginTransaction()
                                                .replace(R.id.container, new MyScheduled())
                                                .commit();
                                    }
                                });

                            } else {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Some Problem Occurred", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            });
        }
    }

    private void populateCalendar(String response) {

        try {
            JSONArray jsonArray = new JSONArray(response);
            JSONObject jObj = jsonArray.getJSONObject(0);

            ArrayList<Integer> hourList = new ArrayList<>();
            ArrayList<Integer> minuteList = new ArrayList<>();

            for (int i = jObj.getInt("SHours"); i <= jObj.getInt("EHours"); i++) {
                hourList.add(i);
            }

            for (int i = 0; i < 6; i++) {
                minuteList.add(i * 10);
            }


            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.alert_reschedule);
            dialog.setCancelable(true);
            DatePickerTimeline datePicker = (DatePickerTimeline) dialog.findViewById(R.id.datePicker);
            datePicker.setFirstVisibleDate(2016, jObj.getInt("SMonth") - 1, jObj.getInt("SDay"));
            datePicker.setLastVisibleDate(2016, jObj.getInt("EMonth") - 1, jObj.getInt("EDay"));
            datePicker.setSelectedDate(0, 0, 0);
            datePicker.setOnDateSelectedListener(new DatePickerTimeline.OnDateSelectedListener() {
                @Override
                public void onDateSelected(int year, int month, int day, int index) {
                    monthStr = month + 1 + "";
                    dayStr = day + "";
                }
            });

            final Spinner hourSpinner = (Spinner) dialog.findViewById(R.id.hourSpinner);
            final Spinner minuteSpinner = (Spinner) dialog.findViewById(R.id.minuteSpinner);

            ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, hourList);
            hourSpinner.setAdapter(adapter);

            ArrayAdapter mAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, minuteList);
            minuteSpinner.setAdapter(mAdapter);

            Button mBtnSet = (Button) dialog.findViewById(R.id.mBtnSet);
            mBtnSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hourStr = hourSpinner.getSelectedItem().toString();
                    minuteStr = minuteSpinner.getSelectedItem().toString();

                    if (hourStr.length() > 0 && minuteStr.length() > 0 && dayStr.length() > 0 && monthStr.length() > 0) {
                        Log.d("Reschedule Time", "Hour-" + hourStr + " Minute-" + minuteStr + " Day-" + dayStr + " Month-" + monthStr);
                        dialog.dismiss();

                        if (ConnectionCheck.connectionStatus(context)) {
                            Progress.showProgress(context);
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("FromEmailID", myScheduledData.getFromEmailID());
                                jsonObject.put("ToEmailID", myScheduledData.getToEmailID());
                                jsonObject.put("Day", dayStr);
                                jsonObject.put("Month", monthStr);
                                jsonObject.put("Hours", hourStr);
                                jsonObject.put("Minutes", minuteStr);

                                Log.d("Reschedule Params", jsonObject.toString());

                                new PostReschedule(ApiUrl.Reschedule, jsonObject.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Progress.closeProgress();
                            }

                        } else {
                            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        Toast.makeText(context, "Select date for meeting", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            dialog.show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetSchedule {
        private String url;

        private GetSchedule(String url) {
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
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    Progress.closeProgress();

                    final String myRepsonse = response.body().string();
                    Log.d("DateResponse", myRepsonse);

                    SharedPreferences sharedPreferences = context.getSharedPreferences("Schedule", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("myResponse", myRepsonse);
                    editor.apply();

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            populateCalendar(myRepsonse);
                        }
                    });


                }
            });
        }

    }

    private class CancelMeeting {

        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        private CancelMeeting(String url, String postBody) {

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
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Progress.closeProgress();
                        }

                    });

                    if (response.isSuccessful()) {

                        String myResponse = response.body().string();

                        Log.d("CancelMeetingRes", myResponse);

                        try {
                            JSONObject jObj = new JSONObject(myResponse);
                            if (jObj.getString("retval").equals("Meeting cancelled successfully")) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        fragment.getChildFragmentManager().beginTransaction()
                                                .replace(R.id.container, new MyScheduled())
                                                .commit();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            });
        }
    }

    public void populateTimeAlert(final String dayStr,final String monthStr) {


        ArrayList<Integer> hourList = new ArrayList<>();
        ArrayList<Integer> minuteList = new ArrayList<>();

//            for (int i = jobj.getInt("SHours"); i <= jobj.getInt("EHours"); i++) {
//                hourList.add(i);
//            }
        hourList.add(10);
        hourList.add(11);
        hourList.add(12);
        hourList.add(1);
        hourList.add(2);
        hourList.add(3);
        hourList.add(4);
        hourList.add(5);
        hourList.add(6);
        hourList.add(7);
        hourList.add(8);


//            for (int i = 0; i < 6; i++) {
//                minuteList.add(i * 10);
//            }

        minuteList.add(00);
        minuteList.add(15);
        minuteList.add(30);
        minuteList.add(45);


        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_time_spinner);
        dialog.setCancelable(true);

        final Spinner hourSpinner = (Spinner) dialog.findViewById(R.id.hourSpinner);
        final Spinner minuteSpinner = (Spinner) dialog.findViewById(R.id.minuteSpinner);

        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, hourList);
        hourSpinner.setAdapter(adapter);

        ArrayAdapter mAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, minuteList);
        minuteSpinner.setAdapter(mAdapter);

        Button mBtnSet = (Button) dialog.findViewById(R.id.mBtnSet);
        mBtnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hourStr = hourSpinner.getSelectedItem().toString();
                minuteStr = minuteSpinner.getSelectedItem().toString();

                if (hourStr.length() > 0 && minuteStr.length() > 0 && dayStr.length() > 0 && monthStr.length() > 0) {
                    Log.d("Reschedule Time", "Hour-" + hourStr + " Minute-" + minuteStr + " Day-" + dayStr + " Month-" + monthStr);
                    dialog.dismiss();

                    if (ConnectionCheck.connectionStatus(context)) {
                        Progress.showProgress(context);
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("FromEmailID", myScheduledData.getToEmailID());
                            jsonObject.put("ToEmailID", myScheduledData.getFromEmailID());
                            jsonObject.put("Day", dayStr);
                            jsonObject.put("Month", monthStr);
                            jsonObject.put("Hours", hourStr);
                            jsonObject.put("Minutes", minuteStr);

                            Log.d("Reschedule Params", jsonObject.toString());

                            new PostReschedule(ApiUrl.Reschedule, jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Progress.closeProgress();
                        }

                    } else {
                        Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(context, "Select time for meeting", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
            }
        });

        dialog.show();


    }
}
