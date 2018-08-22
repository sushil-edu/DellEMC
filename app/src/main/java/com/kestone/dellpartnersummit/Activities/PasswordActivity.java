package com.kestone.dellpartnersummit.Activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.kestone.dellpartnersummit.ApiHandler.ApiResponse;
import com.kestone.dellpartnersummit.ApiHandler.ApiUrl;
import com.kestone.dellpartnersummit.ApiHandler.PostData;
import com.kestone.dellpartnersummit.ConnectionCheck.ConnectionCheck;
import com.kestone.dellpartnersummit.PoJo.UserDetails;
import com.kestone.dellpartnersummit.ProgressView.Progress;
import com.kestone.dellpartnersummit.R;
import com.master.permissionhelper.PermissionHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PasswordActivity extends AppCompatActivity implements ApiResponse {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);


        //FirebaseAnalytics initialization
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        final EditText passwordEt = (EditText) findViewById(R.id.passwordEt);

        findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (passwordEt.getText().length() > 0) {

                    if (passwordEt.getText().toString().equals(getIntent().getStringExtra("Otp"))) {

                        SharedPreferences pref = getApplicationContext().getSharedPreferences("RegId", 0);
                        String regId = pref.getString("regId", null);

                        Progress.showProgress(PasswordActivity.this);

                        JSONObject jsonObject = new JSONObject();
                        try {

                            jsonObject.put("EmailID", getIntent().getStringExtra("Email"));
                            jsonObject.put("DeviceID", regId);
                            jsonObject.put("DeviceType", "Android");

                            Log.d("DeviceId Params", jsonObject.toString());

                            if (ConnectionCheck.connectionStatus(PasswordActivity.this)) {
                                new UpdateDeviceId(ApiUrl.UpdateDeviceID, jsonObject.toString());
                            } else {
                                Progress.closeProgress();
                                Toast.makeText(PasswordActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Progress.closeProgress();
                        }

                    } else
                        Toast.makeText(PasswordActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();

                } else
                    Toast.makeText(PasswordActivity.this, "Fill All Details", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onApiResponse(final Response response) {

        Progress.closeProgress();

        if (response.isSuccessful()) {

            try {
                String myResponse = response.body().string();
                Log.d("Login Response", myResponse);

                SharedPreferences sharedPrefrence = getSharedPreferences("User", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPrefrence.edit();
                editor.putString("UserDetails", myResponse);
                editor.apply();

                try {
                    JSONArray jsonArray = new JSONArray(myResponse);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);


                    UserDetails.setID(jsonObject.getString("ID"));
                    UserDetails.setName(jsonObject.getString("Name"));
                    UserDetails.setDesignation(jsonObject.getString("Designation"));
                    UserDetails.setOrganization(jsonObject.getString("Organization"));
                    UserDetails.setEmailID(jsonObject.getString("EmailID"));
                    UserDetails.setMobile(jsonObject.getString("Mobile"));
                    UserDetails.setPassportNo(jsonObject.getString("PassportNo"));
                    UserDetails.setRegistrationType(jsonObject.getString("RegistrationType"));
                    UserDetails.setImageURL(jsonObject.getString("ImageURL"));
                    UserDetails.setIsCheckedIn(jsonObject.getString("IsCheckedIn"));
                    UserDetails.setUserType(jsonObject.getString("UserType"));
                    UserDetails.setUniqueID(jsonObject.getString("UniqueID"));
                    UserDetails.setCheckinStartDatetime(jsonObject.getString("CheckinStartDatetime"));
                    UserDetails.setCheckinEndDatetime(jsonObject.getString("CheckinEndDatetime"));
                    UserDetails.setIsVenue500Applicable(jsonObject.getString("IsVenue500Applicable"));
                    UserDetails.setVenueLatitude(jsonObject.getString("VenueLatitude"));
                    UserDetails.setVenueLongitude(jsonObject.getString("VenueLongitude"));
                    UserDetails.setVenueName(jsonObject.getString("VenueName"));
                    UserDetails.setActivationDays(jsonObject.getString("ActivationDays"));
                    UserDetails.setIsTrackApplicable(jsonObject.getString("IsTrackApplicable"));
                    UserDetails.setIsChekinRequired(jsonObject.getString("IsChekinRequired"));
                    UserDetails.setIsPriorityCheckin(jsonObject.getString("IsPriorityCheckin"));
                    UserDetails.setPCStart(jsonObject.getString("PCStart"));
                    UserDetails.setPCEnd(jsonObject.getString("PCEnd"));
                    UserDetails.setPriorityMsg(jsonObject.getString("PriorityMsg"));
                    UserDetails.setWelcomemsg(jsonObject.getString("Welcomemsg"));

//                    Time today = new Time(Time.getCurrentTimezone());
//                    today.setToNow();
//                    Log.d("DATE--> ", today.monthDay + ", " + today.month + ", " + today.year);
//                    int month = today.month + 1;
//                    int day = today.monthDay;
//                    int year = today.year;
//                    String monthStr, dayStr;
//
//                    if (month < 10) {
//                        monthStr = "0" + month;
//                    } else {
//                        monthStr = month + "";
//                    }
//
//                    if (day < 10) {
//                        dayStr = "0" + day;
//                    } else {
//                        dayStr = day + "";
//                    }
//
//                    String dateStr = year + "-" + monthStr + "-" + dayStr;
//
//                    Log.d("dateStr", dateStr);

//                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    String date = dateFormat.format(Calendar.getInstance().getTime());
//
//                    Date convertedDate = new Date();
//                    Date startDate = new Date();
//                    Date endDate = new Date();
//                    try {
//                        convertedDate = dateFormat.parse(date);
//                        startDate = dateFormat.parse(UserDetails.getCheckinStartDatetime());
//                        endDate = dateFormat.parse(UserDetails.getCheckinEndDatetime());
//
//                    } catch (ParseException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//
//                    Log.d("convertedDate", convertedDate + "");
//                    Log.d("startDate", startDate + "");
//                    Log.d("endDate", endDate + "");
//
//                    Log.d("startDate", date);
//                    Log.d("getCheckinStartDatetime", UserDetails.getCheckinStartDatetime());
//                    Log.d("getCheckinEndDatetime", UserDetails.getCheckinEndDatetime());
//
//
//                    if (jsonObject.getString("IsCheckedIn").equals("N")
//                            && startDate.compareTo(convertedDate) * convertedDate.compareTo(endDate) >= 0) {
//
//
//                        Bundle bundle = new Bundle();
//                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, jsonObject.getString("EmailID"));
//                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, jsonObject.getString("Name"));
//                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "LogIn");
//                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
//
//                        PermissionHelper permissionHelper = new PermissionHelper(PasswordActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
//                                Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
//                        permissionHelper.request(new PermissionHelper.PermissionCallback() {
//                            @Override
//                            public void onPermissionGranted() {
//
//
//                                Intent intent = new Intent(PasswordActivity.this, AttendanceActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(intent);
//                                finish();
//
//                            }
//
//                            @Override
//                            public void onPermissionDenied() {
//                                Log.d("Permission", "onPermissionDenied() called");
//                            }
//
//                            @Override
//                            public void onPermissionDeniedBySystem() {
//                                Log.d("Permission", "onPermissionDeniedBySystem() called");
//                            }
//                        });
//
//
//                    } else {
//
//                        Bundle bundle = new Bundle();
//                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, jsonObject.getString("EmailID"));
//                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, jsonObject.getString("Name"));
//                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "LogIn");
//                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
//
//                        Intent intent = new Intent(PasswordActivity.this, MainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                        finish();
//                    }

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


                    if (UserDetails.getIsChekinRequired().equals("Y")) {

                        if (jsonObject.getString("IsCheckedIn").equals("N") &&
                                startDate.compareTo(convertedDate) * convertedDate.compareTo(endDate) >= 0) {

                            if (UserDetails.getIsPriorityCheckin().equals("Y")) {


                                try {
                                    Calendar calander = Calendar.getInstance();
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                                    String time = simpleDateFormat.format(calander.getTime());

                                    Date time1 = new SimpleDateFormat("HH:mm").parse(time);
                                    Calendar calendar1 = Calendar.getInstance();
                                    calendar1.setTime(time1);


                                    Date time0 = new SimpleDateFormat("HH:mm").parse(UserDetails.getPCStart());
                                    Calendar calendar0 = Calendar.getInstance();
                                    calendar0.setTime(time0);

                                    Date time2 = new SimpleDateFormat("HH:mm").parse(UserDetails.getPCEnd());
                                    Calendar calendar2 = Calendar.getInstance();
                                    calendar2.setTime(time2);

                                    if (time1.after(time0) && time1.before(time2)) {

                                        PermissionHelper permissionHelper = new PermissionHelper(PasswordActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                                        permissionHelper.request(new PermissionHelper.PermissionCallback() {
                                            @Override
                                            public void onPermissionGranted() {


                                                Intent intent = new Intent(PasswordActivity.this, AttendanceActivity.class);
                                                startActivity(intent);
                                                finish();

                                            }

                                            @Override
                                            public void onPermissionDenied() {
                                                Log.d("Permission", "onPermissionDenied() called");
                                            }

                                            @Override
                                            public void onPermissionDeniedBySystem() {
                                                Log.d("Permission", "onPermissionDeniedBySystem() called");
                                            }
                                        });


                                    } else {

                                        PermissionHelper permissionHelper = new PermissionHelper(PasswordActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                                        permissionHelper.request(new PermissionHelper.PermissionCallback() {
                                            @Override
                                            public void onPermissionGranted() {

                                                Intent intent = new Intent(PasswordActivity.this, AttendanceActivity.class);
                                                startActivity(intent);
                                                finish();

                                            }

                                            @Override
                                            public void onPermissionDenied() {
                                                Log.d("Permission", "onPermissionDenied() called");
                                            }

                                            @Override
                                            public void onPermissionDeniedBySystem() {
                                                Log.d("Permission", "onPermissionDeniedBySystem() called");
                                            }
                                        });

                                    }


                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }


                            } else {


                                PermissionHelper permissionHelper = new PermissionHelper(PasswordActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                                permissionHelper.request(new PermissionHelper.PermissionCallback() {
                                    @Override
                                    public void onPermissionGranted() {


                                        Intent intent = new Intent(PasswordActivity.this, AttendanceActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }

                                    @Override
                                    public void onPermissionDenied() {
                                        Log.d("Permission", "onPermissionDenied() called");
                                    }

                                    @Override
                                    public void onPermissionDeniedBySystem() {
                                        Log.d("Permission", "onPermissionDeniedBySystem() called");
                                    }
                                });

                            }


                        } else {


                            Intent intent = new Intent(PasswordActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    } else {

                            Intent intent = new Intent(PasswordActivity.this, GotItActivity.class);
                            startActivity(intent);
                            finish();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PasswordActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private class UpdateDeviceId {

        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        private UpdateDeviceId(String url, String postBody) {

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

                        String myReponse = response.body().string();

                        Log.d("ResDeviceId", myReponse);

                        try {
                            JSONObject jObj = new JSONObject(myReponse);

                            if (jObj.getString("retval").equals("Device ID updated successfully.")) {


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Progress.showProgress(PasswordActivity.this);
                                    }
                                });

                                JSONObject jsonObject = new JSONObject();
                                try {

                                    jsonObject.put("Username", getIntent().getStringExtra("Email"));

                                    Log.d("LogIn Params", jsonObject.toString());

                                    if (ConnectionCheck.connectionStatus(PasswordActivity.this)) {
                                        new PostData(ApiUrl.Login, jsonObject.toString(), PasswordActivity.this);
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Progress.closeProgress();
                                                Toast.makeText(PasswordActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Progress.closeProgress();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PasswordActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                }
            });
        }
    }

}
