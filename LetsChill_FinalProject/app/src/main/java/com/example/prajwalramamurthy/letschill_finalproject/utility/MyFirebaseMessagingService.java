package com.example.prajwalramamurthy.letschill_finalproject.utility;

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseMessagingService extends FirebaseMessagingService
{

    private static final String CHANNEL_ID = "id";
    private static final String CHANNEL_NAME = "name";
    private static final String CHANNEL_DESC = "desc";

    @Override
    public void onNewToken(String token) {
       // Log.d(TAG, "Refreshed token: " + token);

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

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


        NotificationManagerCompat notfiManagerCompact = NotificationManagerCompat.from(this);
        notfiManagerCompact.notify(1, mBuilder.build());

    }
}



