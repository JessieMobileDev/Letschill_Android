package com.example.prajwalramamurthy.letschill_finalproject.utility;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.TabPastFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.TabTodayFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.TabUpcomingFragment;

import java.util.ArrayList;

public class MainPageAdapter extends FragmentPagerAdapter {

    // Variables
    private final int mNumOfTabs;
    private ArrayList<Event> mTodayEvents, mUpcomingEvents, mPastEvents;


    // Constants
    public static final String ARGS_TODAYEVENTS = "ARGS_TODAYEVENTS";
    public static final String ARGS_UPCOMINGEVENTS = "ARGS_UPCOMINGEVENTS";
    public static final String ARGS_PASTEVENTS = "ARGS_PASTEVENTS";

    public MainPageAdapter(FragmentManager fm, int mNumOfTabs, ArrayList<Event> mTodayEvents, ArrayList<Event> mUpcomingEvents, ArrayList<Event> mPastEvents) {
        super(fm);
        this.mNumOfTabs = mNumOfTabs;
        this.mTodayEvents = mTodayEvents;
        this.mUpcomingEvents = mUpcomingEvents;
        this.mPastEvents = mPastEvents;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle mFragmentBundle = new Bundle();

        switch (position) {

            case 0: // Today


                // Pass the array list with all the events that are due today
                mFragmentBundle.putSerializable(ARGS_TODAYEVENTS, mTodayEvents);
                TabTodayFragment mTabTodayFragment = new TabTodayFragment();
                mTabTodayFragment.setArguments(mFragmentBundle);

                notifyDataSetChanged();

                Log.i("MAIN TODAY", "getItem: " + mTodayEvents.size());

                return mTabTodayFragment;
            case 1: // Upcoming



                // Pass the array list with all the events that are due on the upcoming days
                mFragmentBundle.putSerializable(ARGS_UPCOMINGEVENTS, mUpcomingEvents);
                TabUpcomingFragment mTabUpcomingFragment = new TabUpcomingFragment();
                mTabUpcomingFragment.setArguments(mFragmentBundle);


                notifyDataSetChanged();

                return mTabUpcomingFragment;
            case 2: // Past



                // Pass the array list with all the events that were due already
                mFragmentBundle.putSerializable(ARGS_PASTEVENTS, mPastEvents);
                TabPastFragment mTabPastFragment = new TabPastFragment();
                mTabPastFragment.setArguments(mFragmentBundle);

                notifyDataSetChanged();

                return mTabPastFragment;
        }

        return null;
    }

    @Override
    public int getCount() {

        return mNumOfTabs;
    }
}
