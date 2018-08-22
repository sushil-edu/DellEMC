package com.kestone.dellpartnersummit.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kestone.dellpartnersummit.Fragments.FeedbackFragment;
import com.kestone.dellpartnersummit.Fragments.GalleryFragment;
import com.kestone.dellpartnersummit.Fragments.SocialFragment;
import com.kestone.dellpartnersummit.widget.MyBoldTextView;
import com.kestone.dellpartnersummit.widget.MyTextView;

import com.kestone.dellpartnersummit.Fragments.AskAQuestionFragment;
import com.kestone.dellpartnersummit.Fragments.KnowledgeBankFragment;
import com.squareup.picasso.Picasso;
import com.kestone.dellpartnersummit.Fragments.ActivityStreamFragment;
import com.kestone.dellpartnersummit.Fragments.AgendaFragment;
import com.kestone.dellpartnersummit.Fragments.DelegatesFragment;
import com.kestone.dellpartnersummit.Fragments.HelpDeskFragment;
import com.kestone.dellpartnersummit.Fragments.NetworkingFragment;
import com.kestone.dellpartnersummit.Fragments.PollQuestionsFragment;
import com.kestone.dellpartnersummit.Fragments.SpeakersFragment;
import com.kestone.dellpartnersummit.Fragments.VenueFragment;
import com.kestone.dellpartnersummit.PoJo.UserDetails;
import com.kestone.dellpartnersummit.R;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    NavigationView navigationView;
    android.support.v7.app.ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    public static MyBoldTextView mTitleTv;
    boolean doubleBackToExitPressedOnce = false;
    private LinearLayout profileLin;

    String arrNavItem[] = {"Activity Stream", "Agenda", "Speakers", "Delegates", "Networking",
            "Polls", "Ask a Question", "Social", "Gallery",
            "Venue", "Feedback", "Help Desk", "Log Out"};

    int arrNavIcon[] = {R.drawable.ic_activity_stream,
            R.drawable.ic_agenda,
            R.drawable.ic_speaker,
            R.drawable.delegates,
            R.drawable.ic_menu_slideshow,
            R.drawable.ic_polls,
            R.drawable.ask_question,
           // R.drawable.knowledge_bank,
            R.drawable.ic_menu_share,
            R.drawable.ic_gallery_logo,
            R.drawable.ic_location,
            R.drawable.ic_feedback,
            R.drawable.ic_help,
            R.drawable.log_out};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayFirebaseRegId();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitleTv = (MyBoldTextView) findViewById(R.id.mTitleTv);
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_dashboard);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        navigationView = (NavigationView) findViewById(R.id.navigation_View);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyAdapter());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new ActivityStreamFragment())
                .commit();

        ImageView mEditIv = (ImageView) findViewById(R.id.mEditIv);

        profileLin = (LinearLayout) findViewById(R.id.profileLin);
        profileLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditProfile.class);
                startActivity(intent);
            }
        });


        MyTextView companyTv = (MyTextView) findViewById(R.id.companyTv);
        companyTv.setText(UserDetails.getOrganization());

        MyTextView nameTv = (MyTextView) findViewById(R.id.nameTv);
        nameTv.setText(UserDetails.getName());

        ImageView profileIv = (ImageView) findViewById(R.id.profileIv);

        if (UserDetails.ImageURL.length() > 0) {
            Picasso.with(this)
                    .load(UserDetails.ImageURL)
                    .into(profileIv);

        }

        ImageView notificationIv = (ImageView) findViewById(R.id.notificationIv);
        notificationIv.setVisibility(View.GONE);
        notificationIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                startActivity(intent);

            }
        });
    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("RegId", 0);
        String regId = pref.getString("regId", null);

        Log.e("resId", "Firebase reg id: " + regId);

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;

        Snackbar.make(drawerLayout, "Click Again to Exit", Snackbar.LENGTH_LONG).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {


        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_drawer_item, parent, false);

            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {

            holder.drawer_icon.setImageDrawable(getResources().getDrawable(arrNavIcon[position]));
            holder.drawer_itemName.setText(arrNavItem[position]);

        }

        @Override
        public int getItemCount() {
            return 13;
        }

        class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


            private MyTextView drawer_itemName;
            private ImageView drawer_icon;

            public MyHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);

                drawer_icon = (ImageView) itemView.findViewById(R.id.drawer_icon);
                drawer_itemName = (MyTextView) itemView.findViewById(R.id.drawer_itemName);

            }

            @Override
            public void onClick(View v) {
                if (getAdapterPosition() == 0) {

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new ActivityStreamFragment())
                            .commit();
                    mTitleTv.setText("Activity Stream");

                } else if (getAdapterPosition() == 1) {

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new AgendaFragment())
                            .commit();
                    mTitleTv.setText("Agenda");

                } else if (getAdapterPosition() == 2) {

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new SpeakersFragment())
                            .commit();

                    mTitleTv.setText("Speakers");

                } else if (getAdapterPosition() == 3) {

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new DelegatesFragment())
                            .commit();

                    mTitleTv.setText("Delegates");

                } else if (getAdapterPosition() == 4) {

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new NetworkingFragment())
                            .commit();

                    mTitleTv.setText("Networking");

                } else if (getAdapterPosition() == 5) {

                    getSupportFragmentManager().beginTransaction()
                            //.replace(R.id.container, new SubmittedPollFragment())
                            .replace(R.id.container, new PollQuestionsFragment())
                            .commit();

                    mTitleTv.setText("Polls");

                } else if (getAdapterPosition() == 6) {

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new AskAQuestionFragment())
                            .commit();

                    mTitleTv.setText("Ask a Question");

                }
//                else if (getAdapterPosition() == 7) {
//
//
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.container, new KnowledgeBankFragment())
//                            .commit();
//
//                    mTitleTv.setText("Knowledge Bank");
//
//                }
                else if (getAdapterPosition() == 7) {

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new SocialFragment())
                            .commit();

                    mTitleTv.setText("Social");

                } else if (getAdapterPosition() == 9) {

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new VenueFragment())
                            .commit();

                    mTitleTv.setText("Venue");

                } else if (getAdapterPosition() == 10) {

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new FeedbackFragment())
                            .commit();

                    mTitleTv.setText("Feedback");

                } else if (getAdapterPosition() == 11) {

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new HelpDeskFragment())
                            .commit();

                    mTitleTv.setText("Help Desk");

                } else if (getAdapterPosition() == 12) {
                    SharedPreferences sharedPrefrence = getSharedPreferences("User", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPrefrence.edit();
                    editor.putString("UserDetails", "");
                    editor.apply();

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }else if (getAdapterPosition() == 8) {

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new GalleryFragment())
                            .commit();

                    mTitleTv.setText("Gallery");

                }

                drawerLayout.closeDrawer(GravityCompat.START);
            }
        }
    }


}
