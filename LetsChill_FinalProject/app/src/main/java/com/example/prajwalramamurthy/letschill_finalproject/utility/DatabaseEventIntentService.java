package com.example.prajwalramamurthy.letschill_finalproject.utility;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.example.prajwalramamurthy.letschill_finalproject.activities.MainActivity;
import com.example.prajwalramamurthy.letschill_finalproject.activities.SignInUpActivity;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.User;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.SignUpFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class DatabaseEventIntentService extends IntentService {

    // Variables
    private DatabaseReference mDBReference;
    private ArrayList<Event> mTodayEvents = new ArrayList<>();
    private ArrayList<Event> mUpcomingEvents = new ArrayList<>();
    private ArrayList<Event> mPastEvents = new ArrayList<>();

    // Constants
    public static final String EXTRA_RESULT_RECEIVER = "com.example.prajwalramamurthy.letschill_finalproject.utility.EXTRA_RESULT_RECEIVER";
    private static final String TAG = "test";
    public static final String BUNDLE_EXTRA_TODAY_EVENTS = "BUNDLE_EXTRA_TODAY_EVENTS";
    public static final String BUNDLE_EXTRA_UPCOMING_EVENTS = "BUNDLE_EXTRA_UPCOMING_EVENTS";
    public static final String BUNDLE_EXTRA_PAST_EVENTS = "BUNDLE_EXTRA_PAST_EVENTS";
    public static final String BUNDLE_EXTRA_OK_USERNAME = "BUNDLE_EXTRA_OK_USERNAME";
    public static final String PREFS_USER_NAME = "PREFS_USER_NAME";

    public DatabaseEventIntentService() {
        super("DatabaseEventIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        // If the intent passed does not contain any of the extras variables, throw an exception
        if (!Objects.requireNonNull(intent).hasExtra(EXTRA_RESULT_RECEIVER)) {
            throw new IllegalArgumentException("EXTRA_RESULT_RECEIVER is missing!");
        }

        final ResultReceiver mReceiver = intent.getParcelableExtra(EXTRA_RESULT_RECEIVER);

        Integer mRequestID = intent.getIntExtra(MainActivity.EXTRA_DB_REQUEST_ID, 1);

        // Instantiate arrayLists
//        mTodayEvents = new ArrayList<>();
//        mUpcomingEvents = new ArrayList<>();
//        mPastEvents = new ArrayList<>();

        if (mRequestID == 0) {

            // Get the reference of the database under "Events"
            mDBReference = FirebaseDatabase.getInstance().getReference("Events");

            // Clear the lists before adding
            mTodayEvents.clear();
            mUpcomingEvents.clear();
            mPastEvents.clear();

            Log.d(TAG, "onReceiveResult: Today list: " + mTodayEvents.size() + " - Upcoming list: " + mUpcomingEvents.size() +
                    " - Past list: " + mPastEvents.size());

            mDBReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot event: dataSnapshot.getChildren()) {


                        Event mEvent = event.getValue(Event.class);

                        // Clear the lists before adding
//                        mTodayEvents.clear();
//                        mUpcomingEvents.clear();
//                        mPastEvents.clear();

                        if (mEvent != null) {

                            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("M-dd-yyyy");
                            String mTodayDateString = mSimpleDateFormat.format(Calendar.getInstance().getTime());

                            // Check all the events happening today
                            if (mEvent.getmEventDate().equals(mTodayDateString)) {

                                //mTodayEvents.clear();


                                mTodayEvents.add(mEvent);

                                Log.d(TAG, "onDataChange: (1) Today - Selected event date: " + mEvent.getmEventDate() +
                                        " - Today date: " + mTodayDateString);
                            }



                            // Check all the events happening in the upcoming days
                            try {

                                Date mSelectedEventDate = mSimpleDateFormat.parse(mEvent.getmEventDate());
                                Date mTodayDate = mSimpleDateFormat.parse(mTodayDateString);

                                if (mSelectedEventDate.after(mTodayDate)) {

                                    mUpcomingEvents.add(mEvent);

                                    Log.d(TAG, "onDataChange: (2) Upcoming - Selected event date: " + mEvent.getmEventDate() +
                                            " - Today date: " + mTodayDateString);
                                }

                            } catch (Exception e) {

                                e.printStackTrace();
                            }


                            // Check all the events that have happened already
                            try {

                                Date mSelectedEventDate = mSimpleDateFormat.parse(mEvent.getmEventDate());
                                Date mTodayDate = mSimpleDateFormat.parse(mTodayDateString);

                                if (mSelectedEventDate.before(mTodayDate)) {

                                    mPastEvents.add(mEvent);

                                    Log.d(TAG, "onDataChange: (3) Past - Selected event date: " + mEvent.getmEventDate() +
                                            " - Today date: " + mTodayDateString);
                                }

                            } catch (Exception e) {

                                e.printStackTrace();
                            }

                        }
                    }

                    // TODO the event list needs to be cleared before this step as it is already repeated
                    // Send a message to the receiver with all the 3 array lists
                    Bundle mArraysBundle = new Bundle();
                    mArraysBundle.putSerializable(BUNDLE_EXTRA_TODAY_EVENTS, mTodayEvents);
                    mArraysBundle.putSerializable(BUNDLE_EXTRA_UPCOMING_EVENTS, mUpcomingEvents);
                    mArraysBundle.putSerializable(BUNDLE_EXTRA_PAST_EVENTS, mPastEvents);
                    mReceiver.send(Activity.RESULT_OK, mArraysBundle);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
