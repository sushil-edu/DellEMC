package com.kestone.dellpartnersummit.ApiHandler;

import android.app.Activity;
import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetData {
    private String url;
    private Activity activity;
    private ApiResponse apiResponse;

    public GetData(String url, Activity activity) {
        this.url = url;
        this.activity = activity;
        this.apiResponse=(ApiResponse)activity;

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

                apiResponse.onApiResponse(response);

            }
        });
    }

}

