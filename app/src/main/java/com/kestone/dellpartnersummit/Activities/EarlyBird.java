package com.kestone.dellpartnersummit.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kestone.dellpartnersummit.PoJo.UserDetails;
import com.kestone.dellpartnersummit.R;

public class EarlyBird extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_early_bird);

        TextView checkinRl = (TextView) findViewById(R.id.checkinRl);
        checkinRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EarlyBird.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        TextView venueTv = (TextView) findViewById(R.id.venueTv);
        venueTv.setText(UserDetails.getPriorityMsg());
    }
}
