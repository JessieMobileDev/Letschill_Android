package com.example.prajwalramamurthy.letschill_finalproject.utility;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.prajwalramamurthy.letschill_finalproject.fragments.TabHostingFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.TabJoinedFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.TabPastFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.TabTodayFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.TabUpcomingFragment;

public class MyEventsAdapter extends FragmentPagerAdapter {

    // Variables
    private final int mNumOfTabs;

    public MyEventsAdapter(FragmentManager fm, int mNumOfTabs) {
        super(fm);
        this.mNumOfTabs = mNumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0: // Joined
                return new TabJoinedFragment();
            case 1: // Hosting
                return new TabHostingFragment();

        }

        return null;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
