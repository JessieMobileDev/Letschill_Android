package com.example.prajwalramamurthy.letschill_finalproject.activities;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.EventLocalStorage;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.User;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.MapFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.TabListViewFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.TabMapViewFragment;
import com.example.prajwalramamurthy.letschill_finalproject.utility.ConnectionHandler;
import com.example.prajwalramamurthy.letschill_finalproject.utility.DatabaseEventIntentService;
import com.example.prajwalramamurthy.letschill_finalproject.utility.EventCardAdapter;
import com.example.prajwalramamurthy.letschill_finalproject.utility.FormValidation;
import com.example.prajwalramamurthy.letschill_finalproject.utility.HelperMethods;
import com.example.prajwalramamurthy.letschill_finalproject.utility.LocalStorageHandler;
import com.example.prajwalramamurthy.letschill_finalproject.utility.MainPageAdapter;
import com.example.prajwalramamurthy.letschill_finalproject.utility.MenuIntentHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        TabListViewFragment.TabTodayInterface, TabMapViewFragment.TabMapViewInterface {


    // Variables
    private FloatingActionButton mFab;
    private MainPageAdapter mTabAdapter;
    private TabItem mTabToday;
    private TabItem mTabUpcoming;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private DatabaseReference mDBReference;
    private final Handler mHandler = new Handler();
    private ArrayList<Event> mAllEvents;
    private ArrayList<Event> mFilteredEvents;
    private ProgressBar mProgressBar;
    private MenuItem searchMenuItem;
    private SharedPreferences mPrefs;
    private EditText mEditText_search;
    private Button mButton_return;
    private boolean isSearching = false;
    private User mUser;
    private String mUid;

    // Constants
    public static final String EXTRA_DB_REQUEST_ID = "EXTRA_DB_REQUEST_ID";
    public static final String EXTRA_INTENT_DETAILS = "EXTRA_INTENT_DETAILS";
    public static final String EXTRA_LAT = "EXTRA_LAT";
    public static final String EXTRA_LONG = "EXTRA_LONG";
    public static final String EXTRA_PREFS_TAB_SELECTION = "EXTRA_PREFS_TAB_SELECTION";
    private static final int REQUEST_LOCATION_PERMISSION = 0x01101;
    private static final String TAG = "test";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Events");
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);


        // Find Views
        mFab = findViewById(R.id.fab_activity);
        mTabToday = findViewById(R.id.tab_today);
        mTabUpcoming = findViewById(R.id.tab_upcoming);
        mViewPager = findViewById(R.id.viewPager_tabs);
        mTabLayout = findViewById(R.id.tablayout_events);
        mProgressBar = findViewById(R.id.progress_bar_main);
        searchMenuItem = findViewById(R.id.action_search);
        mEditText_search = findViewById(R.id.editText_search);
        mButton_return = findViewById(R.id.button_returnSearch);

        // Assign the click listener to the floating button
        mFab.setOnClickListener(this);
        mButton_return.setOnClickListener(this);

        // Get current location
        requestLocation();

        FirebaseMessaging.getInstance().subscribeToTopic("user")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

        // When the user presses "done" after typing in the search edit text, the following happens
        mEditText_search.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event != null &&
                        event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    if (event == null || !event.isShiftPressed()) {

                        if (!isSearching) {

                            isSearching = true;
                            requestLocation();
                        } else {

                            requestLocation();
                        }

                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get the current logged in user
        mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot user: dataSnapshot.getChildren()) {
                    if (user.getKey().equals(mUid)) {
                        mUser = user.getValue(User.class);
//                        if (mUser != null && mUser.getUserID().equals(mUid)) {
//                            mUser = tempUser;
//                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Get current location
        requestLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {

            requestLocation();

        }
    }

    private void requestLocation() {

        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Get the current latitude and logitude of the device
            Location mCurrentLocation;
            if (mLocationManager != null) {

                mCurrentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (mCurrentLocation == null) {

                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            2000, 10.0f, (LocationListener) this);
                }

                mCurrentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                double mCurrentLongitude = mCurrentLocation.getLongitude();
                double mCurrentLatitude = mCurrentLocation.getLatitude();

                // Request events data from the database
                requestEventData(mCurrentLatitude, mCurrentLongitude);

            }
        } else {

            // If the app does not have the permission, then request it
            ActivityCompat.requestPermissions(this, new String[]
                    { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

       getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        MenuIntentHandler.getMenuIntents(item, this, this, MenuIntentHandler.MAIN_ACTIVITY);

        switch (item.getItemId()) {

            case R.id.action_search:

                // Display the edit text
                if (mEditText_search.getVisibility() == View.GONE) {

                    mEditText_search.setVisibility(View.VISIBLE);
                    mButton_return.setVisibility(View.VISIBLE);

                } else if (mEditText_search.getVisibility() == View.VISIBLE) {

                    mEditText_search.setVisibility(View.GONE);
                    mButton_return.setVisibility(View.GONE);

                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void requestEventData(double lat, double lng) {

        if (ConnectionHandler.isConnected(this)) {

            // Start an intent service to retrieve all the events' data
            // We're fetching all the events that the user is hosting, or just participating
            Intent mFetchIntent = new Intent(this, DatabaseEventIntentService.class);
            mFetchIntent.putExtra(DatabaseEventIntentService.EXTRA_RESULT_RECEIVER, new DatabaseEventDataReceiver());
            mFetchIntent.putExtra(EXTRA_LAT, lat);
            mFetchIntent.putExtra(EXTRA_LONG, lng);
            mFetchIntent.putExtra(EXTRA_DB_REQUEST_ID, 0);
            startService(mFetchIntent);

            // Set the progress bar to be visible
            mProgressBar.setVisibility(View.VISIBLE);

        } else {

            Toast.makeText(this, R.string.alert_content_noInternet, Toast.LENGTH_SHORT).show();

            // Retrieve an EventLocalStorage object arraylist, turn into Event array list and populate adapter
            mAllEvents = LocalStorageHandler.loadFromFileAndConvertEventLocalStorageToEvent(this, "allEvents");

            // Load the user's last known location
            ArrayList<Double> mLastKnownLocation = LocalStorageHandler.loadUserLastKnownLocation(this);

            Log.d("localStorage", "getStoredFiles: All events size: " + mAllEvents.size() +
                    " Location size: " + mLastKnownLocation.size());
            Log.d("localStorage", "getStoredFiles: data: " + mAllEvents.get(0).getmEventName());

            if (mLastKnownLocation != null && mAllEvents != null) {

                // Apply the adapter
                // Assign the adapter to the view pager that will display the screen for each tab item
                mTabAdapter = new MainPageAdapter(getSupportFragmentManager(), mTabLayout.getTabCount(),
                        mAllEvents, mLastKnownLocation.get(0), mLastKnownLocation.get(1));

                mTabAdapter.notifyDataSetChanged();
                mViewPager.setAdapter(mTabAdapter);

                // Set the progress bar to be invisible
                mProgressBar.setVisibility(View.GONE);

                // Manage what to display when a tab is selected
                mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {

                        mViewPager.setCurrentItem(tab.getPosition());


                        Log.d("test", "onTabSelected: tab was selected: " + tab.getText());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {


                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {


                    }
                });

                mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

            }
        }
    }

    @Override
    public void openDetailsEventFragment(Event event) {

        openDetailsPage(event);
    }

    public class DatabaseEventDataReceiver extends ResultReceiver {

        DatabaseEventDataReceiver() {
            super(mHandler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultData != null) {

                // Retrieve the array list containing all the events
                mAllEvents = (ArrayList<Event>) resultData.getSerializable(DatabaseEventIntentService.BUNDLE_EXTRA_ALL_EVENTS);
                double mCurrentLatitude = resultData.getDouble(EXTRA_LAT);
                double mCurrentLongitude = resultData.getDouble(EXTRA_LONG);

                if (isSearching) {

                    String userInput = mEditText_search.getText().toString().toLowerCase();
                    mFilteredEvents = new ArrayList<>();
                    mEditText_search.setText("");

                    for (Event savedEvent: mAllEvents) {

                        if (savedEvent.getmEventName().toLowerCase().contains(userInput)) {

                            mFilteredEvents.add(savedEvent);
                        }
                    }

                    // Assign the adapter to the view pager that will display the screen for each tab item
                    mTabAdapter = new MainPageAdapter(getSupportFragmentManager(), mTabLayout.getTabCount(),
                            mFilteredEvents, mCurrentLatitude, mCurrentLongitude);

                    // Turn boolean to false
                    isSearching = false;
                } else {

                    // Assign the adapter to the view pager that will display the screen for each tab item
                    mTabAdapter = new MainPageAdapter(getSupportFragmentManager(), mTabLayout.getTabCount(),
                            mAllEvents, mCurrentLatitude, mCurrentLongitude);
                }

                mTabAdapter.notifyDataSetChanged();
                mViewPager.setAdapter(mTabAdapter);

                // Set the progress bar to be invisible
                mProgressBar.setVisibility(View.GONE);

                // Manage what to display when a tab is selected
                mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {

                        mViewPager.setCurrentItem(tab.getPosition());


                        Log.d("test", "onTabSelected: tab was selected: " + tab.getText());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {


                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {


                    }
                });

                mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

                // Event object model is parcelable, we need one that is Serializable in order to save locally
                // Loop through the list and save as EventLocalStorage, and then save to local
                LocalStorageHandler.convertFromEventToEventLocalStorageAndSave(mAllEvents, MainActivity.this, "allEvents");

                // Save the user's current location to file
                LocalStorageHandler.saveUserLastKnownLocation(mCurrentLatitude, mCurrentLongitude, MainActivity.this);

            }

        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.fab_activity:

                if (HelperMethods.isUserVerified(mUser, this)) {

                    // Move to "EventActivity"
                    Intent mCreateEventIntent = new Intent(MainActivity.this, EventActivity.class);
                    startActivity(mCreateEventIntent);
                } else {

                    // Display and alert about not being verified
                    FormValidation.displayAlertNoId("Not verified", "Your account is not verified. Verify now in your profile in order to create an event!",
                            "OK", this);
                }

                break;
            case R.id.button_returnSearch:

//                requestLocation();
                Intent mFilterIntent = new Intent(this, SearchFilterActivity.class);
                startActivity(mFilterIntent);
                break;
        }
    }

    private void openDetailsPage(Event mEvent) {

        Intent mDetailsPageIntent = new Intent(MainActivity.this, DetailsEventActivity.class);
        mDetailsPageIntent.putExtra(EXTRA_INTENT_DETAILS, mEvent);
        startActivity(mDetailsPageIntent);
    }

    @Override
    public void openDetailsPageFromTodayTab(Event mEvent) {

        openDetailsPage(mEvent);
    }

}
