package com.kestone.dellpartnersummit.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kestone.dellpartnersummit.ApiHandler.ApiUrl;
import com.kestone.dellpartnersummit.ConnectionCheck.ConnectionCheck;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SpeakerDetails extends AppCompatActivity {

    private static int REQUEST_CODE = 100;
    private LinearLayout bottomLl;
    private JSONObject jsonObject;
    public static String hourStr, minuteStr, monthStr, dayStr;
    private Button mScheduleBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView organizationTv = (TextView) findViewById(R.id.organizationTv);
        TextView designationTv = (TextView) findViewById(R.id.designationTv);
        TextView nameTv = (TextView) findViewById(R.id.nameTv);
        TextView mTitleTv = (TextView) findViewById(R.id.mTitleTv);
        TextView detailsTv = (TextView) findViewById(R.id.detailsTv);
        detailsTv.setText(getIntent().getStringExtra("details"));
       // mTitleTv.setText(getIntent().getStringExtra("Type") + " Details");
        mScheduleBtn = (Button) findViewById(R.id.mScheduleBtn);

        bottomLl = (LinearLayout) findViewById(R.id.bottomLl);

        organizationTv.setText(getIntent().getStringExtra("Organization"));
        designationTv.setText(getIntent().getStringExtra("Designation"));
        nameTv.setText(getIntent().getStringExtra("Name"));
//
//        getSupportFragmentManager().beginTransaction().
//                replace(R.id.container, new com.xperiatechnology.eventapp.Fragments.ScheduledMeetingFragment())
//                .commit();
//
//        getSupportFragmentManager().beginTransaction().
//                replace(R.id.container, new com.xperiatechnology.eventapp.Fragments.SpeakerDetails())
//                .commit();

        CircularImageView profileIv = (CircularImageView) findViewById(R.id.profileIv);

        if (getIntent().getStringExtra("Image").length() > 0) {
            Picasso.with(SpeakerDetails.this).load(getIntent().getStringExtra("Image"))
                    .resize(100,100)
                    .placeholder(R.drawable.ic_user).into(profileIv);
        }


        if (ConnectionCheck.connectionStatus(this)) {

            Progress.showProgress(this);

            jsonObject = new JSONObject();
            try {
                jsonObject.put("FromEmailID", UserDetails.EmailID);
                jsonObject.put("ToEmailID", getIntent().getStringExtra("Email"));

                Log.d("MeetingStatusParams", jsonObject.toString());

                new CheckMeetingStatus(ApiUrl.GetMeetingStatus, jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
                Progress.closeProgress();
            }


        } else {
            Toast.makeText(this, "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show();
        }

        Button mRescheduleBtn = (Button) findViewById(R.id.mRescheduleBtn);
        mRescheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SpeakerDetails.this, RequestSpekerMeeting.class);
                intent.putExtra("Name", getIntent().getStringExtra("Name"));
                intent.putExtra("Designation", getIntent().getStringExtra("Designation"));
                intent.putExtra("Organization", getIntent().getStringExtra("Organization"));
                intent.putExtra("Type", getIntent().getStringExtra("Type"));
                intent.putExtra("Email", getIntent().getStringExtra("Email"));
                intent.putExtra("Tag", "Reschedule");
                startActivityForResult(intent, REQUEST_CODE);

            }
        });

        findViewById(R.id.mScheduleBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SpeakerDetails.this, RequestSpekerMeeting.class);
                intent.putExtra("Name", getIntent().getStringExtra("Name"));
                intent.putExtra("Designation", getIntent().getStringExtra("Designation"));
                intent.putExtra("Organization", getIntent().getStringExtra("Organization"));
                intent.putExtra("Type", getIntent().getStringExtra("Type"));
                intent.putExtra("Email", getIntent().getStringExtra("Email"));
                intent.putExtra("Tag", "Schedule");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        findViewById(R.id.cancelMeetingBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ConnectionCheck.connectionStatus(SpeakerDetails.this)) {

                    Progress.showProgress(SpeakerDetails.this);

                    jsonObject = new JSONObject();
                    try {
                        jsonObject.put("FromEmailID", UserDetails.EmailID);
                        jsonObject.put("ToEmailID", getIntent().getStringExtra("Email"));

                        Log.d("CancelMeetingParams", jsonObject.toString());

                        new CancelMeeting(ApiUrl.CancelMeeting, jsonObject.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Progress.closeProgress();
                    }

                } else {
                    Toast.makeText(SpeakerDetails.this, "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            if (data.getStringExtra("Status").equals("ok")) {

                if (ConnectionCheck.connectionStatus(this)) {

                    Progress.showProgress(this);

                    jsonObject = new JSONObject();
                    try {
                        jsonObject.put("FromEmailID", UserDetails.EmailID);
                        jsonObject.put("ToEmailID", getIntent().getStringExtra("Email"));

                        Log.d("MeetingStatusParams", jsonObject.toString());

                        new CheckMeetingStatus(ApiUrl.GetMeetingStatus, jsonObject.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Progress.closeProgress();
                    }


                } else {
                    Toast.makeText(this, "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private class CheckMeetingStatus {

        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        private CheckMeetingStatus(String url, String postBody) {


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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Progress.closeProgress();
                        }

                    });

                    if (response.isSuccessful()) {

                        String myResponse = response.body().string();

                        Log.d("MeetingStatusRes", myResponse);

                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(myResponse);
                            if (jsonArray.length() > 0) {

                                JSONObject jObj = jsonArray.getJSONObject(0);

                                hourStr = jObj.getString("Hours");
                                if (hourStr.length() == 1) {
                                    hourStr = "0" + hourStr;
                                }

                                minuteStr = jObj.getString("Minutes");
                                if (minuteStr.length() == 1) {
                                    minuteStr = "0" + minuteStr;
                                }

                                monthStr = new DateFormatSymbols().getMonths()[Integer.parseInt(jObj.getString("Month")) - 1];
                                monthStr = monthStr.substring(0, 3);
                                dayStr = jObj.getString("Day");
                                if (dayStr.length() == 1) {
                                    dayStr = "0" + dayStr;
                                }

                                getSupportFragmentManager().beginTransaction().
                                        replace(R.id.container, new com.kestone.dellpartnersummit.Fragments.ScheduledMeetingFragment())
                                        .commit();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        bottomLl.setVisibility(View.VISIBLE);
                                        mScheduleBtn.setVisibility(View.GONE);
                                    }
                                });

                            } else {
                                getSupportFragmentManager().beginTransaction().
                                        replace(R.id.container, new com.kestone.dellpartnersummit.Fragments.SpeakerDetails())
                                        .commit();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        bottomLl.setVisibility(View.GONE);
                                        mScheduleBtn.setVisibility(View.VISIBLE);
                                    }
                                });

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SpeakerDetails.this, response.message(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

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
                    runOnUiThread(new Runnable() {
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
                                if (ConnectionCheck.connectionStatus(SpeakerDetails.this)) {

                                   runOnUiThread(new Runnable() {
                                       @Override
                                       public void run() {
                                           Toast.makeText(SpeakerDetails.this, "Meeting Cancelled", Toast.LENGTH_SHORT).show();

                                           Progress.showProgress(SpeakerDetails.this);
                                       }
                                   });

                                    jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("FromEmailID", UserDetails.EmailID);
                                        jsonObject.put("ToEmailID", getIntent().getStringExtra("Email"));

                                        Log.d("MeetingStatusParams", jsonObject.toString());

                                        new CheckMeetingStatus(ApiUrl.GetMeetingStatus, jsonObject.toString());

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Progress.closeProgress();
                                    }


                                } else {
                                    Toast.makeText(SpeakerDetails.this, "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SpeakerDetails.this, response.message(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            });
        }
    }

}
