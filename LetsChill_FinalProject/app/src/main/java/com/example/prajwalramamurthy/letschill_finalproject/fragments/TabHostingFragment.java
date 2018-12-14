package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;
import com.example.prajwalramamurthy.letschill_finalproject.utility.EventCardAdapter;

import java.util.ArrayList;

public class TabHostingFragment extends Fragment implements ListView.OnItemClickListener {

    // Variables
    private ListView mListView_hosting;
    private final ArrayList<Event> mEventList = new ArrayList<>();
    
    public static TabHostingFragment newInstance() {
        
        Bundle args = new Bundle();
        
        TabHostingFragment fragment = new TabHostingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_hosting, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() != null && getContext() != null) {

            // Find views
            mListView_hosting = getView().findViewById(R.id.listView_hosting);
            mListView_hosting.setOnItemClickListener(this);

            // Populate the array list with the retrieved data from the database
            populateEventList();

            // Adapter that will populate the list view
            EventCardAdapter mAdapter = new EventCardAdapter(getContext(), mEventList);
            mListView_hosting.setAdapter(mAdapter);
        }
    }

    public void populateEventList() {

        // TODO: Data below is for testing. Populate with database data. Check filter options.
        mEventList.clear();
        mEventList.add(new Event("Tennis", "55 Lincoln Avenue", "Dec 20, 2018", "3:30pm", "7:00pm",
                "Let's watch some movies!", "Me, John, and Jessie", "Movies", "Me", true, true));

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // TODO: Do an intent to the details page and pass the event object to it using the position in the arrayList

    }
}
