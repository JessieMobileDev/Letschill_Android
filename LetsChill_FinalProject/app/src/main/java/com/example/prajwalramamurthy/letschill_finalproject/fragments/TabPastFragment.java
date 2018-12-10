package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class TabPastFragment extends Fragment {

    public static TabPastFragment newInstance() {
        
        Bundle args = new Bundle();
        
        TabPastFragment fragment = new TabPastFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
