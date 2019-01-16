package com.example.prajwalramamurthy.letschill_finalproject.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.CreateEventFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.EditEventFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.MapFragment;

public class EditEventActivity extends AppCompatActivity implements EditEventFragment.EditEventInterface {

    // Variables
    private Event mEvent;

    // Constants
    public static final String BUNDLE_FORM_ALL_DATA_EDIT = "BUNDLE_FORM_ALL_DATA_EDIT";
    private static final int MAP_REQUESTING_RESULT_EDIT = 84;
    public static final String AFTER_CLICKING_MAP_ICON = "AFTER_CLICKING_MAP_ICON";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Edit event");
        setContentView(R.layout.activity_edit_event);

        // Get the custom object passed through the intent
        mEvent = getIntent().getParcelableExtra(DetailsEventActivity.ARGS_INTENT_OBJECT);

        if (mEvent != null) {

            // Inflate the edit fragment
            getSupportFragmentManager().beginTransaction().add(R.id.edit_frame, EditEventFragment.newInstance(mEvent, null, null, 0)).commit();
        }
    }

    @Override
    public void closeEditEventActivity() {

        // Close this activity
        finish();
    }

    @Override
    public void openMapActivity(Bundle bundle) {

        // Open the MapActivity
        Intent mMapIntent = new Intent(EditEventActivity.this, MapActivity.class);
        mMapIntent.putExtra(BUNDLE_FORM_ALL_DATA_EDIT, bundle);
        mMapIntent.putExtra(AFTER_CLICKING_MAP_ICON, 1);
        startActivityForResult(mMapIntent, MAP_REQUESTING_RESULT_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case MAP_REQUESTING_RESULT_EDIT:

                if (resultCode == Activity.RESULT_OK) {

                    String address = data.getStringExtra(MapActivity.INTENT_RESULT_ADDRESS);
                    Bundle allDataBundle = data.getBundleExtra(MapFragment.ARG_ALL_DATA_BUNDLE);

                    if (!address.isEmpty() && allDataBundle != null) {

                        // Pass the address to the fragment and restart the CreateEventFragment
                        getSupportFragmentManager().beginTransaction().replace(R.id.edit_frame, EditEventFragment.newInstance(mEvent, allDataBundle, address, 1)).commit();
                    }
                }

                break;
        }
    }
}
