package com.example.prajwalramamurthy.letschill_finalproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.InterestsFragment;

public class InterestsActivity extends AppCompatActivity implements InterestsFragment.InterestsFragmentInterface {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);
        setTitle("Interests");

        getFragmentManager().beginTransaction().replace(R.id.interests_frame, InterestsFragment.newInstance()).commit();
    }

    @Override
    public void moveToMainActivityFromInterests() {

        // Move to "MainActivity"
        Intent mMainIntent = new Intent(InterestsActivity.this, MainActivity.class);
        startActivity(mMainIntent);
    }
}
