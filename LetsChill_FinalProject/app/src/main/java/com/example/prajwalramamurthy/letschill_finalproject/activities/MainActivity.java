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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.MapFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.TabListViewFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.TabMapViewFragment;
import com.example.prajwalramamurthy.letschill_finalproject.utility.ConnectionHandler;
import com.example.prajwalramamurthy.letschill_finalproject.utility.DatabaseEventIntentService;
import com.example.prajwalramamurthy.letschill_finalproject.utility.EventCardAdapter;
import com.example.prajwalramamurthy.letschill_finalproject.utility.MainPageAdapter;
import com.example.prajwalramamurthy.letschill_finalproject.utility.MenuIntentHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        TabListViewFragment.TabTodayInterface, SearchView.OnQueryTextListener, TabMapViewFragment.TabMapViewInterface {

    // Variables
    private FloatingActionButton mFab;
    private MainPageAdapter mTabAdapter;
    private TabItem mTabToday;
    private TabItem mTabUpcoming;
    private TabItem mTabPast;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private DatabaseReference mDBReference;
    private final Handler mHandler = new Handler();
    private ArrayList<Event> mTodayEvents;
    private ArrayList<Event> mUpcomingEvents;
    private ArrayList<Event> mPastEvents;
    private ArrayList<Event> mAllEvents;
    private ProgressBar mProgressBar;
    MenuItem searchMenuItem;
    private EventCardAdapter mAdapter;
    private SharedPreferences mPrefs;

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

//        mAdapter = new EventCardAdapter(this, mUpcomingEvents);
//
//        ListView myListView = findViewById(R.id.Mylistviewtest);
//        myListView.setAdapter(mAdapter);

        // Assign the click listener to the floating button
        mFab.setOnClickListener(this);

        // Get current location
        requestLocation();

//        // Request events data from the database
//        requestEventData();

        FirebaseMessaging.getInstance().subscribeToTopic("user")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // create our search manager to handle the search functionality
        SearchManager searchManager = (SearchManager)
                Objects.requireNonNull(this).getSystemService(Context.SEARCH_SERVICE);
        //MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(Objects.requireNonNull(searchManager).
                getSearchableInfo(Objects.requireNonNull(this).getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        // create our search manager to handle the search functionality
        SearchManager searchManager = (SearchManager)
                Objects.requireNonNull(this).getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(Objects.requireNonNull(searchManager).
                getSearchableInfo(Objects.requireNonNull(this).getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        MenuIntentHandler.getMenuIntents(item, this, this, MenuIntentHandler.MAIN_ACTIVITY);

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
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (mAdapter != null && !newText.isEmpty()) {
            mAdapter.getFilter().filter(newText);
            mAdapter.notifyDataSetChanged();
        }
        else
        {

            mAdapter.filteredData = mUpcomingEvents;
            mAdapter.notifyDataSetChanged();
        }
        return false;
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

//                // Retrieve all the array lists from the bundle
//                mTodayEvents = (ArrayList<Event>) resultData.getSerializable(DatabaseEventIntentService.BUNDLE_EXTRA_TODAY_EVENTS);
//                mUpcomingEvents = (ArrayList<Event>) resultData.getSerializable(DatabaseEventIntentService.BUNDLE_EXTRA_UPCOMING_EVENTS);
//                mPastEvents = (ArrayList<Event>) resultData.getSerializable(DatabaseEventIntentService.BUNDLE_EXTRA_PAST_EVENTS);

                // Assign the adapter to the view pager that will display the screen for each tab item
                mTabAdapter = new MainPageAdapter(getSupportFragmentManager(), mTabLayout.getTabCount(),
                        mAllEvents, mCurrentLatitude, mCurrentLongitude);

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
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.fab_activity:

                // Move to "EventActivity"
                Intent mCreateEventIntent = new Intent(MainActivity.this, EventActivity.class);
                startActivity(mCreateEventIntent);

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
