package com.kestone.dellpartnersummit.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.master.permissionhelper.PermissionHelper;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;
import com.kestone.dellpartnersummit.ApiHandler.ApiUrl;
import com.kestone.dellpartnersummit.ConnectionCheck.ConnectionCheck;
import com.kestone.dellpartnersummit.PoJo.UserDetails;
import com.kestone.dellpartnersummit.ProgressView.Progress;
import com.kestone.dellpartnersummit.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditProfile extends AppCompatActivity {

    private static final int SELECT_PHOTO = 0x01;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private CircularImageView profileIv;
    private TextView emailTv, uniqueIdTv, tv;
    private EditText nameTv, mobileTv, organizationTv, designationTv;
    private String str = "";
    private CheckBox checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ImageView mPassIv = (ImageView) findViewById(R.id.mPassIv);
        mPassIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        emailTv = (TextView) findViewById(R.id.emailEt);
        nameTv = (EditText) findViewById(R.id.nameTv);
        mobileTv = (EditText) findViewById(R.id.mobileTv);
        organizationTv = (EditText) findViewById(R.id.organizationTv);
        designationTv = (EditText) findViewById(R.id.designationTv);
        uniqueIdTv = (TextView) findViewById(R.id.uniqueIdTv);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        checkbox.setChecked(true);
        Button updateBtn = (Button) findViewById(R.id.updateBtn);

        nameTv.setText(UserDetails.getName());
        mobileTv.setText(UserDetails.getMobile());
        organizationTv.setText(UserDetails.getOrganization());
        designationTv.setText(UserDetails.getDesignation());
        profileIv = (CircularImageView) findViewById(R.id.profileIv);
        profileIv.setScaleType(CircularImageView.ScaleType.CENTER_CROP);
        uniqueIdTv.setText(UserDetails.getUniqueID());
        emailTv.setText(UserDetails.getEmailID());
        tv = (TextView) findViewById(R.id.tv);


        // this is the text we'll be operating on
        SpannableString text = new SpannableString("I agree to Terms & Conditions and Privacy Policy");

        // make "Lorem" (characters 0 to 5) red
        text.setSpan(new ForegroundColorSpan(Color.RED), 11, 29, 17);
        text.setSpan(new ForegroundColorSpan(Color.RED), 34, text.length(), 18);

        final Context context = this;
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "dolor", Toast.LENGTH_LONG).show();
            }
        };

        text.setSpan(new URLSpan("http://marketing.kestoneapps.com/Events/DellPartnerSummite2017/PP/index.html"), 11, 29, 17);
        text.setSpan(new URLSpan("http://india.emc.com/legal/emc-corporation-privacy-statement.htm"), 34, text.length(), 18);

        // make our ClickableSpans and URLSpans work
        tv.setMovementMethod(LinkMovementMethod.getInstance());

        // shove our styled text into the TextView
        tv.setText(text, TextView.BufferType.SPANNABLE);




        if(UserDetails.ImageURL.length()>0){
            Picasso.with(this)
                    .load(UserDetails.ImageURL)
                    .placeholder(R.drawable.ic_user)
                    .into(profileIv);

        }


        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionHelper permissionHelper = new PermissionHelper(EditProfile.this, new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                permissionHelper.request(new PermissionHelper.PermissionCallback() {
                    @Override
                    public void onPermissionGranted() {

                        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
                        builder.setMessage("Select Source");
                        builder.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                photoPickerIntent.setType("image/*");
                                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                            }
                        });

                        builder.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                }

                            }
                        });
                        builder.show();


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
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ConnectionCheck.connectionStatus(EditProfile.this)){
                    Progress.showProgress(EditProfile.this);
                    JSONObject jsonObject = new JSONObject();

                    try {
                        jsonObject.put("Name",nameTv.getText().toString());
                        jsonObject.put("Designation",designationTv.getText().toString());
                        jsonObject.put("Organization",organizationTv.getText().toString());
                        jsonObject.put("EmailID", UserDetails.EmailID);
                        jsonObject.put("Mobile",mobileTv.getText().toString());
                        if(str.length()>0){
                            jsonObject.put("ProfilePic",str);
                        }else {
                            jsonObject.put("ProfilePic","");
                        }

                        Log.d("UpdateProParams", jsonObject.toString());

                        new UpdateProfile(ApiUrl.UpdateProfile,jsonObject.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Progress.closeProgress();
                    }


                }else{
                    Toast.makeText(EditProfile.this, "No Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == SELECT_PHOTO) {
            final Uri resultUri = data.getData();
            try {

                Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] barray = stream.toByteArray();


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 10, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                Bitmap compressedImage = BitmapFactory.decodeByteArray(b, 0, b.length);

                str = Base64.encodeToString(b, Base64.DEFAULT);
                profileIv.setImageBitmap(compressedImage);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] barray = stream.toByteArray();


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 10, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            Bitmap compressedImage = BitmapFactory.decodeByteArray(b, 0, b.length);

            str = Base64.encodeToString(b, Base64.DEFAULT);
            profileIv.setImageBitmap(compressedImage);

        }
    }

    private class UpdateProfile {

        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        private UpdateProfile(String url, String postBody) {

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

                        Log.d("EditProfileRes", myReponse);

                        try {
                            JSONObject jsonObj = new JSONObject(myReponse);
                            if(jsonObj.getString("retval").equals("Profile updated successfully.")){

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(EditProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                JSONArray jsonArray = new JSONArray();
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("ID",UserDetails.getID());
                                jsonObject.put("Name",nameTv.getText().toString());
                                jsonObject.put("Designation",designationTv.getText().toString());
                                jsonObject.put("Organization",organizationTv.getText().toString());
                                jsonObject.put("EmailID",UserDetails.getEmailID());
                                jsonObject.put("Mobile",mobileTv.getText().toString());
                                jsonObject.put("PassportNo",UserDetails.getPassportNo());
                                jsonObject.put("RegistrationType",UserDetails.getRegistrationType());

                                UserDetails.setName(nameTv.getText().toString());
                                UserDetails.setMobile(mobileTv.getText().toString());
                                UserDetails.setDesignation(designationTv.getText().toString());
                                UserDetails.setOrganization(organizationTv.getText().toString());
                                if(jsonObj.getString("code").length()>4){
                                    UserDetails.setImageURL(jsonObj.getString("code"));
                                    jsonObject.put("ImageURL",jsonObj.getString("code"));
                                }else {
                                    jsonObject.put("ImageURL",UserDetails.getImageURL());
                                }

                                jsonObject.put("IsCheckedIn",UserDetails.getIsCheckedIn());
                                jsonObject.put("CheckinStartDatetime",UserDetails.getCheckinStartDatetime());
                                jsonObject.put("UserType",UserDetails.getUserType());
                                jsonObject.put("UniqueID",UserDetails.getUniqueID());
                                jsonObject.put("CheckinEndDatetime",UserDetails.getCheckinEndDatetime());
                                jsonObject.put("IsVenue500Applicable",UserDetails.getIsVenue500Applicable());
                                jsonObject.put("VenueLatitude",UserDetails.getVenueLatitude());
                                jsonObject.put("VenueLongitude",UserDetails.getVenueLongitude());
                                jsonObject.put("VenueName",UserDetails.getVenueName());
                                jsonObject.put("ActivationDays",UserDetails.getActivationDays());
                                jsonObject.put("IsTrackApplicable",UserDetails.getIsTrackApplicable());
                                jsonObject.put("IsChekinRequired",UserDetails.getIsChekinRequired());
                                jsonObject.put("IsPriorityCheckin",UserDetails.getIsPriorityCheckin());
                                jsonObject.put("PCStart",UserDetails.getPCStart());
                                jsonObject.put("PCEnd",UserDetails.getPCEnd());
                                jsonObject.put("PriorityMsg",UserDetails.getPriorityMsg());
                                jsonObject.put("Welcomemsg",UserDetails.getWelcomemsg());

                                jsonArray.put(jsonObject);

                                SharedPreferences sharedPrefrence = getSharedPreferences("User", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPrefrence.edit();
                                editor.putString("UserDetails", jsonArray.toString());
                                editor.apply();

                                Intent intent = new Intent(EditProfile.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(EditProfile.this, "Profile Not Updated", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(EditProfile.this, response.message(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                }
            });
        }
    }

}
