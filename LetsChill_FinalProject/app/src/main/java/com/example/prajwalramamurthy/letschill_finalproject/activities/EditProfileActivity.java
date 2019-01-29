package com.example.prajwalramamurthy.letschill_finalproject.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.User;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.EditProfileFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.ProfileFragment;

import java.util.ArrayList;

public class EditProfileActivity extends AppCompatActivity implements EditProfileFragment.EditProfileInterface {

    // Variables
    private User mRetrievedUser;

    // Constants
    public static final String INTENT_LOGGED_USER = "INTENT_LOGGED_USER";
    public static final String INTENT_COMES_FROM_EDIT_PROFILE = "INTENT_COMES_FROM_EDIT_PROFILE";
    public static final String INTENT_ALL_TYPED_DATA = "INTENT_ALL_TYPED_DATA";
    public static final String INTENT_INTERESTS = "INTENT_INTERESTS";

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
            Bundle allTypedData = mReceivedIntent.getBundleExtra(InterestsActivity.INTENT_ALL_TYPED_DATA);
            ArrayList<String> newInterests = mReceivedIntent.getStringArrayListExtra(InterestsActivity.INTENT_NEW_INTERESTS);
            Bitmap fbImage = mReceivedIntent.getParcelableExtra(ProfileFragment.ARG_PROFILE_IMAGE);

            if (mRetrievedUser != null) {

//                if (fbImage != null) {
//
//                    // Start the "EditProfileFragment" and pass the object to it
//                    getSupportFragmentManager().beginTransaction().add(R.id.edit_profile_frame,
//                            EditProfileFragment.newInstance(mRetrievedUser, null, null, fbImage)).commit();
//                } else {

                    // Start the "EditProfileFragment" and pass the object to it
                    getSupportFragmentManager().beginTransaction().add(R.id.edit_profile_frame,
                            EditProfileFragment.newInstance(mRetrievedUser, null, null)).commit();
//                }

            }

            if (allTypedData != null && newInterests != null) {

//                if (fbImage != null) {
//
//                    getSupportFragmentManager().beginTransaction().add(R.id.edit_profile_frame,
//                            EditProfileFragment.newInstance(mRetrievedUser, allTypedData, newInterests, fbImage)).commit();
//                } else {

                    getSupportFragmentManager().beginTransaction().add(R.id.edit_profile_frame,
                            EditProfileFragment.newInstance(mRetrievedUser, allTypedData, newInterests)).commit();
//                }

            }
        }
    }

    @Override
    public void closeEditProfileActivity() {

        // Close this activity
        finish();
    }

    @Override
    public void openInterestsActivity(User loggedUser, int openedFromProfile, Bundle allTypedData,
                                      ArrayList<String> interests) {

        // Open the InterestsActivity and pass the data to it
        Intent mInterestsIntent = new Intent(this, InterestsActivity.class);
        mInterestsIntent.putExtra(INTENT_LOGGED_USER, loggedUser);
        mInterestsIntent.putExtra(INTENT_COMES_FROM_EDIT_PROFILE, openedFromProfile);
        mInterestsIntent.putExtra(INTENT_ALL_TYPED_DATA, allTypedData);
        mInterestsIntent.putExtra(INTENT_INTERESTS, interests);
        startActivity(mInterestsIntent);

        // Close this activity
        finish();

    }
}
