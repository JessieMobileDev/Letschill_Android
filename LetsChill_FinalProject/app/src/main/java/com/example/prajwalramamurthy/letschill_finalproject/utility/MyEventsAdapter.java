package com.example.prajwalramamurthy.letschill_finalproject.utility;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.TabHostingFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.TabJoinedFragment;

import java.util.ArrayList;

public class MyEventsAdapter extends FragmentStatePagerAdapter {

    // Variables
    private final int mNumOfTabs;
    private ArrayList<Event> mJoinedEvents, mHostingEvents;

    // Constants
    public static final String ARGS_JOINEDEVENTS = "ARGS_JOINEDEVENTS";
    public static final String ARGS_HOSTINGEVENTS = "ARGS_HOSTINGEVENTS";

    public MyEventsAdapter(FragmentManager fm, int mNumOfTabs, ArrayList<Event> mJoinedEvents, ArrayList<Event> mHostingEvents) {
        super(fm);
        this.mNumOfTabs = mNumOfTabs;
        this.mJoinedEvents = mJoinedEvents;
        this.mHostingEvents = mHostingEvents;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle mFragmentBundle = new Bundle();

        switch (position) {

            case 0: // Joined

                // Pass the array list with all the events that the current logged in user is participating
                mFragmentBundle.putSerializable(ARGS_JOINEDEVENTS, mJoinedEvents);
                TabJoinedFragment mTabJoinedFragment = new TabJoinedFragment();
                mTabJoinedFragment.setArguments(mFragmentBundle);

                return mTabJoinedFragment;

            case 1: // Hosting

                // Pass the array list with all the events that the current logged in user is hosting
                mFragmentBundle.putSerializable(ARGS_HOSTINGEVENTS, mHostingEvents);
                TabHostingFragment mTabHostingFragment = new TabHostingFragment();
                mTabHostingFragment.setArguments(mFragmentBundle);

                return mTabHostingFragment;

        }

        return null;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
