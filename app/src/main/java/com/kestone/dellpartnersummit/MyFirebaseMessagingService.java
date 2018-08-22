package com.kestone.dellpartnersummit;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kestone.dellpartnersummit.Activities.MainActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(this);
        notificationCompat.setContentTitle("FCM Notification");
        notificationCompat.setContentText(remoteMessage.getNotification().getBody());
        notificationCompat.setAutoCancel(true);

        long[] pattern = {500,500,500,500,500,500,500,500,500};
        notificationCompat.setVibrate(pattern);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationCompat.setSound(alarmSound);

        notificationCompat.setSmallIcon(R.drawable.notification_icon);
        notificationCompat.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notificationCompat.build());
    }
}
