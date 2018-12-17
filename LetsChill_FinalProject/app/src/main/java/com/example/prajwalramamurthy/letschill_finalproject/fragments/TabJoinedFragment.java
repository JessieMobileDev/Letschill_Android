package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;
import com.example.prajwalramamurthy.letschill_finalproject.utility.EventCardAdapter;
import com.example.prajwalramamurthy.letschill_finalproject.utility.MyEventsAdapter;

import java.util.ArrayList;

public class TabJoinedFragment extends ListFragment {

    // Variables
    private ArrayList<Event> mJoinedEventList = new ArrayList<>();
    private TabJoinedInterface mTabJoinedInterface;

    public interface TabJoinedInterface {

        void openDetailsPageFromJoinedTab(Event mEvent);
    }

    public static TabJoinedFragment newInstance() {
        
        Bundle args = new Bundle();
        
        TabJoinedFragment fragment = new TabJoinedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof TabJoinedInterface) {

            mTabJoinedInterface = (TabJoinedInterface)context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_joined, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() != null && getContext() != null && getArguments() != null) {

            // Retrieve the list view that was passed to this fragment through arguments
            mJoinedEventList = (ArrayList<Event>) getArguments().getSerializable(MyEventsAdapter.ARGS_JOINEDEVENTS);

            if (mJoinedEventList != null) {

                // Adapter that will populate the list view
                EventCardAdapter mAdapter = new EventCardAdapter(getContext(), mJoinedEventList);
                setListAdapter(mAdapter);

<<<<<<< HEAD
        // TODO: Data below is for testing. Populate with database data. Check filter options.
        mEventList.clear();
        mEventList.add(new Event("aaaa","Tennis", "55 Lincoln Avenue", "Dec 20, 2018", "3:30pm", "7:00pm",
                "Let's watch some movies!", "Me, John, and Jessie", "Movies", "Me", true, true, "test"));

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // TODO: Do an intent to the details page and pass the event object to it using the position in the arrayList
=======
                // Allow users to tap on the cards to see details
                getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
>>>>>>> 6e79e79566eee894e9b11ea9b0e153428808ab41

                        // Pass the selected event object to the "DetailsEventActivity"
                        mTabJoinedInterface.openDetailsPageFromJoinedTab(mJoinedEventList.get(position));
                    }
                });
            }
        }
    }
}
