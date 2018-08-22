package com.kestone.dellpartnersummit.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kestone.dellpartnersummit.ApiHandler.ApiUrl;
import com.kestone.dellpartnersummit.ConnectionCheck.ConnectionCheck;
import com.kestone.dellpartnersummit.Fragments.PollsFragment;
import com.kestone.dellpartnersummit.Fragments.SubmittedPollFragment;
import com.kestone.dellpartnersummit.PoJo.PollsData;
import com.kestone.dellpartnersummit.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class PollsAdapter extends RecyclerView.Adapter<PollsAdapter.MyHolder> {

    private Context context;
    private Activity activity;
    private ArrayList<PollsData> pollsList;



    public PollsAdapter(Context context, ArrayList<PollsData> pollsList) {
        this.context = context;
        this.activity = (Activity) context;
        this.pollsList = pollsList;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.polls_cell, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {

        final PollsData pollsData = pollsList.get(position);
        if (pollsData.getIsActive().equals("Y")) {
            holder.pollSwitch.setChecked(true);
        } else {
            holder.pollSwitch.setChecked(false);
        }
        holder.titleTv.setText(pollsData.getTitle());
        holder.questionTv.setText(pollsData.getQuestion());

        holder.pollSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

//                        LayoutInflater factory = LayoutInflater.from(context);
//                        final View deleteDialogView = factory.inflate(R.layout.poll_activation_alert, null);
//                        final AlertDialog deleteDialog = new AlertDialog.Builder(context).create();
//                        deleteDialog.setView(deleteDialogView);
//                        deleteDialog.show();
                    PollsData pollsData = pollsList.get(position);

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("ID", pollsData.getID());
                        jsonObject.put("Status", "Activate");
                        if (ConnectionCheck.connectionStatus(context)) {
                            new ActivatePoll(ApiUrl.ActivatePoll, jsonObject.toString(), holder.pollSwitch);
                        } else {
                            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                            holder.pollSwitch.setChecked(false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    PollsData pollsData = pollsList.get(position);

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("ID", pollsData.getID());
                        jsonObject.put("Status", "Deactivate");
                        if (ConnectionCheck.connectionStatus(context)) {
                            new DeactivatePoll(ApiUrl.ActivatePoll, jsonObject.toString(), holder.pollSwitch);
                        } else {
                            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                            holder.pollSwitch.setChecked(true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        holder.editIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new PollsFragment(pollsData.getTitle(), pollsData.getQuestion()
                                , pollsData.getOption1(), pollsData.getOption2(), pollsData.getOption3(), pollsData.getOption4(), pollsData.getID())).commit();
            }
        });

        holder.deleteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("PollID", pollsData.getID());
                    if (ConnectionCheck.connectionStatus(context)) {
                        new DeletePoll(ApiUrl.DeletePoll, jsonObject.toString(), position);
                    } else {
                        Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return pollsList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        private SwitchCompat pollSwitch;
        private TextView questionTv, titleTv;
        private ImageView editIv, deleteIv;

        public MyHolder(View itemView) {
            super(itemView);
            pollSwitch = (SwitchCompat) itemView.findViewById(R.id.pollSwitch);
            questionTv = (TextView) itemView.findViewById(R.id.questionTv);
            titleTv = (TextView) itemView.findViewById(R.id.titleTv);
            editIv = (ImageView) itemView.findViewById(R.id.editIv);
            deleteIv = (ImageView) itemView.findViewById(R.id.deleteIv);
        }
    }

    private class ActivatePoll {

        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        private SwitchCompat switchBtn;

        ActivatePoll(String url, String postBody, SwitchCompat switchBtn) {

            this.switchBtn = switchBtn;


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

                    if (response.isSuccessful()) {

                        String myReponse = response.body().string();

                        Log.d("Polls Activate Res", myReponse);

                        try {
                            JSONObject jsonObj = new JSONObject(myReponse);
                            if (jsonObj.getString("retval").equals("Status updated successfully")) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        switchBtn.setChecked(true);
                                        Toast.makeText(context, "Activated", Toast.LENGTH_SHORT).show();
                                    }
                                });


                            } else {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Not Activated", Toast.LENGTH_SHORT).show();
                                        switchBtn.setChecked(false);
                                    }
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                                switchBtn.setChecked(false);
                            }
                        });

                    }

                }
            });
        }
    }

    private class DeletePoll {

        private int position;
        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        DeletePoll(String url, String postBody, int position) {
            this.position = position;
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

                    if (response.isSuccessful()) {

                        String myReponse = response.body().string();

                        Log.d("DeletePollRes", myReponse);

                        try {
                            JSONObject jsonObj = new JSONObject(myReponse);
                            if (jsonObj.getString("retval").equals("Poll removed successfully")) {

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.container, new SubmittedPollFragment()).commit();

                                    }
                                });

                            } else {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Not Deleted", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                }
            });
        }
    }


    private class DeactivatePoll {

        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        private SwitchCompat switchBtn;

        DeactivatePoll(String url, String postBody, SwitchCompat switchBtn) {

            this.switchBtn = switchBtn;


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

                    if (response.isSuccessful()) {

                        String myReponse = response.body().string();

                        Log.d("Polls Activate Res", myReponse);

                        try {
                            JSONObject jsonObj = new JSONObject(myReponse);
                            if (jsonObj.getString("retval").equals("Status updated successfully")) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        switchBtn.setChecked(false);
                                        Toast.makeText(context, "Deactivated", Toast.LENGTH_SHORT).show();
                                    }
                                });


                            } else {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Not Deactivated", Toast.LENGTH_SHORT).show();
                                        switchBtn.setChecked(true);
                                    }
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                                switchBtn.setChecked(true);
                            }
                        });

                    }

                }
            });
        }
    }


}
