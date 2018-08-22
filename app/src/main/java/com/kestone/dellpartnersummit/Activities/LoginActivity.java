package com.kestone.dellpartnersummit.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kestone.dellpartnersummit.ApiHandler.ApiResponse;
import com.kestone.dellpartnersummit.ApiHandler.ApiUrl;
import com.kestone.dellpartnersummit.ApiHandler.PostData;
import com.kestone.dellpartnersummit.ConnectionCheck.ConnectionCheck;
import com.kestone.dellpartnersummit.ProgressView.Progress;
import com.kestone.dellpartnersummit.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements ApiResponse{

    EditText emailEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEt = (EditText) findViewById(R.id.emailEt);
        findViewById(R.id.verifyBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailEt.getText().length() > 0) {

                    Progress.showProgress(LoginActivity.this);

                    JSONObject jsonObject = new JSONObject();
                    try {

                        Random ran = new Random();
                        String code= (100000 + ran.nextInt(900000))+"";

                        jsonObject.put("EmailID", emailEt.getText().toString());

                        Log.d("Email Validation Param",jsonObject.toString());

                        if(ConnectionCheck.connectionStatus(LoginActivity.this)){

                            new PostData(ApiUrl.ValidateEmailOTP, jsonObject.toString(), LoginActivity.this);


                        } else {
                            Toast.makeText(LoginActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                            Progress.closeProgress();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Progress.closeProgress();
                    }

                } else
                    Toast.makeText(LoginActivity.this, "Fill Details First", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onApiResponse(final Response response) {

        Progress.closeProgress();

        if(response.isSuccessful()){

            try {
                String myResponse = response.body().string();
                Log.d("Email Validation Resp",myResponse);

                try {
                    JSONObject jsonObject = new JSONObject(myResponse);

                    if(jsonObject.getString("retval").equals("Invalid EmailID.")){

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Email ID is not registered", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }else {

                        Intent intent = new Intent(LoginActivity.this,PasswordActivity.class);
                        intent.putExtra("Otp",jsonObject.getString("retval"));
                        intent.putExtra("Email",emailEt.getText().toString());
                        startActivity(intent);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
