package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.prajwalramamurthy.letschill_finalproject.R;

public class MainFragment extends Fragment implements View.OnClickListener{

    // Variables
    private FloatingActionButton mFab;
    private MainFragmentInterface mMainFragmentInterface;

    public interface MainFragmentInterface {
        void moveToCreateEventActivity();
    }

    public static MainFragment newInstance() {
        
        Bundle args = new Bundle();
        
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Verify if the interface is an instance of this context
        if (context instanceof MainFragmentInterface) {
            mMainFragmentInterface = (MainFragmentInterface)context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() != null) {

            // Find views
            mFab = getView().findViewById(R.id.fab_fragment);

            // Assign the click listener to the floating button
            mFab.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.fab_fragment:

                // Move to "CreateEventActivity"
                mMainFragmentInterface.moveToCreateEventActivity();
                break;
        }
    }
}
