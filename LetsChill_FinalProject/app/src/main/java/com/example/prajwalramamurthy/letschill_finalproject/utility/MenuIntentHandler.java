package com.example.prajwalramamurthy.letschill_finalproject.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.activities.MyEventsActivity;
import com.example.prajwalramamurthy.letschill_finalproject.activities.ProfileActivity;
import com.example.prajwalramamurthy.letschill_finalproject.activities.SignInUpActivity;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.SignInFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.SignUpFragment;
import com.google.firebase.auth.FirebaseAuth;

public class MenuIntentHandler {

    // Variables
    private static SharedPreferences mPrefs;

    public static void getMenuIntents(MenuItem item, Context context, Activity activity) {

        switch (item.getItemId()) {

            case R.id.submenu_profile:

                // Intent to "ProfileActivity"
                Intent mProfileIntent = new Intent(context, ProfileActivity.class);
                context.startActivity(mProfileIntent);
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

                // Instantiate the SharedPreferences
                mPrefs = PreferenceManager.getDefaultSharedPreferences(context);

                // Remove the "PREFS_REMEMBER_ME" from SharedPreferences
                mPrefs.edit().remove(SignInFragment.PREFS_REMEMBER_ME).apply();

                // Remove the user UID from SharedPreferences
                mPrefs.edit().remove(SignUpFragment.PREFS_USER_UID).apply();

                // Sign out of the Firebase Auth
                FirebaseAuth.getInstance().signOut();

                // Sign out of the Facebook Auth
                FirebaseAuth.getInstance().signOut();

                // Close the current activity
                activity.finish();

                // Open the SignInUpActivity
                Intent mSignInUpIntent = new Intent(context, SignInUpActivity.class);
                activity.startActivity(mSignInUpIntent);
                break;
        }
    }
}
