package com.example.prajwalramamurthy.letschill_finalproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.TabPastFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.TabTodayFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.TabUpcomingFragment;
import com.example.prajwalramamurthy.letschill_finalproject.utility.ConnectionHandler;
import com.example.prajwalramamurthy.letschill_finalproject.utility.DatabaseEventIntentService;
import com.example.prajwalramamurthy.letschill_finalproject.utility.MainPageAdapter;
import com.example.prajwalramamurthy.letschill_finalproject.utility.MenuIntentHandler;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        TabTodayFragment.TabTodayInterface, TabUpcomingFragment.TabUpcomingInterface,
        TabPastFragment.TabPastInterface {

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
    private ArrayList<Event> mTodayEvents = new ArrayList<>();
    private ArrayList<Event> mUpcomingEvents = new ArrayList<>();
    private ArrayList<Event> mPastEvents = new ArrayList<>();
    private ProgressBar mProgressBar;

    // Constants
    public static final String EXTRA_DB_REQUEST_ID = "EXTRA_DB_REQUEST_ID";
    public static final String EXTRA_INTENT_DETAILS = "EXTRA_INTENT_DETAILS";
    private static final String TAG = "test";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Events");

        // Find Views
        mFab = findViewById(R.id.fab_activity);
        mTabToday = findViewById(R.id.tab_today);
        mTabUpcoming = findViewById(R.id.tab_upcoming);
        mTabPast = findViewById(R.id.tab_past);
        mViewPager = findViewById(R.id.viewPager_tabs);
        mTabLayout = findViewById(R.id.tablayout_events);
        mProgressBar = findViewById(R.id.progress_bar_main);

        // Assign the click listener to the floating button
        mFab.setOnClickListener(this);

        // Request events data from the database
        requestEventData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        MenuIntentHandler.getMenuIntents(item, this, this);
        return super.onOptionsItemSelected(item);
    }

    private void instantiateActivity() {

        setContentView(R.layout.activity_main);
        setTitle("Events");

        // Find Views
        mFab = findViewById(R.id.fab_activity);
        mTabToday = findViewById(R.id.tab_today);
        mTabUpcoming = findViewById(R.id.tab_upcoming);
        mTabPast = findViewById(R.id.tab_past);
        mViewPager = findViewById(R.id.viewPager_tabs);
        mTabLayout = findViewById(R.id.tablayout_events);
        mProgressBar = findViewById(R.id.progress_bar_main);

        // Assign the click listener to the floating button
        mFab.setOnClickListener(this);

        // Request events data from the database
        requestEventData();


    }

    private void requestEventData() {

        if (ConnectionHandler.isConnected(this)) {

            // Clear the lists before adding
            mTodayEvents.clear();
            mUpcomingEvents.clear();
            mPastEvents.clear();

            // Start an intent service to retrieve all the events' data
            // We're fetching all the events that the user is hosting, or just participating
            Intent mFetchIntent = new Intent(this, DatabaseEventIntentService.class);
            mFetchIntent.putExtra(DatabaseEventIntentService.EXTRA_RESULT_RECEIVER, new DatabaseEventDataReceiver());
            mFetchIntent.putExtra(EXTRA_DB_REQUEST_ID, 0);
            startService(mFetchIntent);

            // Set the progress bar to be visible
            mProgressBar.setVisibility(View.VISIBLE);

        } else {

            Toast.makeText(this, R.string.alert_content_noInternet, Toast.LENGTH_SHORT).show();
        }
    }



    public class DatabaseEventDataReceiver extends ResultReceiver {

        DatabaseEventDataReceiver() {
            super(mHandler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultData != null) {

                // Retrieve all the array lists from the bundle
                mTodayEvents = (ArrayList<Event>) resultData.getSerializable(DatabaseEventIntentService.BUNDLE_EXTRA_TODAY_EVENTS);
                mUpcomingEvents = (ArrayList<Event>) resultData.getSerializable(DatabaseEventIntentService.BUNDLE_EXTRA_UPCOMING_EVENTS);
                mPastEvents = (ArrayList<Event>) resultData.getSerializable(DatabaseEventIntentService.BUNDLE_EXTRA_PAST_EVENTS);

                Log.d(TAG, "onReceiveResult: Today list: " + mTodayEvents.size() + " - Upcoming list: " + mUpcomingEvents.size() +
                " - Past list: " + mPastEvents.size());

                // Assign the adapter to the view pager that will display the screen for each tab item
                mTabAdapter = new MainPageAdapter(getSupportFragmentManager(), mTabLayout.getTabCount(), mTodayEvents,
                        mUpcomingEvents, mPastEvents);
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

    @Override
    public void openDetailsPageFromPastTab(Event mEvent) {

        openDetailsPage(mEvent);
    }

    @Override
    public void openDetailsPageFromUpcomingTab(Event mEvent) {

        openDetailsPage(mEvent);
    }
}
