package com.example.prajwalramamurthy.letschill_finalproject.utility;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService
{

    private static final String CHANNEL_ID = "id.test";
    private static final String CHANNEL_NAME = "name";
    private static final String CHANNEL_DESC = "desc";
    private String TAG;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e(TAG, "onMessageReceived: ");

        displayNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
    }

    private void displayNotification(String title, String body) {

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
//            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }




    }

    @Override
    public void onNewToken(String token) {



        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        displayNotification();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
       // sendRegistrationToServer(token);

    }


    private void displayNotification()
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_white_24dp)
                .setContentTitle("It works")
                .setContentText("First Notification")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        mBuilder.setAutoCancel(true);

        NotificationManagerCompat notfiManagerCompact = NotificationManagerCompat.from(this);
        notfiManagerCompact.notify(1, mBuilder.build());

    }
}



