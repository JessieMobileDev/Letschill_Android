package com.example.prajwalramamurthy.letschill_finalproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.CreateEventFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.DetailsEventFragment;

public class DetailsEventActivity extends AppCompatActivity implements DetailsEventFragment.DetailsEventInterface {

    // Constants
    public static final String ARGS_INTENT_OBJECT = "ARGS_INTENT_OBJECT";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_event);
        setTitle("Events");

        // Get the custom object out of the intent
        Event mEvent = getIntent().getParcelableExtra(MainActivity.EXTRA_INTENT_DETAILS);

        if (mEvent != null) {

            // Inflate the details fragment
            getFragmentManager().beginTransaction().add(R.id.details_activity_frame, DetailsEventFragment.newInstance(mEvent)).commit();

        }
    }

    @Override
    public void closeDetailsEventActivity(Event mEvent) {

        // Open "EditEventActivity"
        Intent mEditIntent = new Intent(DetailsEventActivity.this, EditEventActivity.class);
        mEditIntent.putExtra(ARGS_INTENT_OBJECT, mEvent);
        startActivity(mEditIntent);

        // Close this activity
        finish();
    }
}
