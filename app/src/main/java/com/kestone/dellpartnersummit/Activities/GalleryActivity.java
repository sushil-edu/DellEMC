package com.kestone.dellpartnersummit.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.kestone.dellpartnersummit.R;
import com.squareup.picasso.Picasso;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        ImageView largeIv = (ImageView) findViewById(R.id.largeIv);

        Picasso.with(this).load(getIntent().getStringExtra("link")).into(largeIv);

        findViewById(R.id.closeIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
