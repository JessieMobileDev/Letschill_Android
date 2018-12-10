package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.prajwalramamurthy.letschill_finalproject.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InterestsFragment extends Fragment implements ListView.OnItemClickListener {

    // Variables
    private boolean isInterestsSelected = false;
    private int mClicksCounter = 0;
    private MenuItem mMenu;
    private ListView mListView;
    private List<String> mInterestsList = new ArrayList<>(Arrays.asList("Video Game", "Sports", "Technology", "Outdoor Activities", "Indoor Activities", "Arts", "Music", "Movies", "Auto", "Food", "Fitness"));

    public static InterestsFragment newInstance() {

        Bundle args = new Bundle();

        InterestsFragment fragment = new InterestsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_interests, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        mMenu = menu.findItem(R.id.action_done_interests);
        inflater.inflate(R.menu.interests_menu, menu);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() != null && getContext() != null) {

            // Find views
            mListView = getView().findViewById(R.id.listview_interests);

            // Adapter
            ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, mInterestsList);

            // Set the adapter to the list view
            mListView.setAdapter(mArrayAdapter);

            // Assign click listener on the list view
            mListView.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // Update the counter of elements clicked
        mClicksCounter+=1;

        // Add a checkmark by the cell that was tapped
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setItemChecked(position, true);

        Log.d("test", "onItemClick: position: " + position + " id: " + id);

        if (mClicksCounter >= 3) {

            // Show the menu if at least 3 were selected
//            mMenu.setVisible(true);
        }
    }
}
