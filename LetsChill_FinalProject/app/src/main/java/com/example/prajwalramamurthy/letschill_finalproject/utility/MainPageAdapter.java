package com.example.prajwalramamurthy.letschill_finalproject.utility;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.widget.Filter;
import android.widget.Filterable;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.TabListViewFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.TabMapViewFragment;
import java.util.ArrayList;
import java.util.List;

public class MainPageAdapter extends FragmentStatePagerAdapter implements Filterable {

    // Variables
    private final int mNumOfTabs;
    private ArrayList<Event> mAllEvents;
    private double mLatitude;
    private double mLongitude;

    // Constants
    public static final String ARGS_ALL_EVENTS = "ARGS_ALL_EVENTS";
    public static final String ARGS_LATITUDE = "ARGS_LATITUDE";
    public static final String ARGS_LONGITUDE = "ARGS_LONGITUDE";

    public MainPageAdapter(FragmentManager fm, int mNumOfTabs, ArrayList<Event> mAllEvents, double lat, double lng) {
        super(fm);
        this.mNumOfTabs = mNumOfTabs;
        this.mAllEvents = mAllEvents;
        this.mLatitude = lat;
        this.mLongitude = lng;

//        this.filteredData = mUpcomingEvents;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {


        Bundle mFragmentBundle = new Bundle();

        switch (position) {

            case 0: // Today to ListView

                // Pass the array list with all the events that are due on the upcoming days
                mFragmentBundle.putSerializable(ARGS_ALL_EVENTS, mAllEvents);
                TabListViewFragment mTabListViewFragment = new TabListViewFragment();
                mTabListViewFragment.setArguments(mFragmentBundle);

                return mTabListViewFragment;



            case 1: // Upcoming to MapView

                // Pass the array list with all the events that are due today
                mFragmentBundle.putSerializable(ARGS_ALL_EVENTS, mAllEvents);
                mFragmentBundle.putDouble(ARGS_LATITUDE, mLatitude);
                mFragmentBundle.putDouble(ARGS_LONGITUDE, mLongitude);

                Log.d("boa", "getItem: lat  view: " + mLatitude + " long view: " + mLongitude + " array size: " + mAllEvents.size());
                TabMapViewFragment mTabMapViewFragment = new TabMapViewFragment();
                mTabMapViewFragment.setArguments(mFragmentBundle);

                return mTabMapViewFragment;
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

            final List<Event> list = mAllEvents;

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

