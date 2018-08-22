package com.kestone.dellpartnersummit.Fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kestone.dellpartnersummit.ApiHandler.ApiUrl;
import com.kestone.dellpartnersummit.ConnectionCheck.ConnectionCheck;
import com.kestone.dellpartnersummit.ProgressView.Progress;
import com.kestone.dellpartnersummit.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class PollsFragment extends Fragment {

    private String S1 = "", S2 = "", S3 = "", S4 = "", S5 = "", S6 = "", S7 = "";

    public PollsFragment() {

    }

    @SuppressLint("ValidFragment")
    public PollsFragment(String S1, String S2, String S3, String S4, String S5, String S6, String S7) {
        // Required empty public constructor

        this.S1 = S1;
        this.S2 = S2;
        this.S3 = S3;
        this.S4 = S4;
        this.S5 = S5;
        this.S6 = S6;
        this.S7 = S7;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_polls, container, false);

        Button mBtnCancel = (Button) view.findViewById(R.id.mBtnCancel);
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new SubmittedPollFragment()).commit();
            }
        });

        final EditText optionA = (EditText) view.findViewById(R.id.optionA);
        final EditText optionB = (EditText) view.findViewById(R.id.optionB);
        final EditText optionC = (EditText) view.findViewById(R.id.optionC);
        final EditText optionD = (EditText) view.findViewById(R.id.optionD);
        Button createPollBtn = (Button) view.findViewById(R.id.createPollBtn);

        final TextView questionLimitTv = (TextView) view.findViewById(R.id.quetionLimitTv);
        final TextView titleLimitTv = (TextView) view.findViewById(R.id.titleLimitTV);
        final EditText pollTitleEt = (EditText) view.findViewById(R.id.pollTitleEt);
        final EditText pollQuestionEt = (EditText) view.findViewById(R.id.pollQuestionEt);


        pollTitleEt.setText(S1);
        pollQuestionEt.setText(S2);
        optionA.setText(S3);
        optionB.setText(S4);
        optionC.setText(S5);
        optionD.setText(S6);


        createPollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (optionA.getText().length() > 0 && optionB.getText().length() > 0 && optionC.getText().length() > 0
                        && optionD.getText().length() > 0 && pollTitleEt.getText().length() > 0 && pollQuestionEt.getText().length() > 0) {

                    Progress.showProgress(getActivity());
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("Title", pollTitleEt.getText().toString());
                        jsonObject.put("Question", pollQuestionEt.getText().toString());
                        jsonObject.put("Option1", optionA.getText().toString());
                        jsonObject.put("Option2", optionB.getText().toString());
                        jsonObject.put("Option3", optionC.getText().toString());
                        jsonObject.put("Option4", optionD.getText().toString());

                        if (ConnectionCheck.connectionStatus(getActivity())) {
                            if (S1.length() == 0) {

                                Log.d("SubmitPollParams", jsonObject.toString());
                                new SubmitPoll(ApiUrl.CreatePoll, jsonObject.toString());
                            } else {
                                jsonObject.put("ID", S7);
                                jsonObject.put("IsActive", "N");

                                Log.d("EditPollParams", jsonObject.toString());

                                new EditPoll(ApiUrl.EditPoll, jsonObject.toString());
                            }
                        } else {
                            Progress.closeProgress();
                            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else Toast.makeText(getActivity(), "Fill All Details", Toast.LENGTH_SHORT).show();
            }
        });


        pollTitleEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = 50 - s.length();
                titleLimitTv.setText(length + " words left");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        pollQuestionEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = 200 - s.length();
                questionLimitTv.setText(length + " words left");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return view;
    }

    class SubmitPoll {

        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        SubmitPoll(String url, String postBody) {

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

                        Log.d("SubmitPollRes", myReponse);

                        try {
                            JSONObject jsonObj = new JSONObject(myReponse);
                            if (jsonObj.getString("retval").equals("Question submitted successfully")) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "Poll Created", Toast.LENGTH_SHORT).show();
                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.container, new SubmittedPollFragment()).commit();
                                    }
                                });


                            } else {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "Not Successfull", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                }
            });
        }
    }


    class EditPoll {

        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        EditPoll(String url, String postBody) {

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

                        Log.d("EditPollRes", myReponse);

                        try {
                            JSONObject jsonObj = new JSONObject(myReponse);
                            if (jsonObj.getString("retval").equals("Updated Succesfully")) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "Poll Created", Toast.LENGTH_SHORT).show();
                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.container, new SubmittedPollFragment()).commit();
                                    }
                                });


                            } else {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "Not Successfull", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                }
            });
        }
    }

}
