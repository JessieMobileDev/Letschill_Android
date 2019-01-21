package com.example.prajwalramamurthy.letschill_finalproject.utility;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.activities.MyEventsActivity;
import com.example.prajwalramamurthy.letschill_finalproject.activities.NotificationsActivity;
import com.example.prajwalramamurthy.letschill_finalproject.activities.ProfileActivity;
import com.example.prajwalramamurthy.letschill_finalproject.activities.SignInUpActivity;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.SignInFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.SignUpFragment;
import com.google.firebase.auth.FirebaseAuth;

public class MenuIntentHandler {

    // Variables
    private static SharedPreferences mPrefs;

    // Constants
    public static String CREATE_EVENT_ACTIVITY = "CREATE_EVENT_ACTIVITY";
    public static String MAIN_ACTIVITY = "MAIN_ACTIVITY";
    public static String MY_EVENTS_ACTIVITY = "MY_EVENTS_ACTIVITY";
    public static String DETAILS_EVENT_ACTIVITY = "DETAILS_EVENT_ACTIVITY";
    public static String EDIT_PROFILE_ACTIVITY = "EDIT_PROFILE_ACTIVITY";
    public static String PROFILE_ACTIVITY = "PROFILE_ACTIVITY";

    public static void getMenuIntents(MenuItem item, final Context context, final Activity activity, String activityName) {

        switch (item.getItemId()) {

            case R.id.submenu_profile:

                // Intent to "ProfileActivity"
                Intent mProfileIntent = new Intent(context, ProfileActivity.class);
                context.startActivity(mProfileIntent);

                if (!activityName.equals(MAIN_ACTIVITY)) {

                    // Close current activity
                    activity.finish();
                }

                break;

            case R.id.submenu_my_events:

                // Intent to "My Events" activity
                Intent mMyEventsIntent = new Intent(context, MyEventsActivity.class);
                context.startActivity(mMyEventsIntent);
                Log.d("test", "getMenuIntents: menu was clicked - events");

                if (!activityName.equals(MAIN_ACTIVITY)) {

                    // Close current activity
                    activity.finish();
                }

                break;
            case R.id.submenu_notification:

                // Intent to "ProfileActivity"
                Intent mNotIntent = new Intent(context, NotificationsActivity.class);
                context.startActivity(mNotIntent);

                if (!activityName.equals(MAIN_ACTIVITY)) {

                    // Close current activity
                    activity.finish();
                }

                break;
            case R.id.submenu_logout:

                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Log out");
                alert.setMessage("Would you like to log out?");
                alert.setNegativeButton("No", null);
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

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
                    }
                });
                alert.show();

                break;
        }
    }
}
