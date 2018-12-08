package com.example.prajwalramamurthy.letschill_finalproject.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.InterestsFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.SignInFragment;

public class InterestsActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);

        getFragmentManager().beginTransaction().replace(R.id.interests_frame, InterestsFragment.newInstance()).commit();
    }
}
