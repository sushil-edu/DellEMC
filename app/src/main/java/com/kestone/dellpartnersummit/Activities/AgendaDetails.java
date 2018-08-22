package com.kestone.dellpartnersummit.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kestone.dellpartnersummit.R;

import java.text.DateFormatSymbols;

import in.galaxyofandroid.widgets.AwesomeRelativeLayout;

public class AgendaDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        TextView descriptionTv = (TextView) findViewById(R.id.descriptionTv);
        TextView locationTv = (TextView) findViewById(R.id.locationTv);



//        intent.putExtra("description", agendaData.getDescription());
//        intent.putExtra("location", agendaData.getLocation());

        TextView titleTv = (TextView) findViewById(R.id.titleTv);
        titleTv.setText(getIntent().getStringExtra("title"));

        ImageView profileIv = (ImageView) findViewById(R.id.profileIv);

        AwesomeRelativeLayout imageRel = (AwesomeRelativeLayout) findViewById(R.id.profileRel);
        TextView nameTv = (TextView) findViewById(R.id.nameTv);

        TextView designationTv = (TextView) findViewById(R.id.designationTv);
        TextView organizationTv = (TextView) findViewById(R.id.organizationTv);

        TextView dateTv = (TextView) findViewById(R.id.dateTv);

        if(getIntent().getStringExtra("date").length()>0){
            dateTv.setVisibility(View.VISIBLE);

            String month = new DateFormatSymbols().getMonths()[Integer.parseInt(getIntent().getStringExtra("date").substring(5, 7))-1];

            dateTv.setText(month.substring(0, 3)+" "+getIntent().getStringExtra("date").substring(8, 10)
                    + "\n"+ getIntent().getStringExtra("time"));

        }else dateTv.setVisibility(View.GONE);


        if(getIntent().getStringExtra("description")==null||getIntent().getStringExtra("description").length()==0){
            descriptionTv.setText("");
        }else {
            descriptionTv.setText(getIntent().getStringExtra("description"));
        }

        if(getIntent().getStringExtra("location")==null||getIntent().getStringExtra("location").length()==0){
            locationTv.setText("");
        }else {
            locationTv.setText(getIntent().getStringExtra("location"));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
