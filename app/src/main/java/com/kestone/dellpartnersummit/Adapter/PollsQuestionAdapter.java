package com.kestone.dellpartnersummit.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kestone.dellpartnersummit.Activities.MainActivity;
import com.kestone.dellpartnersummit.ApiHandler.ApiUrl;
import com.kestone.dellpartnersummit.ConnectionCheck.ConnectionCheck;
import com.kestone.dellpartnersummit.Fragments.ActivityStreamFragment;
import com.kestone.dellpartnersummit.Fragments.PollQuestionsFragment;
import com.kestone.dellpartnersummit.PoJo.PollsData;
import com.kestone.dellpartnersummit.PoJo.UserDetails;
import com.kestone.dellpartnersummit.ProgressView.Progress;
import com.kestone.dellpartnersummit.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PollsQuestionAdapter extends RecyclerView.Adapter<PollsQuestionAdapter.MyHolder> {

    private Context context;
    private Map<Integer, Integer> linkedHashMap;
    private ArrayList<PollsData> pollsList;
    private Button mSubmitBtn;
    private MainActivity activity;

    public PollsQuestionAdapter(Context context, ArrayList<PollsData> pollsList, Button mSubmitBtn) {
        this.context = context;
        linkedHashMap = new LinkedHashMap<>();
        this.pollsList = pollsList;
        this.mSubmitBtn = mSubmitBtn;
        activity = (MainActivity) context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.polls_question_cell, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {

        if (linkedHashMap.containsKey(position)) {
            if (linkedHashMap.get(position) == 1) {
                holder.mRadioFirst.setChecked(true);

            } else if (linkedHashMap.get(position) == 2) {
                holder.mRadioTwo.setChecked(true);

            } else if (linkedHashMap.get(position) == 3) {
                holder.mRadioThree.setChecked(true);

            } else if (linkedHashMap.get(position) == 4) {
                holder.mRadioFour.setChecked(true);
            }
        } else {
            holder.mRadioFirst.setChecked(false);
            holder.mRadioTwo.setChecked(false);
            holder.mRadioThree.setChecked(false);
            holder.mRadioFour.setChecked(false);
        }

        final PollsData pollsData = pollsList.get(position);
        holder.titleTv.setText("Select One Option");
        holder.questionTv.setVisibility(View.GONE);
        holder.mRadioFirst.setText(pollsData.getOption1());
        holder.mRadioTwo.setText(pollsData.getOption2());
        holder.mRadioThree.setText(pollsData.getOption3());
        holder.mRadioFour.setText(pollsData.getOption4());

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linkedHashMap.size() > 0) {

                    if (UserDetails.getUserType().equals("ITDM")) {

                        if(PollQuestionsFragment.TRACK_NAME.equals("Select Track")||PollQuestionsFragment.TRACK_NAME.length()==0){

                            Toast.makeText(context, "Select Track Also", Toast.LENGTH_SHORT).show();

                        }else {
                            Log.d("Polls Answer", linkedHashMap.toString());
                            Progress.showProgress(context);

                            try {
                                JSONArray jsonArray = new JSONArray();

                                for (Map.Entry<Integer, Integer> entry : linkedHashMap.entrySet()) {

                                    Integer key = entry.getKey();

                                    PollsData poll = pollsList.get(key);
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("EmailID", UserDetails.EmailID);
                                    jsonObject.put("PollID", poll.getID());
                                    jsonObject.put("Response", linkedHashMap.get(key).toString());
                                    jsonObject.put("Track",PollQuestionsFragment.TRACK_NAME);
                                    jsonArray.put(jsonObject);

                                }

                                Log.d("Polls Param", jsonArray.toString());

                                if (ConnectionCheck.connectionStatus(context)) {
                                    new SubmitPoll(ApiUrl.SubmitPoll, jsonArray.toString());
                                } else {
                                    Progress.closeProgress();
                                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Progress.closeProgress();
                            }
                        }

                    } else {

                        Log.d("Polls Answer", linkedHashMap.toString());
                        Progress.showProgress(context);

                        try {
                            JSONArray jsonArray = new JSONArray();

                            for (Map.Entry<Integer, Integer> entry : linkedHashMap.entrySet()) {

                                Integer key = entry.getKey();

                                PollsData poll = pollsList.get(key);
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("EmailID", UserDetails.EmailID);
                                jsonObject.put("PollID", poll.getID());
                                jsonObject.put("Response", linkedHashMap.get(key).toString());
                                jsonObject.put("Track","");
                                jsonArray.put(jsonObject);


                            }

                            Log.d("Polls Param", jsonArray.toString());

                            if (ConnectionCheck.connectionStatus(context)) {
                                new SubmitPoll(ApiUrl.SubmitPoll, jsonArray.toString());
                            } else {
                                Progress.closeProgress();
                                Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Progress.closeProgress();
                        }
                    }

                } else
                    Toast.makeText(context, "Nothing to submit", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public int getItemCount() {
        return pollsList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        RadioButton mRadioFirst, mRadioTwo, mRadioThree, mRadioFour;
        TextView questionTv, titleTv, optionATv, optionBTv, optionCTv, optionDTv;

        public MyHolder(View itemView) {
            super(itemView);
            questionTv = (TextView) itemView.findViewById(R.id.questionTv);
            titleTv = (TextView) itemView.findViewById(R.id.titleTv);
            mRadioFirst = (RadioButton) itemView.findViewById(R.id.mRadioFirst);
            mRadioFirst.setOnClickListener(this);
            mRadioTwo = (RadioButton) itemView.findViewById(R.id.mRadioTwo);
            mRadioTwo.setOnClickListener(this);
            mRadioThree = (RadioButton) itemView.findViewById(R.id.mRadioThree);
            mRadioThree.setOnClickListener(this);
            mRadioFour = (RadioButton) itemView.findViewById(R.id.mRadioFour);
            mRadioFour.setOnClickListener(this);

            optionATv = (TextView) itemView.findViewById(R.id.optionATv);
            optionATv.setOnClickListener(this);

            optionBTv = (TextView) itemView.findViewById(R.id.optionBTv);
            optionBTv.setOnClickListener(this);

            optionCTv = (TextView) itemView.findViewById(R.id.optionCTv);
            optionCTv.setOnClickListener(this);

            optionDTv = (TextView) itemView.findViewById(R.id.optionDTv);
            optionDTv.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.optionATv:
                    linkedHashMap.put(getAdapterPosition(), 1);
                    optionATv.setBackgroundResource(R.color.green);
                    optionBTv.setBackgroundResource(R.color.colorPrimary);
                    optionCTv.setBackgroundResource(R.color.colorPrimary);
                    optionDTv.setBackgroundResource(R.color.colorPrimary);
//                    mRadioFirst.setChecked(true);
//                    mRadioTwo.setChecked(false);
//                    mRadioThree.setChecked(false);
//                    mRadioFour.setChecked(false);
                    break;

                case R.id.optionBTv:
                    linkedHashMap.put(getAdapterPosition(), 2);
                    optionATv.setBackgroundResource(R.color.colorPrimary);
                    optionBTv.setBackgroundResource(R.color.green);
                    optionCTv.setBackgroundResource(R.color.colorPrimary);
                    optionDTv.setBackgroundResource(R.color.colorPrimary);
//                    mRadioFirst.setChecked(false);
//                    mRadioTwo.setChecked(true);
//                    mRadioThree.setChecked(false);
//                    mRadioFour.setChecked(false);
                    break;

                case R.id.optionCTv:
                    linkedHashMap.put(getAdapterPosition(), 3);
                    optionATv.setBackgroundResource(R.color.colorPrimary);
                    optionBTv.setBackgroundResource(R.color.colorPrimary);
                    optionCTv.setBackgroundResource(R.color.green);
                    optionDTv.setBackgroundResource(R.color.colorPrimary);
//                    mRadioFirst.setChecked(false);
//                    mRadioTwo.setChecked(false);
//                    mRadioThree.setChecked(true);
//                    mRadioFour.setChecked(false);
                    break;

                case R.id.optionDTv:
                    linkedHashMap.put(getAdapterPosition(), 4);
                    optionATv.setBackgroundResource(R.color.colorPrimary);
                    optionBTv.setBackgroundResource(R.color.colorPrimary);
                    optionCTv.setBackgroundResource(R.color.colorPrimary);
                    optionDTv.setBackgroundResource(R.color.green);
//                    mRadioFirst.setChecked(false);
//                    mRadioTwo.setChecked(false);
//                    mRadioThree.setChecked(false);
//                    mRadioFour.setChecked(true);
                    break;

            }
        }
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

                        Log.d("Polls Answer Res", myReponse);

                        try {
                            JSONObject jsonObj = new JSONObject(myReponse);
                            if (jsonObj.getString("retval").equals("Response submitted successfully")) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Response Submitted Sucessfully", Toast.LENGTH_SHORT).show();
                                        activity.getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.container, new ActivityStreamFragment()).commit();
                                        MainActivity.mTitleTv.setText("Activity Stream");

                                    }
                                });


                            } else {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Not Successfull", Toast.LENGTH_SHORT).show();
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
}
