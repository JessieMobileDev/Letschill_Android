package com.example.prajwalramamurthy.letschill_finalproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.MainFragment;

public class MainActivity extends AppCompatActivity implements MainFragment.MainFragmentInterface {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Events");

        getSupportFragmentManager().beginTransaction().add(R.id.main_frame, MainFragment.newInstance()).commit();
    }

    @Override
    public void moveToCreateEventActivity() {

        // Move to "CreateEventActivity"
        Intent mCreateEventIntent = new Intent(MainActivity.this, CreateEventActivity.class);
        startActivity(mCreateEventIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
