package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class TabTodayFragment extends Fragment {

    public static TabTodayFragment newInstance() {
        
        Bundle args = new Bundle();
        
        TabTodayFragment fragment = new TabTodayFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
