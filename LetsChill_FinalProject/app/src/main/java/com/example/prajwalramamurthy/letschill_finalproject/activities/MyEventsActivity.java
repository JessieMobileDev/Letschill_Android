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
import com.example.prajwalramamurthy.letschill_finalproject.fragments.TabHostingFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.TabJoinedFragment;
import com.example.prajwalramamurthy.letschill_finalproject.utility.ConnectionHandler;
import com.example.prajwalramamurthy.letschill_finalproject.utility.DatabaseEventIntentService;
import com.example.prajwalramamurthy.letschill_finalproject.utility.MainPageAdapter;
import com.example.prajwalramamurthy.letschill_finalproject.utility.MenuIntentHandler;
import com.example.prajwalramamurthy.letschill_finalproject.utility.MyEventsAdapter;

import java.util.ArrayList;

public class MyEventsActivity extends AppCompatActivity implements TabJoinedFragment.TabJoinedInterface, TabHostingFragment.TabHostingInterface {

    // Variables
    private MyEventsAdapter mTabAdapter;
    private TabItem mTabJoined;
    private TabItem mTabHosting;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private final Handler mHandler = new Handler();
    private ArrayList<Event> mJoinedEvents = new ArrayList<>();
    private ArrayList<Event> mHostingEvents = new ArrayList<>();
    private ProgressBar mProgressBar;
    private boolean isFirstRun;

    // Constants
    public static final String EXTRA_DB_REQUEST_ID_MYEVENTS = "EXTRA_DB_REQUEST_ID_MYEVENTS";
    public static final String EXTRA_INTENT_DETAILS = "EXTRA_INTENT_DETAILS";
    private static final String TAG = "test";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);
        setTitle("My Events");

        // Find Views
        mTabJoined = findViewById(R.id.tab_joined);
        mTabHosting = findViewById(R.id.tab_hosting);
        mViewPager = findViewById(R.id.viewPager_my_events_tabs);
        mTabLayout = findViewById(R.id.tablayout_my_events);
        mProgressBar = findViewById(R.id.progressBar_myEvents);

        // Request events data from the database
        requestEventData();
        isFirstRun = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isFirstRun) {

            requestEventData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        MenuIntentHandler.getMenuIntents(item, this, this, MenuIntentHandler.MY_EVENTS_ACTIVITY);
        return super.onOptionsItemSelected(item);
    }

    private void requestEventData() {

        if (ConnectionHandler.isConnected(this)) {

            // Start an intent service to retrieve all the events' data
            // We're fetching all the events that the user is hosting, or just participating
            Intent mFetchIntent = new Intent(this, DatabaseEventIntentService.class);
            mFetchIntent.putExtra(DatabaseEventIntentService.EXTRA_RESULT_RECEIVER, new MyEventsActivity.DatabaseEventDataReceiver());
            mFetchIntent.putExtra(EXTRA_DB_REQUEST_ID_MYEVENTS, 1);
            startService(mFetchIntent);

            if (isFirstRun) {

                isFirstRun = false;
            } else {

                isFirstRun = true;
            }

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
                mJoinedEvents = (ArrayList<Event>) resultData.getSerializable(DatabaseEventIntentService.BUNDLE_EXTRA_JOINED_EVENTS);
                mHostingEvents = (ArrayList<Event>) resultData.getSerializable(DatabaseEventIntentService.BUNDLE_EXTRA_HOSTING_EVENTS);

                Log.d(TAG, "onReceiveResult (received): Joined list: " + mJoinedEvents.size() + " - Hosting list: " + mHostingEvents.size());

                // Assign the adapter to the view pager that will display the screen for each tab item
                mTabAdapter = new MyEventsAdapter(getSupportFragmentManager(), mTabLayout.getTabCount(), mJoinedEvents,
                        mHostingEvents);

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
    public void openDetailsPageFromHostingTab(Event mEvent) {

        openDetailsPage(mEvent);
    }

    @Override
    public void openDetailsPageFromJoinedTab(Event mEvent) {

        openDetailsPage(mEvent);
    }

    private void openDetailsPage(Event mEvent) {

        // Pass the selected custom object to the DetailsEventActivity
        Intent mDetailsIntent = new Intent(MyEventsActivity.this, DetailsEventActivity.class);
        mDetailsIntent.putExtra(MainActivity.EXTRA_INTENT_DETAILS, mEvent);
        startActivity(mDetailsIntent);
    }
}
