package com.kestone.dellpartnersummit.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kestone.dellpartnersummit.Activities.SpeakerDetails;
import com.kestone.dellpartnersummit.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduledMeetingFragment extends Fragment {


    public ScheduledMeetingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scheduled_meeting, container, false);

        TextView mScheduledTv = (TextView) view.findViewById(R.id.mScheduledTv);

        String time = SpeakerDetails.dayStr +" "+SpeakerDetails.monthStr+"\n"+SpeakerDetails.hourStr+"Hrs"+" "+SpeakerDetails.minuteStr+"Min";

        mScheduledTv.setText(time);

        return view;
    }

}
