package com.kestone.dellpartnersummit.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fusedbulblib.GetCurrentLocation;
import com.fusedbulblib.interfaces.GpsOnListner;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
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

import okhttp3.Response;

public class AttendanceActivity extends AppCompatActivity implements ApiResponse, GpsOnListner {

    private GoogleApiClient mGoogleApiClient;
    final static int REQUEST_CHECK_SETTINGS = 199;
    private TextView checkinRl, skipTv;
    private ImageView venuePin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        TextView venueTv = (TextView) findViewById(R.id.venueTv);

        checkinRl = (TextView) findViewById(R.id.checkinRl);
        skipTv = (TextView) findViewById(R.id.skipTv);
        venuePin = (ImageView) findViewById(R.id.venuePin);

        if (UserDetails.getIsVenue500Applicable().equals("Y") && UserDetails.getIsChekinRequired().equals("Y")) {

            mGoogleApiClient = new GoogleApiClient.Builder(AttendanceActivity.this)
                    .addApi(LocationServices.API).build();
            mGoogleApiClient.connect();

            Progress.showProgress(AttendanceActivity.this);

            new GetCurrentLocation(AttendanceActivity.this).getCurrentLocation();

            skipTv.setVisibility(View.GONE);
            venuePin.setVisibility(View.VISIBLE);
            venueTv.setText(UserDetails.getVenueName());
            checkinRl.setVisibility(View.VISIBLE);

        } else {

            if (UserDetails.getIsChekinRequired().equals("Y")) {

                skipTv.setVisibility(View.GONE);
                venuePin.setVisibility(View.VISIBLE);
                venueTv.setText(UserDetails.getVenueName());
                checkinRl.setVisibility(View.VISIBLE);

            } else {
                skipTv.setVisibility(View.VISIBLE);
                checkinRl.setVisibility(View.GONE);
                venuePin.setVisibility(View.GONE);
            }
        }

        checkinRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ConnectionCheck.connectionStatus(AttendanceActivity.this)) {

                    JSONObject jObj = new JSONObject();
                    try {
                        jObj.put("EmailID", UserDetails.getEmailID());
                        Progress.showProgress(AttendanceActivity.this);
                        new PostData(ApiUrl.CheckIn, jObj.toString(), AttendanceActivity.this);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(AttendanceActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        skipTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendanceActivity.this, GotItActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onApiResponse(final Response response) {
        Progress.closeProgress();
        if (response.isSuccessful()) {
            try {
                String myResponse = response.body().string();
                Log.d("checkInResponse", myResponse);

                JSONArray jsonArray = new JSONArray(myResponse);

                if (jsonArray.length() > 0) {

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
//
                    SharedPreferences sharedPrefrence = getSharedPreferences("User", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPrefrence.edit();
                    editor.putString("UserDetails", myResponse);
                    editor.apply();

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

                                PermissionHelper permissionHelper = new PermissionHelper(AttendanceActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                                permissionHelper.request(new PermissionHelper.PermissionCallback() {
                                    @Override
                                    public void onPermissionGranted() {

                                        Intent intent = new Intent(AttendanceActivity.this, EarlyBird.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

                                PermissionHelper permissionHelper = new PermissionHelper(AttendanceActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                                permissionHelper.request(new PermissionHelper.PermissionCallback() {
                                    @Override
                                    public void onPermissionGranted() {

                                        Intent intent = new Intent(AttendanceActivity.this, GotItActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

                        Intent intent = new Intent(AttendanceActivity.this, GotItActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    }


                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AttendanceActivity.this, "Check-In unsuccessfull", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AttendanceActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void gpsStatus(boolean _status) {
        if (_status) {
            Progress.showProgress(AttendanceActivity.this);
            new GetCurrentLocation(this).getCurrentLocation();
        } else {
            //GPS is off on user's device.
            enableLoc();
        }
    }

    @Override
    public void gpsPermissionDenied(int deviceGpsStatus) {
        if (deviceGpsStatus == 1) {
            //user does not want to switch on GPS of his/her device.
            Log.d("Location", "Not Allowed");
        }

    }

    @Override
    public void gpsLocationFetched(Location location) {
        Progress.closeProgress();
        if (location != null) {
            // you will get user's current location
            Log.d("Location", "Fetched");

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            double dist = distance(latitude, longitude, Double.parseDouble(UserDetails.getVenueLatitude()),
                    Double.parseDouble(UserDetails.getVenueLongitude()));

            Log.d("dist", dist + "");

            if (dist > 700) {
                skipTv.setVisibility(View.VISIBLE);
                checkinRl.setVisibility(View.GONE);
            } else {
                skipTv.setVisibility(View.GONE);
                checkinRl.setVisibility(View.VISIBLE);
            }


        } else {
            Toast.makeText(this, "Unable to find location", Toast.LENGTH_SHORT).show();

            skipTv.setVisibility(View.VISIBLE);
            checkinRl.setVisibility(View.GONE);

        }
    }

    private void enableLoc() {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(AttendanceActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        new GetCurrentLocation(this).getCurrentLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        enableLoc();//keep asking if imp or do whatever
                        break;
                }
                break;
        }


    }


    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}
