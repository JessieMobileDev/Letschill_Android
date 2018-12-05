package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.app.Fragment;
import android.os.Bundle;

public class InterestsFragment extends Fragment
{
    public static InterestsFragment newInstance() {

        Bundle args = new Bundle();

        InterestsFragment fragment = new InterestsFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
