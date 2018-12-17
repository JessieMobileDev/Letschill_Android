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

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class DatabaseEventIntentService extends IntentService {

    // Variables
    private DatabaseReference mDBReference;
    private ArrayList<Event> mTodayEvents;
    private ArrayList<Event> mUpcomingEvents;
    private ArrayList<Event> mPastEvents;
    private ArrayList<Event> mJoinedEvents;
    private ArrayList<Event> mHostingEvents;
    private String mUsername;

    // Constants
    public static final String EXTRA_RESULT_RECEIVER = "com.example.prajwalramamurthy.letschill_finalproject.utility.EXTRA_RESULT_RECEIVER";
    private static final String TAG = "test";
    public static final String BUNDLE_EXTRA_TODAY_EVENTS = "BUNDLE_EXTRA_TODAY_EVENTS";
    public static final String BUNDLE_EXTRA_UPCOMING_EVENTS = "BUNDLE_EXTRA_UPCOMING_EVENTS";
    public static final String BUNDLE_EXTRA_PAST_EVENTS = "BUNDLE_EXTRA_PAST_EVENTS";
    public static final String BUNDLE_EXTRA_JOINED_EVENTS = "BUNDLE_EXTRA_JOINED_EVENTS";
    public static final String BUNDLE_EXTRA_HOSTING_EVENTS = "BUNDLE_EXTRA_HOSTING_EVENTS";
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

        Integer mRequestID = intent.getIntExtra(MainActivity.EXTRA_DB_REQUEST_ID, 3);
        Integer mRequestMyEvents = intent.getIntExtra(MyEventsActivity.EXTRA_DB_REQUEST_ID_MYEVENTS, 3);
        Integer mRequestDeleteID = intent.getIntExtra(EditEventFragment.EXTRA_DB_DELETE_ID, 3);

        // Get the reference of the database under "Events"
        mDBReference = FirebaseDatabase.getInstance().getReference("Events");

        if (mRequestID == 0) {

            mDBReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    // Clear the lists before adding
                    mTodayEvents = new ArrayList<>();
                    mUpcomingEvents = new ArrayList<>();
                    mPastEvents = new ArrayList<>();

                    Log.d(TAG, "onReceiveResult (service): Today list: " + mTodayEvents.size() + " - Upcoming list: " + mUpcomingEvents.size() +
                            " - Past list: " + mPastEvents.size());

                    for (DataSnapshot event: dataSnapshot.getChildren()) {

                        Event mEvent = event.getValue(Event.class);

                        if (mEvent != null) {

                            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("M-dd-yyyy");
                            String mTodayDateString = mSimpleDateFormat.format(Calendar.getInstance().getTime());

                            // Check all the events happening today
                            if (mEvent.getmEventDate().equals(mTodayDateString)) {

                                // If the event deletion is set to false, then display
                                if (!mEvent.ismIsDeleted()) {

                                    mTodayEvents.add(mEvent);

                                    Log.d(TAG, "onDataChange (today): (1) Today - Selected event date: " + mEvent.getmEventDate() +
                                            " - Today date: " + mTodayDateString);
                                }
                            }

                            // Check all the events happening in the upcoming days
                            try {

                                Date mSelectedEventDate = mSimpleDateFormat.parse(mEvent.getmEventDate());
                                Date mTodayDate = mSimpleDateFormat.parse(mTodayDateString);

                                if (mSelectedEventDate.after(mTodayDate)) {

                                    // If the event deletion is set to false, then display
                                    if (!mEvent.ismIsDeleted()) {

                                        mUpcomingEvents.add(mEvent);

                                        Log.d(TAG, "onDataChange (upcoming): (2) Upcoming - Selected event date: " + mEvent.getmEventDate() +
                                                " - Today date: " + mTodayDateString);
                                    }
                                }

                            } catch (Exception e) {

                                e.printStackTrace();
                            }


                            // Check all the events that have happened already
                            try {

                                Date mSelectedEventDate = mSimpleDateFormat.parse(mEvent.getmEventDate());
                                Date mTodayDate = mSimpleDateFormat.parse(mTodayDateString);

                                if (mSelectedEventDate.before(mTodayDate)) {

                                    // If the event deletion is set to false, then display
                                    if (!mEvent.ismIsDeleted()) {

                                        mPastEvents.add(mEvent);

                                        Log.d(TAG, "onDataChange (past): (3) Past - Selected event date: " + mEvent.getmEventDate() +
                                                " - Today date: " + mTodayDateString);
                                    }
                                }

                            } catch (Exception e) {

                                e.printStackTrace();
                            }

                        }
                    }

                    Log.d(TAG, "onReceiveResult (service - after populating): Today list: " + mTodayEvents.size() + " - Upcoming list: " + mUpcomingEvents.size() +
                            " - Past list: " + mPastEvents.size());

                    // TODO the event list needs to be cleared before this step as it is already repeated
                    // Send a message to the receiver with all the 3 array lists
                    Bundle mArraysBundle = new Bundle();
                    mArraysBundle.putSerializable(BUNDLE_EXTRA_TODAY_EVENTS, mTodayEvents);
                    mArraysBundle.putSerializable(BUNDLE_EXTRA_UPCOMING_EVENTS, mUpcomingEvents);
                    mArraysBundle.putSerializable(BUNDLE_EXTRA_PAST_EVENTS, mPastEvents);
                    mReceiver.send(Activity.RESULT_OK, mArraysBundle);

                    Log.d(TAG, "onDataChange: ");
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

            DatabaseReference mDBUsersReference = FirebaseDatabase.getInstance().getReference("Users");

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

                            for (DataSnapshot event: dataSnapshot.getChildren()) {

                                Event mEvent = event.getValue(Event.class);

                                if (mEvent != null) {

                                    // TODO: GOLD PHASE: Pull data based on people who joined an event

                                    Log.d(TAG, "onReceiveResult: Host name: " + mEvent.getmHost());
                                    if (mEvent.getmHost().equals(mUsername)) {

                                        mHostingEvents.add(mEvent);
                                    }
                                }
                            }

                            Log.d(TAG, "onReceiveResult: Joined list: " + mJoinedEvents.size() + " - Hosting list: " + mHostingEvents.size());

                            // Send a message to the receiver with all the 2 array lists
                            Bundle mArraysBundle = new Bundle();
                            mArraysBundle.putSerializable(BUNDLE_EXTRA_JOINED_EVENTS, mJoinedEvents);
                            mArraysBundle.putSerializable(BUNDLE_EXTRA_HOSTING_EVENTS, mHostingEvents);
                            mReceiver.send(Activity.RESULT_OK, mArraysBundle);
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

        // Reference to an image file in Cloud Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();



        if (mRequestDeleteID == 2) {

            // Retrieve the event passed through the intent
            final Event mEvent = intent.getParcelableExtra(EditEventFragment.ARGS_OBJECT);

            if (mEvent != null) {

                // Loop through the events and look for the same to change the variable isDeleted to true
                mDBReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot event: dataSnapshot.getChildren()) {

                            Event mRetrievedEvent = event.getValue(Event.class);

                            if (mRetrievedEvent != null) {

                                if (mRetrievedEvent.getmEventId().equals(mEvent.getmEventId())) {

                                    // If Ids are a match, change the variable isDeleted to true
                                    Event mChangedEvent = new Event(mRetrievedEvent.getmEventId(), mRetrievedEvent.getmEventName(),
                                            mRetrievedEvent.getmEventLocation(), mRetrievedEvent.getmEventDate(), mRetrievedEvent.getmEventTimeStart(),
                                            mRetrievedEvent.getmEventTimeFinish(), mRetrievedEvent.getmDescription(), mRetrievedEvent.getmParticipants(),
                                            mRetrievedEvent.getmCategory(), mRetrievedEvent.getmHost(), mRetrievedEvent.ismIsRecurringEvent(),
                                            mRetrievedEvent.ismPublicOrPrivate(), mRetrievedEvent.getmUrl(), true);

                                    // Get the reference in the database using the event ID
                                    mDBReference = FirebaseDatabase.getInstance().getReference("Events").child(mRetrievedEvent.getmEventId());

                                    // Save the changed event back to the database
                                    mDBReference.setValue(mChangedEvent);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                // Send an ok to the EditEventFragment
                mReceiver.send(Activity.RESULT_OK, null);
            }
        }
    }
}
