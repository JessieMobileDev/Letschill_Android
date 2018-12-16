package com.example.prajwalramamurthy.letschill_finalproject.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.EditEventFragment;

public class EditEventActivity extends AppCompatActivity implements EditEventFragment.EditEventInterface {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Edit event");
        setContentView(R.layout.activity_edit_event);

        // Get the custom object passed through the intent
        Event mEvent = getIntent().getParcelableExtra(DetailsEventActivity.ARGS_INTENT_OBJECT);

        if (mEvent != null) {

            // Inflate the edit fragment
            getSupportFragmentManager().beginTransaction().add(R.id.edit_frame, EditEventFragment.newInstance(mEvent)).commit();
        }
    }

    @Override
    public void closeEditEventActivity() {

        // Close this activity
        finish();
    }
}
