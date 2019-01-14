package com.example.prajwalramamurthy.letschill_finalproject.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class NotificationsActivity extends AppCompatActivity
{



    private static final String CHANNEL_ID = "id";
    private static final String CHANNEL_NAME = "name";
    private static final String CHANNEL_DESC = "desc";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details_event);
        setTitle("Notifications");



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {

                if(task.isSuccessful())
                {
                    String token = task.getResult().getToken();
                    Log.i("", "TOKEN: " + token);
                }
                else
                {

                }

            }
        });

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

