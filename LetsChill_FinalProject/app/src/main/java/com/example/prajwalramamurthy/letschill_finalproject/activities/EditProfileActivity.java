package com.example.prajwalramamurthy.letschill_finalproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.User;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.EditProfileFragment;

public class EditProfileActivity extends AppCompatActivity {

    // Variables
    private User mRetrievedUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Edit Profile");
        setContentView(R.layout.activity_edit_profile);

        // Receive the intent from "ProfileActivity"
        Intent mReceivedIntent = getIntent();

        if (mReceivedIntent != null) {

            // Retrieve the User object that was passed in the intent
            mRetrievedUser = mReceivedIntent.getParcelableExtra(ProfileActivity.INTENT_LOGGED_USER_OBJECT);

            if (mRetrievedUser != null) {

                // Start the "EditProfileFragment" and pass the object to it
                getSupportFragmentManager().beginTransaction().add(R.id.edit_profile_frame,
                        EditProfileFragment.newInstance(mRetrievedUser)).commit();
            }
        }
    }
}
