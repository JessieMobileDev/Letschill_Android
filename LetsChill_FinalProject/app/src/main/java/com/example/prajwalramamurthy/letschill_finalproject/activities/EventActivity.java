package com.example.prajwalramamurthy.letschill_finalproject.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.CreateEventFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.MapFragment;

import java.io.IOException;
import java.util.Objects;

public class EventActivity extends AppCompatActivity implements CreateEventFragment.CreateEventFragmentInterface {

    // Variables
    private CreateEventFragment createEventFragment;

    // Constants
    private static final int PICTURE_REQUEST = 0x0101;
    public static final int MAP_REQUESTING_RESULT = 737;
    public static final String BUNDLE_FORM_ALL_DATA = "BUNDLE_FORM_ALL_DATA";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        setTitle("Add Event");

        createEventFragment = CreateEventFragment.newInstance("no address", null);

        getSupportFragmentManager().beginTransaction().replace(R.id.event_frame, createEventFragment).commit();



    }

    @Override
    public void closeCreateEventActivity() {

        // Close current activity
        finish();


    }

    @Override
    public void imageUploader() {

        Intent mGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(mGalleryIntent, "Select Image from Gallery"), PICTURE_REQUEST);

    }

    @Override
    public void openMapActivity(Bundle bundle) {

        // Open the MapActivity
        Intent mMapIntent = new Intent(this, MapActivity.class);
        mMapIntent.putExtra(BUNDLE_FORM_ALL_DATA, bundle);
        startActivityForResult(mMapIntent, MAP_REQUESTING_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case PICTURE_REQUEST:

                Uri imageUri = Objects.requireNonNull(data).getData();

                Bitmap photo = null;

                if (imageUri != null) {
                    try {
                        photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                    photo = (Bitmap) Objects.requireNonNull(Objects.requireNonNull(data).getExtras()).get("data");}
                if(photo != null) {

                    createEventFragment.displayImage(photo);
                }

                break;
            case MAP_REQUESTING_RESULT:

                if (resultCode == Activity.RESULT_OK) {

                    String address = data.getStringExtra(MapActivity.INTENT_RESULT_ADDRESS);
                    Bundle allDataBundle = data.getBundleExtra(MapFragment.ARG_ALL_DATA_BUNDLE);

                    Log.d("address", "onActivityResult: address in the result: " + address +
                    " - Participants: " + allDataBundle.getString(CreateEventFragment.BUNDLE_PARTICIPANTS));

                    if (!address.isEmpty() && allDataBundle != null) {

                        // Pass the address to the fragment and restart the CreateEventFragment
                        getSupportFragmentManager().beginTransaction().replace(R.id.event_frame, CreateEventFragment.newInstance(address, allDataBundle)).commit();
                    }
                }

                break;

        }
//        if(requestCode == PICTURE_REQUEST) {
//
//            Uri imageUri = Objects.requireNonNull(data).getData();
//
//            Bitmap photo = null;
//
//            if (imageUri != null) {
//                try {
//                    photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } else {
//
//                photo = (Bitmap) Objects.requireNonNull(Objects.requireNonNull(data).getExtras()).get("data");}
//            if(photo != null) {
//
//                createEventFragment.displayImage(photo);
//            }
//        }
    }
}
