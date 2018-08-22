package com.kestone.dellpartnersummit.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kestone.dellpartnersummit.R;


public class SpeakerDetails extends Fragment {


    public SpeakerDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_speaker_details, container, false);

        view.findViewById(R.id.mScheduleBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), RequestSpekerMeeting.class);
//                intent.putExtra("Email", com.xperiatechnology.eventapp.Activities.SpeakerDetails.Email);
//                intent.putExtra("Tag", "Schedule");
//                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        return view;
    }

}
