package com.kestone.dellpartnersummit.ProgressView;

import android.app.ProgressDialog;
import android.content.Context;

public class Progress {
    private static ProgressDialog progress;

    static public void showProgress(Context context) {
        progress = new ProgressDialog(context);
        progress.setMessage("Please Wait");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(true);
        progress.show();
    }

    static public void closeProgress() {
        progress.dismiss();
    }

}
