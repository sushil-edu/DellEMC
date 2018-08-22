package com.kestone.dellpartnersummit.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.kestone.dellpartnersummit.PoJo.UserDetails;
import com.kestone.dellpartnersummit.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackFragment extends Fragment {


    public FeedbackFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = dateFormat.format(Calendar.getInstance().getTime());

        Date convertedDate = new Date();
        Date startDate = new Date();
        Date endDate = new Date();
        try {
            convertedDate = dateFormat.parse(date);
            startDate = dateFormat.parse(UserDetails.getCheckinStartDatetime());
            endDate = dateFormat.parse(UserDetails.getCheckinEndDatetime());

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (startDate.compareTo(convertedDate) * convertedDate.compareTo(endDate) >= 0) {
            final WebView mWebView = (WebView) view.findViewById(R.id.mWebView);
            mWebView.setWebViewClient(new WebViewClient());
            mWebView.loadUrl("http://marketing.kestoneapps.com/Events/DellPartnerSummite2017/feedback.aspx?ID=" + UserDetails.getEmailID());

            final SwipeRefreshLayout swipeRefreshLayout = (android.support.v4.widget.SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mWebView.setWebViewClient(new WebViewClient());
                    mWebView.loadUrl("http://marketing.kestoneapps.com/Events/DellPartnerSummite2017/feedback.aspx?ID=" + UserDetails.getEmailID());
                    swipeRefreshLayout.setRefreshing(false);
                }
            });


        } else {
            Toast.makeText(getContext(), "Feedback will be made active on the day of event", Toast.LENGTH_SHORT).show();

        }

        return view;
    }

}
