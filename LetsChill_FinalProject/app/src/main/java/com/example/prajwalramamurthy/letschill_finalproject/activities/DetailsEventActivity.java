package com.example.prajwalramamurthy.letschill_finalproject.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.DetailsEventFragment;

public class DetailsEventActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event);
        setTitle("Add Event");

        getFragmentManager().beginTransaction().replace(R.id.create_event_frame, DetailsEventFragment.newInstance()).commit();


    }
}
