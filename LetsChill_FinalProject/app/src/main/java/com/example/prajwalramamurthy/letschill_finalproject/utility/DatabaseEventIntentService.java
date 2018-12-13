package com.example.prajwalramamurthy.letschill_finalproject.utility;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.prajwalramamurthy.letschill_finalproject.activities.MainActivity;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.SignUpFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class DatabaseEventIntentService extends IntentService {

    // Variables
    private DatabaseReference mDBReference;
    private ArrayList<Event> mTodayEvents;
    private ArrayList<Event> mUpcomingEvents;
    private ArrayList<Event> mPastEvents;
    private SharedPreferences mPrefs;

    // Constants
    public static final String EXTRA_RESULT_RECEIVER = "com.example.prajwalramamurthy.letschill_finalproject.utility.EXTRA_RESULT_RECEIVER";

    public DatabaseEventIntentService() {
        super("DatabaseEventIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        // If the intent passed does not contain any of the extras variables, throw an exception
        if (!Objects.requireNonNull(intent).hasExtra(EXTRA_RESULT_RECEIVER)) {
            throw new IllegalArgumentException("EXTRA_RESULT_RECEIVER is missing!");
        }

        final ResultReceiver receiver = intent.getParcelableExtra(EXTRA_RESULT_RECEIVER);

        Integer mRequestID = intent.getIntExtra(MainActivity.EXTRA_DB_REQUEST_ID, 1);

        // Get the database reference and instantiate arrayLists
        mDBReference = FirebaseDatabase.getInstance().getReference("Events");
        mTodayEvents = new ArrayList<>();
        mUpcomingEvents = new ArrayList<>();
        mPastEvents = new ArrayList<>();

        // Instantiate the SharedPreferences
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Retrieve the user's uid from SharedPreferences
        final String mUserUID = mPrefs.getString(SignUpFragment.PREFS_USER_UID, "default");

        if (mRequestID == 0) {

            mDBReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot event: dataSnapshot.getChildren()) {

                        Event mEvent = event.getValue(Event.class);

                        if (mEvent != null) {

                            // Check all the events happening today

                            // Check all the events happening in the upcoming days

                            // Check all the events that have happened already

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
