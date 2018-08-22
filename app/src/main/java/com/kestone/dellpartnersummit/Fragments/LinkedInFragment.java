package com.kestone.dellpartnersummit.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kestone.dellpartnersummit.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LinkedInFragment extends Fragment {


    public LinkedInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_linked_in, container, false);

        final WebView mWebView = (WebView) view.findViewById(R.id.mWebView);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl(SocialFragment.linkedin);

        final SwipeRefreshLayout swipeRefreshLayout = (android.support.v4.widget.SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.setWebViewClient(new WebViewClient());
                mWebView.loadUrl(SocialFragment.linkedin);
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        return view;
    }

}
