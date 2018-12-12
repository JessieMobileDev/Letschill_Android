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
import com.example.prajwalramamurthy.letschill_finalproject.utility.MyEventsAdapter;

public class MyEventsActivity extends AppCompatActivity {

    // Variables
    private MyEventsAdapter mTabAdapter;
    private TabItem mTabJoined;
    private TabItem mTabHosting;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

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

        // Assign the adapter to the view pager that will display the screen for each tab item
        mTabAdapter = new MyEventsAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
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
}
