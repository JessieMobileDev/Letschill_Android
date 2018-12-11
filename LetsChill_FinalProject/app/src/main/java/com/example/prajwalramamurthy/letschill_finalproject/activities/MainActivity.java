package com.example.prajwalramamurthy.letschill_finalproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.utility.MainPageAdapter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Variables
    private FloatingActionButton mFab;
    private MainPageAdapter mTabAdapter;
    private TabItem mTabToday;
    private TabItem mTabUpcoming;
    private TabItem mTabPast;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

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

        // Assign the click listener to the floating button
        mFab.setOnClickListener(this);

        // Assign the adapter to the view pager that will display the screen for each tab item
        mTabAdapter = new MainPageAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
        mViewPager.setAdapter(mTabAdapter);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.fab_activity:

                // Move to "CreateEventActivity"
                Intent mCreateEventIntent = new Intent(MainActivity.this, CreateEventActivity.class);
                startActivity(mCreateEventIntent);

                break;
        }
    }
}
