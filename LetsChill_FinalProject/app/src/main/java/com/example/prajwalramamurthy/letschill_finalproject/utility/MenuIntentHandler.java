package com.example.prajwalramamurthy.letschill_finalproject.utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.activities.MyEventsActivity;

public class MenuIntentHandler {

    public static void getMenuIntents(MenuItem item, Context context) {

        switch (item.getItemId()) {

            case R.id.submenu_profile:
                break;
            case R.id.submenu_my_events:

                // Intent to "My Events" activity
                Intent mMyEventsIntent = new Intent(context, MyEventsActivity.class);
                context.startActivity(mMyEventsIntent);
                Log.d("test", "getMenuIntents: menu was clicked - events");

                break;
            case R.id.submenu_notification:
                break;
            case R.id.submenu_logout:
                break;
        }
    }
}
