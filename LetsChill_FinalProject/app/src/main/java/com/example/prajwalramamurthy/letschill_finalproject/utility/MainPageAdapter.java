package com.example.prajwalramamurthy.letschill_finalproject.utility;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.prajwalramamurthy.letschill_finalproject.fragments.TabPastFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.TabTodayFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.TabUpcomingFragment;

public class MainPageAdapter extends FragmentPagerAdapter {

    // Variables
    private int mNumOfTabs;

    public MainPageAdapter(FragmentManager fm, int mNumOfTabs) {
        super(fm);
        this.mNumOfTabs = mNumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0: // Today
                return new TabTodayFragment();
            case 1: // Upcoming
                return new TabUpcomingFragment();
            case 2: // Past
                return new TabPastFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
