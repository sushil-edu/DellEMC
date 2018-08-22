package com.kestone.dellpartnersummit.ApiHandler;

import android.app.Activity;
import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostData {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType IMAGE = MediaType.parse("image/png");
    private Activity activity;
    private ApiResponse apiResponse;;

    public PostData(String url, String postBody, Activity activity) {

        this.activity = activity;
        this.apiResponse=(ApiResponse)activity;

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
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                apiResponse.onApiResponse(response);

            }
        });
    }
}


