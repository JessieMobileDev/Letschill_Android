package com.example.prajwalramamurthy.letschill_finalproject.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.CreateEventFragment;

public class EventActivity extends AppCompatActivity implements CreateEventFragment.CreateEventFragmentInterface {

    private CreateEventFragment createEventFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        setTitle("Add Event");

        createEventFragment = CreateEventFragment.newInstance();

        getSupportFragmentManager().beginTransaction().replace(R.id.event_frame, createEventFragment).commit();


    }

    @Override
    public void closeCreateEventActivity() {

        // Close current activity
        finish();
    }
}