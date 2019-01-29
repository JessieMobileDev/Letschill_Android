package com.example.prajwalramamurthy.letschill_finalproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.User;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.InterestsFragment;
import java.util.ArrayList;

public class InterestsActivity extends AppCompatActivity implements InterestsFragment.InterestsFragmentInterface {

    // Constants
    public static final String INTENT_ALL_TYPED_DATA = "INTENT_ALL_TYPED_DATA";
    public static final String INTENT_NEW_INTERESTS = "INTENT_NEW_INTERESTS";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);
        setTitle("Interests");

        Intent receivedIntent = getIntent();
        if (receivedIntent != null &&
                receivedIntent.getIntExtra(SignInUpActivity.INTENT_COMES_FROM_SIGN_UP, 3) == 0) {

            // If the received intent comes from the SignInUpActivity, do the following
            getFragmentManager().beginTransaction().replace(R.id.interests_frame, InterestsFragment.newInstance(null, 0, null)).commit();

        } else if (receivedIntent != null &&
                receivedIntent.getIntExtra(EditProfileActivity.INTENT_COMES_FROM_EDIT_PROFILE, 3) == 1) {

            // If the received intent comes from the EditProfileActivity, do the following
            User loggedUser = receivedIntent.getParcelableExtra(EditProfileActivity.INTENT_LOGGED_USER);
            int codeRequest = receivedIntent.getIntExtra(EditProfileActivity.INTENT_COMES_FROM_EDIT_PROFILE, 3);
            Bundle allTypedData = receivedIntent.getBundleExtra(EditProfileActivity.INTENT_ALL_TYPED_DATA);

            if (loggedUser != null && codeRequest != 3 && allTypedData != null) {

                getFragmentManager().beginTransaction().replace(R.id.interests_frame, InterestsFragment.newInstance(loggedUser,
                        1, allTypedData)).commit();
            }
        } else {

            getFragmentManager().beginTransaction().replace(R.id.interests_frame, InterestsFragment.newInstance(null,
                    3, null)).commit();
        }
    }

    @Override
    public void moveToMainActivityFromInterests() {

        // Move to "MainActivity"
        Intent mMainIntent = new Intent(InterestsActivity.this, MainActivity.class);
        startActivity(mMainIntent);

        // Close this activity
        finish();
    }

    @Override
    public void sendDataBackToEditProfileActivity(Bundle allTypedData, ArrayList<String> newInterests) {

        // Open EditProfileActivity again and pass data back to it
        Intent mNewEditProfileIntent = new Intent(this, EditProfileActivity.class);
        mNewEditProfileIntent.putExtra(INTENT_ALL_TYPED_DATA, allTypedData);
        mNewEditProfileIntent.putExtra(INTENT_NEW_INTERESTS, newInterests);
        startActivity(mNewEditProfileIntent);

        // Close this activity
        finish();

    }
}
