package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class EditEventFragment extends Fragment {

    public static EditEventFragment newInstance() {
        
        Bundle args = new Bundle();
        
        EditEventFragment fragment = new EditEventFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
