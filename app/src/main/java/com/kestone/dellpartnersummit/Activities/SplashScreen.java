package com.kestone.dellpartnersummit.Activities;

import android.Manifest;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.kestone.dellpartnersummit.PoJo.UserDetails;
import com.kestone.dellpartnersummit.R;
import com.master.permissionhelper.PermissionHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreen extends AppCompatActivity {
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // setContentView(R.layout.activity_splash_screen);

        SharedPreferences mShrd = getSharedPreferences("GotIt", MODE_PRIVATE);
        String mFlag = mShrd.getString("Flag", "");
        Log.d("Flag",mFlag);

        SharedPreferences sharedPrefrence = getSharedPreferences("User", MODE_PRIVATE);
        String userDetails = sharedPrefrence.getString("UserDetails", "");
        if (userDetails.length() > 0) {

            try {
                JSONArray jsonArray = new JSONArray(userDetails);
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

                                    PermissionHelper permissionHelper = new PermissionHelper(SplashScreen.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                                    permissionHelper.request(new PermissionHelper.PermissionCallback() {
                                        @Override
                                        public void onPermissionGranted() {

                                            View easySplashScreenView = new EasySplashScreen(SplashScreen.this)
                                                    .withFullScreen()
                                                    .withTargetActivity(AttendanceActivity.class)
                                                    .withSplashTimeOut(4000)
                                                    .withBackgroundResource(R.drawable.splash)
                                                    .withHeaderText("")
                                                    .withFooterText("")
                                                    .withBeforeLogoText("")
                                                    // .withLogo(R.drawable.app_logo)
                                                    .withAfterLogoText("")
                                                    .create();

                                            setContentView(easySplashScreenView);

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

                                    PermissionHelper permissionHelper = new PermissionHelper(SplashScreen.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                                    permissionHelper.request(new PermissionHelper.PermissionCallback() {
                                        @Override
                                        public void onPermissionGranted() {

                                            View easySplashScreenView = new EasySplashScreen(SplashScreen.this)
                                                    .withFullScreen()
                                                    .withTargetActivity(AttendanceActivity.class)
                                                    .withSplashTimeOut(4000)
                                                    .withBackgroundResource(R.drawable.splash)
                                                    .withHeaderText("")
                                                    .withFooterText("")
                                                    .withBeforeLogoText("")
                                                    // .withLogo(R.drawable.app_logo)
                                                    .withAfterLogoText("")
                                                    .create();

                                            setContentView(easySplashScreenView);

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


                            PermissionHelper permissionHelper = new PermissionHelper(SplashScreen.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                            permissionHelper.request(new PermissionHelper.PermissionCallback() {
                                @Override
                                public void onPermissionGranted() {

                                    View easySplashScreenView = new EasySplashScreen(SplashScreen.this)
                                            .withFullScreen()
                                            .withTargetActivity(AttendanceActivity.class)
                                            .withSplashTimeOut(4000)
                                            .withBackgroundResource(R.drawable.splash)
                                            .withHeaderText("")
                                            .withFooterText("")
                                            .withBeforeLogoText("")
                                            // .withLogo(R.drawable.app_logo)
                                            .withAfterLogoText("")
                                            .create();

                                    setContentView(easySplashScreenView);

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

                        View easySplashScreenView = new EasySplashScreen(SplashScreen.this)
                                .withFullScreen()
                                .withTargetActivity(MainActivity.class)
                                .withSplashTimeOut(4000)
                                .withBackgroundResource(R.drawable.splash)
                                .withHeaderText("")
                                .withFooterText("")
                                .withBeforeLogoText("")
                                // .withLogo(R.drawable.app_logo)
                                .withAfterLogoText("")
                                .create();

                        setContentView(easySplashScreenView);
                    }
                } else {

                    SharedPreferences shrd = getSharedPreferences("GotIt", MODE_PRIVATE);
                    String Flag = shrd.getString("Flag", "");

                    if (Flag.equals("Y")) {

                        View easySplashScreenView = new EasySplashScreen(SplashScreen.this)
                                .withFullScreen()
                                .withTargetActivity(MainActivity.class)
                                .withSplashTimeOut(4000)
                                .withBackgroundResource(R.drawable.splash)
                                .withHeaderText("")
                                .withFooterText("")
                                .withBeforeLogoText("")
                                // .withLogo(R.drawable.app_logo)
                                .withAfterLogoText("")
                                .create();

                        setContentView(easySplashScreenView);

                    }else {

                        View easySplashScreenView = new EasySplashScreen(SplashScreen.this)
                                .withFullScreen()
                                .withTargetActivity(GotItActivity.class)
                                .withSplashTimeOut(4000)
                                .withBackgroundResource(R.drawable.splash)
                                .withHeaderText("")
                                .withFooterText("")
                                .withBeforeLogoText("")
                                // .withLogo(R.drawable.app_logo)
                                .withAfterLogoText("")
                                .create();

                        setContentView(easySplashScreenView);

                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

            View easySplashScreenView = new EasySplashScreen(SplashScreen.this)
                    .withFullScreen()
                    .withTargetActivity(LoginActivity.class)
                    .withSplashTimeOut(4000)
                    .withBackgroundResource(R.drawable.splash)
                    .withHeaderText("")
                    .withFooterText("")
                    .withBeforeLogoText("")
                    // .withLogo(R.drawable.app_logo)
                    .withAfterLogoText("")
                    .create();

            setContentView(easySplashScreenView);
        }

    }

}

