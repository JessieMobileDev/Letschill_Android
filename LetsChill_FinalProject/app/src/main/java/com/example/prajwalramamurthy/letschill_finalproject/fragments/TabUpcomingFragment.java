package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class TabUpcomingFragment extends Fragment {

    public static TabUpcomingFragment newInstance() {
        
        Bundle args = new Bundle();
        
        TabUpcomingFragment fragment = new TabUpcomingFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
