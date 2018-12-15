package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;
import com.example.prajwalramamurthy.letschill_finalproject.utility.EventCardAdapter;
import com.example.prajwalramamurthy.letschill_finalproject.utility.MainPageAdapter;

import java.util.ArrayList;

public class TabTodayFragment extends ListFragment implements ListView.OnItemClickListener {

    // Variables
    private ListView mListView_today;
    private ArrayList<Event> mEventList = new ArrayList<>();

    public static TabTodayFragment newInstance() {
        
        Bundle args = new Bundle();
        
        TabTodayFragment fragment = new TabTodayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_tab_today, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() != null && getContext() != null) {



            // Find views
//            mEmptyState = getView().findViewById(R.id.emptyState_todayFrag);
//            mListView_today = getView().findViewById(R.id.listView_today_frag);
//            mListView_today.setEmptyView(mEmptyState);
//            mListView_today.setOnItemClickListener(this);

            // Populate the array list with the retrieved data from the database
            populateEventList();

            // Adapter that will populate the list view
            EventCardAdapter mAdapter = new EventCardAdapter(getContext(), mEventList);
//            mListView_today.setAdapter(mAdapter);
            setListAdapter(mAdapter);

//            if (mEventList.size() > 0) {
//
//                mEmptyState.setVisibility(View.INVISIBLE);
//
//            } else {
//
//                mEmptyState.setVisibility(View.VISIBLE);
//            }




        }
    }

    public void populateEventList() {

//        // TODO: Data below is for testing. Populate with database data. Check filter options.
//        mEventList.clear();
//        mEventList.add(new Event("Movies at Lincoln", "55 Lincoln Avenue", "Dec 20, 2018", "3:30pm", "6:30pm",
//                "Let's watch some movies!", "Me, John, and Jessie", "Movies", "Me", true, true));

        // Get the array list from the fragment arguments
        mEventList = (ArrayList<Event>) getArguments().getSerializable(MainPageAdapter.ARGS_TODAYEVENTS);

        // If the array list size is 0, then display the empty state
        Log.d("test", "populateEventList: list size: " + mEventList.size());

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // TODO: Do an intent to the details page and pass the event object to it using the position in the arrayList

    }
}
