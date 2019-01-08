package com.example.prajwalramamurthy.letschill_finalproject.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.CreateEventFragment;

import java.io.IOException;
import java.util.Objects;

public class EventActivity extends AppCompatActivity implements CreateEventFragment.CreateEventFragmentInterface {

    private CreateEventFragment createEventFragment;
    private static final int PICTURE_REQUEST = 0x0101;



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

    @Override
    public void imageUploader() {

        Intent mGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(mGalleryIntent, "Select Image from Gallery"), PICTURE_REQUEST);

    }

    @Override
    public void openMapActivity() {

        // Open the MapActivity
        Intent mMapIntent = new Intent(this, MapActivity.class);
        startActivity(mMapIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICTURE_REQUEST)
        {
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
        }
    }
}
