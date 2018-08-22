package com.kestone.dellpartnersummit.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kestone.dellpartnersummit.Fragments.DelegatesMeetingFragment;
import com.kestone.dellpartnersummit.R;

public class DelegatesDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delegates_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView organizationTv = (TextView) findViewById(R.id.organizationTv);
        TextView designationTv = (TextView) findViewById(R.id.designationTv);
        TextView nameTv = (TextView) findViewById(R.id.nameTv);
        organizationTv.setText(getIntent().getStringExtra("Organization"));
        designationTv.setText(getIntent().getStringExtra("Designation"));
        nameTv.setText(getIntent().getStringExtra("Name"));

        getSupportFragmentManager().beginTransaction().
                replace(R.id.container,new DelegatesMeetingFragment())
                .commit();

        Button mRescheduleBtn = (Button) findViewById(R.id.mRescheduleBtn);
        mRescheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DelegatesDetails.this,RequestSpekerMeeting.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
