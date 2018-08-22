package com.kestone.dellpartnersummit.Adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kestone.dellpartnersummit.PoJo.HelpData;
import com.kestone.dellpartnersummit.R;
import com.master.permissionhelper.PermissionHelper;

import java.util.ArrayList;


public class HelpAdapter extends RecyclerView.Adapter<HelpAdapter.MyHolder> {

    private Context context;
    private ArrayList<HelpData> helpList;
    private Activity activity;

    public HelpAdapter(Context context, ArrayList<HelpData> helpList) {
        this.context = context;
        this.helpList = helpList;
        activity = (Activity) this.context;

    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.help_cell, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {

        final HelpData helpData = helpList.get(position);
        holder.nameTv.setText(helpData.getName());
        holder.titleTv.setText(helpData.getTitle());
        holder.contactTv.setText(helpData.getPhone());
        holder.emailTv.setText(helpData.getEmailID());
        holder.designationTv.setText(helpData.getDesignation());

        holder.phoneIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PermissionHelper permissionHelper = new PermissionHelper(activity, new String[]{Manifest.permission.CALL_PHONE}, 100);
                permissionHelper.request(new PermissionHelper.PermissionCallback() {
                    @Override
                    public void onPermissionGranted() {

                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" +helpData.getPhone() ));//change the number
                        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        context.startActivity(callIntent);


                    }

                    @Override
                    public void onPermissionDenied() {
                        Log.d("Permission", "onPermissionDenied() called");
                    }

                    @Override
                    public void onPermissionDeniedBySystem() {
                        Log.d("Permission", "onPermissionDeniedBySystem() called");
                    }
                });


            }
        });


        holder.contactTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PermissionHelper permissionHelper = new PermissionHelper(activity, new String[]{Manifest.permission.CALL_PHONE}, 100);
                permissionHelper.request(new PermissionHelper.PermissionCallback() {
                    @Override
                    public void onPermissionGranted() {

                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" +helpData.getPhone() ));//change the number
                        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        context.startActivity(callIntent);


                    }

                    @Override
                    public void onPermissionDenied() {
                        Log.d("Permission", "onPermissionDenied() called");
                    }

                    @Override
                    public void onPermissionDeniedBySystem() {
                        Log.d("Permission", "onPermissionDeniedBySystem() called");
                    }
                });

            }
        });

        holder.emailTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail(holder.emailTv.getText().toString());
            }
        });

        holder.emailLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail(holder.emailTv.getText().toString());
            }
        });

    }

    protected void sendEmail(String email) {
        Log.i("Send email", "");
        String[] TO = {email};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");

        try {
            context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            Log.i("Finished sending", "email");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return helpList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        TextView contactTv,emailTv,titleTv,nameTv,designationTv;
        ImageView phoneIv, emailLogo;

        public MyHolder(View itemView) {
            super(itemView);

            emailTv = (TextView) itemView.findViewById(R.id.emailTv);
            titleTv = (TextView) itemView.findViewById(R.id.titleTv);
            nameTv = (TextView) itemView.findViewById(R.id.nameTv);
            contactTv = (TextView) itemView.findViewById(R.id.contactTv);
            phoneIv = (ImageView) itemView.findViewById(R.id.phoneIv);
            emailLogo = (ImageView) itemView.findViewById(R.id.emailLogo);
            designationTv = (TextView) itemView.findViewById(R.id.designationTv);

            contactTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PackageManager pm = context.getPackageManager();

                    try {
                        Intent sendIntent = new Intent("android.intent.action.MAIN");
                        //sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.setType("text/plain");
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                        sendIntent.putExtra("jid", contactTv.getText().toString() + "@s.whatsapp.net"); //phone number without "+" prefix
                        sendIntent.setPackage("com.whatsapp");
                        context.startActivity(sendIntent);
                    } catch (Exception e) {
                        Toast.makeText(context, "Error/n" + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
