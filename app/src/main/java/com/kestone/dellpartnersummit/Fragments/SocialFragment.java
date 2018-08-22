package com.kestone.dellpartnersummit.Fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kestone.dellpartnersummit.ApiHandler.ApiUrl;
import com.kestone.dellpartnersummit.ConnectionCheck.ConnectionCheck;
import com.kestone.dellpartnersummit.ProgressView.Progress;
import com.kestone.dellpartnersummit.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SocialFragment extends Fragment {

    private ViewPager viewPager;
    public static String twitter = "", facebook = "", linkedin = "", instagram = "";
    private Activity activity;


    public SocialFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_social, container, false);

        activity = (Activity) getContext();

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        Progress.showProgress(getContext());
        if(ConnectionCheck.connectionStatus(getContext())){
            new SocialDataFetch(ApiUrl.Social);
        }else {
            Progress.closeProgress();
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        return view;

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void setupViewPager(ViewPager viewPager, String Twitter, String LinkedIn, String Facebook, String Instagram) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        if (twitter.length() > 0) {
            adapter.addFragment(new TwitterFragment(), "Twitter");
        }

        if (linkedin.length() > 0) {
            adapter.addFragment(new LinkedInFragment(), "LinkedIn");
        }
        if (facebook.length() > 0) {
            adapter.addFragment(new FacebookFragment(), "Facebook");
        }
        if (instagram.length() > 0) {
            adapter.addFragment(new InstagramFragment(), "Instagram");
        }


        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
    }


    public class SocialDataFetch {
        private String url;

        public SocialDataFetch(String url) {
            this.url = url;
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
                public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {

                    Progress.closeProgress();

                    if (response.isSuccessful()) {
                        String myResponse = response.body().string();
                        Log.d("Venue Reponse", myResponse);
                        JSONArray jsonArray = null;

                        try {
                            jsonArray = new JSONArray(myResponse);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if (jsonObject.getString("SocialPlatform").equals("Twitter")) {
                                    twitter = jsonObject.getString("Link");
                                }
                                if (jsonObject.getString("SocialPlatform").equals("Facebook")) {
                                    facebook = jsonObject.getString("Link");

                                }
                                if (jsonObject.getString("SocialPlatform").equals("Linkedin")) {
                                    linkedin = jsonObject.getString("Link");

                                }
                                if (jsonObject.getString("SocialPlatform").equals("Instagram")) {
                                    instagram = jsonObject.getString("Link");
                                }
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setupViewPager(viewPager, twitter, linkedin, facebook, instagram);
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
                                Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                }
            });
        }
    }
}

