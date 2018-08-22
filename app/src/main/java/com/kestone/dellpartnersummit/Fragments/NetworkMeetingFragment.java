package com.kestone.dellpartnersummit.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kestone.dellpartnersummit.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NetworkMeetingFragment extends Fragment implements View.OnClickListener{
    private TextView mScheduledTv,mApproveTv,mPendingTv;


    public NetworkMeetingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_network_meeting, container, false);

        mScheduledTv = (TextView) view.findViewById(R.id.mScheduledTv);
        mScheduledTv.setOnClickListener(this);
        mApproveTv = (TextView) view.findViewById(R.id.mApproveTv);
        mApproveTv.setOnClickListener(this);
        mPendingTv = (TextView) view.findViewById(R.id.mPendingTv);
        mPendingTv.setOnClickListener(this);

        mScheduledTv.setTextColor(getResources().getColor(R.color.colorPrimary));
        getChildFragmentManager().beginTransaction()
                .replace(R.id.container, new MyScheduled())
                .commit();



        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.mScheduledTv:
                mScheduledTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                mApproveTv.setTextColor(getResources().getColor(R.color.grey));
                mPendingTv.setTextColor(getResources().getColor(R.color.grey));
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.container, new MyScheduled())
                        .commit();

                break;

            case R.id.mApproveTv:
                mApproveTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                mPendingTv.setTextColor(getResources().getColor(R.color.grey));
                mScheduledTv.setTextColor(getResources().getColor(R.color.grey));
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.container, new MyApprove())
                        .commit();

                break;

            case R.id.mPendingTv:
                mPendingTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                mScheduledTv.setTextColor(getResources().getColor(R.color.grey));
                mApproveTv.setTextColor(getResources().getColor(R.color.grey));
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.container, new MyPending())
                        .commit();

                break;


        }

    }
}
