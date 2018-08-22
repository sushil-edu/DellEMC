package com.kestone.dellpartnersummit.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.widget.TextView;

import com.kestone.dellpartnersummit.R;

import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;

public class CheckIn extends AppCompatActivity {

    // QREader
    private SurfaceView mySurfaceView;
    private QREader qrEader;
    private String boothStr="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final TextView text = (TextView) findViewById(R.id.textView);

        // Setup SurfaceView
        // -----------------
        mySurfaceView = (SurfaceView) findViewById(R.id.camera_view);

        // Init QREader
        // ------------
        qrEader = new QREader.Builder(this, mySurfaceView, new QRDataListener() {
            @Override
            public void onDetected(final String data) {
                Log.d("QREader", "Value : " + data);
                text.post(new Runnable() {
                    @Override
                    public void run() {
                        text.setText(data);
                        boothStr = data;
                        finish();
                    }
                });
            }
        }).facing(QREader.BACK_CAM)
                .enableAutofocus(true)
                .height(mySurfaceView.getHeight())
                .width(mySurfaceView.getWidth())
                .build();

        qrEader.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        qrEader.initAndStart(mySurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrEader.releaseAndCleanup();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        qrEader.stop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("boothStr", boothStr);
        setResult(RESULT_OK,returnIntent);
        super.finish();
    }
}
