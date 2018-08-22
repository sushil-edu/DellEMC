package com.kestone.dellpartnersummit.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kestone.dellpartnersummit.widget.MyEditText;
import com.master.permissionhelper.PermissionHelper;
import com.kestone.dellpartnersummit.ApiHandler.ApiUrl;
import com.kestone.dellpartnersummit.ConnectionCheck.ConnectionCheck;
import com.kestone.dellpartnersummit.PoJo.UserDetails;
import com.kestone.dellpartnersummit.ProgressView.Progress;
import com.kestone.dellpartnersummit.R;

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


public class AddActivity extends AppCompatActivity {

    private ImageView addImageView, checkinIcon;
    private TextView checkInIv;
    private MyEditText txtFeedback;
    private static final int SELECT_PHOTO = 0x01;
    private static final int SELECT_QR_CODE = 102;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    public static String status;
    private String str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        status = "";

//        RadioButton imgRadioButton = (RadioButton) findViewById(R.id.imgRadioBtn);
//        RadioButton txtRadioButton = (RadioButton) findViewById(R.id.textRadioBtn);
        final CardView textCard = (CardView) findViewById(R.id.textCard);
        final RelativeLayout imageCard = (RelativeLayout) findViewById(R.id.imageCard);
        checkInIv = (TextView) findViewById(R.id.checkInIv);
        txtFeedback = (MyEditText) findViewById(R.id.txtFeedback);
        checkinIcon = (ImageView) findViewById(R.id.checkinIcon);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");


        addImageView = (ImageView) findViewById(R.id.cameraImage);
        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionHelper permissionHelper = new PermissionHelper(AddActivity.this, new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                permissionHelper.request(new PermissionHelper.PermissionCallback() {
                    @Override
                    public void onPermissionGranted() {

                        AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
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

        checkInIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddActivity.this, CheckIn.class);
                startActivityForResult(intent, SELECT_QR_CODE);

            }
        });

        findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (addImageView.getDrawable() == null && checkInIv.getText().length() == 0 && txtFeedback.getText().length() == 0) {

                    Toast.makeText(AddActivity.this, "All fields can't be empty", Toast.LENGTH_SHORT).show();

                } else {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        Progress.showProgress(AddActivity.this);

                        jsonObject.put("RefEmailID", UserDetails.EmailID);
                        jsonObject.put("PostedText", txtFeedback.getText().toString());
                        jsonObject.put("BoothCheckIn", checkInIv.getText().toString());
                        if (addImageView.getDrawable() != null) {
                            jsonObject.put("PostedImage", str);
                        } else {
                            jsonObject.put("PostedImage", "");
                        }

                        Log.d("Add Activity Params", jsonObject.toString());

                        if (ConnectionCheck.connectionStatus(AddActivity.this)) {
                            new PostActivity(ApiUrl.ActivityStream, jsonObject.toString());
                        } else {
                            Progress.closeProgress();
                            Toast.makeText(AddActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Progress.closeProgress();
                    }

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
                 addImageView.setImageBitmap(compressedImage);


            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 10, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            Bitmap compressedImage = BitmapFactory.decodeByteArray(b, 0, b.length);

            str = Base64.encodeToString(b, Base64.DEFAULT);
            addImageView.setImageBitmap(compressedImage);

        } else if (resultCode == RESULT_OK && requestCode == SELECT_QR_CODE) {
            String boothStr = data.getStringExtra("boothStr");

            if (boothStr.length() > 0) {
                checkInIv.setText(boothStr);
                checkinIcon.setVisibility(View.GONE);
            } else {
                checkinIcon.setVisibility(View.VISIBLE);
            }

        }


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        status = "No";

    }

    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("Status", status);
        setResult(RESULT_OK, returnIntent);
        super.finish();
    }


    class PostActivity {

        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        PostActivity(String url, String postBody) {

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
                        String myResponse = response.body().string();

                        Log.d("AddActivity Response", myResponse);
                        try {
                            JSONObject jsonObject = new JSONObject(myResponse);
                            if (jsonObject.getString("retval").equals("Post submitted successfully")) {
                                status = "Yes";
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AddActivity.this, "Submitted Scuccessfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AddActivity.this, "Error in submitting", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(AddActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }


}
