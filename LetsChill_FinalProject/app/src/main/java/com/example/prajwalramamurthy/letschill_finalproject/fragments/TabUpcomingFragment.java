package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.content.Context;
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
import android.widget.ListView;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;
import com.example.prajwalramamurthy.letschill_finalproject.utility.EventCardAdapter;
import com.example.prajwalramamurthy.letschill_finalproject.utility.MainPageAdapter;

import java.util.ArrayList;

public class TabUpcomingFragment extends ListFragment {

    // Variables
    private ArrayList<Event> mEventList = new ArrayList<>();
    private TabUpcomingInterface mTabUpcomingInterface;

    public interface TabUpcomingInterface {

        void openDetailsPageFromUpcomingTab(Event mEvent);
    }

    public static TabUpcomingFragment newInstance() {
        
        Bundle args = new Bundle();
        
        TabUpcomingFragment fragment = new TabUpcomingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof TabUpcomingInterface) {

            mTabUpcomingInterface = (TabUpcomingInterface)context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_upcoming, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Populate the array list with the retrieved data from the database
        populateEventList();

        // Adapter that will populate the list view
        EventCardAdapter mAdapter = new EventCardAdapter(getContext(), mEventList);
        setListAdapter(mAdapter);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Pass the selected event object to the "DetailsEventActivity"
                mTabUpcomingInterface.openDetailsPageFromUpcomingTab(mEventList.get(position));
                Log.d("test", "onItemClick: ITEM WAS CLICKED YAY. position: " + position + " - id: " + id);

            }
        });
    }

    public void populateEventList() {

        // Get the array list from the fragment arguments
        mEventList = (ArrayList<Event>) getArguments().getSerializable(MainPageAdapter.ARGS_UPCOMINGEVENTS);
    }
}
