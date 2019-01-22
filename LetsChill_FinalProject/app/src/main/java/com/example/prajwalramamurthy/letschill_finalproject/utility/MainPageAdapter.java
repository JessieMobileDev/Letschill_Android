package com.example.prajwalramamurthy.letschill_finalproject.utility;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.MapFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.TabPastFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.TabTodayFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.TabUpcomingFragment;

import java.util.ArrayList;
import java.util.List;

public class MainPageAdapter extends FragmentStatePagerAdapter implements Filterable {

    // Variables
    private final int mNumOfTabs;
    private ArrayList<Event> mTodayEvents, mUpcomingEvents, mPastEvents;

    // Constants
    public static final String ARGS_TODAYEVENTS = "ARGS_TODAYEVENTS";
    public static final String ARGS_UPCOMINGEVENTS = "ARGS_UPCOMINGEVENTS";
   // public static final String ARGS_PASTEVENTS = "ARGS_PASTEVENTS";

    public MainPageAdapter(FragmentManager fm, int mNumOfTabs, ArrayList<Event> mTodayEvents, ArrayList<Event> mUpcomingEvents, ArrayList<Event> mPastEvents) {
        super(fm);
        this.mNumOfTabs = mNumOfTabs;
        this.mTodayEvents = mTodayEvents;
        this.mUpcomingEvents = mUpcomingEvents;
        this.mPastEvents = mPastEvents;
        this.filteredData = mUpcomingEvents;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {


        Bundle mFragmentBundle = new Bundle();

        switch (position) {

            case 0: // Today to Listview

                // Pass the array list with all the events that are due on the upcoming days
                mFragmentBundle.putSerializable(ARGS_UPCOMINGEVENTS, mUpcomingEvents);
                TabUpcomingFragment mTabUpcomingFragment = new TabUpcomingFragment();
                mTabUpcomingFragment.setArguments(mFragmentBundle);

                return mTabUpcomingFragment;



            case 1: // Upcoming to MapVIew

                // Pass the array list with all the events that are due today
                mFragmentBundle.putSerializable(ARGS_TODAYEVENTS, mTodayEvents);
                TabTodayFragment mTabTodayFragment = new TabTodayFragment();
                mTabTodayFragment.setArguments(mFragmentBundle);

                Log.i("MAIN TODAY", "getItem: " + mTodayEvents.size());

                return mTabTodayFragment;




//            case 2: // Past
//
//
//
//                // Pass the array list with all the events that were due already
//                mFragmentBundle.putSerializable(ARGS_PASTEVENTS, mPastEvents);
//                TabPastFragment mTabPastFragment = new TabPastFragment();
//                mTabPastFragment.setArguments(mFragmentBundle);
//
//
//                return mTabPastFragment;
        }

        return null;
    }

    // Get count
    @Override
    public int getCount(){


//        if(filteredData != null && filteredData.size() > 0){
//
//            return filteredData.size();
//        }
//
//        return 0;

        return mNumOfTabs;
    }

    private final MainPageAdapter.ItemFilter mFilter = new MainPageAdapter.ItemFilter();


    // will hanfle filter for the handymen
    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public List<Event> filteredData = null;

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<Event> list = mUpcomingEvents;

            int count = list.size();
            final ArrayList<Event> nlist = new ArrayList<>(count);

            Event filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.getmEventName().toLowerCase().contains(filterString)) {
                    nlist.add(filterableString);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<Event>) results.values;
            notifyDataSetChanged();
        }

    }


}

