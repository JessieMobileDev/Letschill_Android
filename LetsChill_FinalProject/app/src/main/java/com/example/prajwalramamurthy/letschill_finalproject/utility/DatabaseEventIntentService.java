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
import android.widget.ImageView;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.activities.MainActivity;
import com.example.prajwalramamurthy.letschill_finalproject.activities.MyEventsActivity;
import com.example.prajwalramamurthy.letschill_finalproject.activities.SignInUpActivity;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.User;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.EditEventFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.SignUpFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class DatabaseEventIntentService extends IntentService {

    // Variables
    private DatabaseReference mDBReference;
    private ArrayList<Event> mJoinedEvents;
    private ArrayList<Event> mHostingEvents;
    private ArrayList<Event> mAllEvents;
    private String mUsername;

    // Constants
    public static final String EXTRA_RESULT_RECEIVER = "com.example.prajwalramamurthy.letschill_finalproject.utility.EXTRA_RESULT_RECEIVER";
    private static final String TAG = "test";
    public static final String BUNDLE_EXTRA_JOINED_EVENTS = "BUNDLE_EXTRA_JOINED_EVENTS";
    public static final String BUNDLE_EXTRA_HOSTING_EVENTS = "BUNDLE_EXTRA_HOSTING_EVENTS";
    public static final String BUNDLE_EXTRA_ALL_EVENTS = "BUNDLE_EXTRA_ALL_EVENTS";

    public DatabaseEventIntentService() {
        super("DatabaseEventIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {

        // If the intent passed does not contain any of the extras variables, throw an exception
        if (!Objects.requireNonNull(intent).hasExtra(EXTRA_RESULT_RECEIVER)) {
            throw new IllegalArgumentException("EXTRA_RESULT_RECEIVER is missing!");
        }

        final ResultReceiver mReceiver = intent.getParcelableExtra(EXTRA_RESULT_RECEIVER);

        Integer mRequestID = intent.getIntExtra(MainActivity.EXTRA_DB_REQUEST_ID, 3);
        Integer mRequestMyEvents = intent.getIntExtra(MyEventsActivity.EXTRA_DB_REQUEST_ID_MYEVENTS, 3);
        Integer mRequestDeleteID = intent.getIntExtra(EditEventFragment.EXTRA_DB_DELETE_ID, 3);

        // Get current month
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String mTodayDateString = mSimpleDateFormat.format(Calendar.getInstance().getTime());
        Date date = new Date();
        String monthIndex = "Month";
        try {

            // Convert the string date to a date variable and extract the month out of it
            date = mSimpleDateFormat.parse(mTodayDateString);
            monthIndex = String.valueOf(date.getMonth());



        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Get the reference of the database under "Events"
        mDBReference = FirebaseDatabase.getInstance().getReference("Events").child(monthIndex);

        if (mRequestID == 0) {

            // Collect the preferences passed by the intent
//            final String mFilterSelection = intent.getStringExtra(MainActivity.EXTRA_FILTER_SELECTION);
//            int mRadiusMiles = intent.getIntExtra(MainActivity.EXTRA_FILTER_RADIUS, 3);
//            final ArrayList<String> mSelectedInterests = intent.getStringArrayListExtra(MainActivity.EXTRA_FILTER_INTERESTS);

            mDBReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    // Clear the list before adding
                    mAllEvents = new ArrayList<>();

                    for (DataSnapshot day: dataSnapshot.getChildren()) {

                        for (DataSnapshot event: day.getChildren()) {
                            Event mEvent = event.getValue(Event.class);

                            // Do checks based on the filter selection
                            if (mEvent != null && !mEvent.ismIsDeleted()) {

                                mAllEvents.add(mEvent);

                            } else {

                                Log.d(TAG, "onDataChange: event is null (today)");
                            }
                        }
                    }

                    // Retrieve the latitude and longitude from the extras
                    double mLatitude = intent.getDoubleExtra(MainActivity.EXTRA_LAT, 0.0);
                    double mLongitude = intent.getDoubleExtra(MainActivity.EXTRA_LONG, 0.0);

                    // Send a message to the receiver with all the 3 array lists
                    Bundle mArraysBundle = new Bundle();
                    mArraysBundle.putSerializable(BUNDLE_EXTRA_ALL_EVENTS, mAllEvents);
                    mArraysBundle.putDouble(MainActivity.EXTRA_LAT, mLatitude);
                    mArraysBundle.putDouble(MainActivity.EXTRA_LONG, mLongitude);
                    mReceiver.send(Activity.RESULT_OK, mArraysBundle);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        if (mRequestMyEvents == 1) {

            // Collect the current logged in user name
            FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            final String mUid = mFirebaseUser.getUid();

            final DatabaseReference mDBUsersReference = FirebaseDatabase.getInstance().getReference("Users");

            mDBUsersReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    // Collect the user name
                    mUsername = dataSnapshot.child(mUid).child("username").getValue(String.class);
                    Log.d(TAG, "onReceiveResult: My events username: " + mUsername);

                    mDBReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            // Clear the lists before adding to them
                            mJoinedEvents = new ArrayList<>();
                            mHostingEvents = new ArrayList<>();

                            for (DataSnapshot day: dataSnapshot.getChildren()) {

                                for (DataSnapshot event: day.getChildren()) {

                                    final Event mEvent = event.getValue(Event.class);

                                    if (mEvent != null) {

                                        if (mEvent.getmHost().equals(mUsername) && !mEvent.ismIsDeleted()) {
                                            Log.d(TAG, "onReceiveResult: User name: " + mUsername);

                                            mHostingEvents.add(mEvent);

                                        }

                                        if (!mEvent.getmHost().equals(mUsername)) {

                                            Log.d(TAG, "onReceiveResult: User uid: " + mUid);

                                            // Look for events that the current logged in user is participating
                                            mDBUsersReference.child(mUid).child("joinedEvents").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    for (DataSnapshot joinedEvents : dataSnapshot.getChildren()) {

                                                        final String mJoinedEventID = joinedEvents.getValue(String.class);

                                                        if (mJoinedEventID != null) {

                                                            // Compare the current event on the loop with the joined event id
                                                            if (mEvent.getmEventId().equals(mJoinedEventID)) {

                                                                Log.d(TAG, "onDataChange: Comparing: Event id with the user joined events: " + mEvent.getmEventId() + " --- " + mJoinedEventID);


                                                                Log.d(TAG, "onDataChange (joined list before adding): size: " + mJoinedEvents.size());
//                                                            if (!mJoinedEvents.contains(mEvent)) {

                                                                mJoinedEvents.add(mEvent);
                                                                Log.d(TAG, "onDataChange (joined list after adding): size: " + mJoinedEvents.size());
//                                                            }


                                                            }
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }
                                }
                            }

                            // Send a message to the receiver with all the 2 array lists
                            Bundle mArraysBundle = new Bundle();
                            mArraysBundle.putSerializable(BUNDLE_EXTRA_JOINED_EVENTS, mJoinedEvents);
                            mArraysBundle.putSerializable(BUNDLE_EXTRA_HOSTING_EVENTS, mHostingEvents);
                            mReceiver.send(Activity.RESULT_OK, mArraysBundle);

                            Log.d(TAG, "onReceiveResult: Joined list: " + mJoinedEvents.size() + " - Hosting list: " + mHostingEvents.size());


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
