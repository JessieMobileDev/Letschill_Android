package com.example.prajwalramamurthy.letschill_finalproject.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    // Variables
    private FirebaseUser mFirebaseUser;
    private String mUserUID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Profile");

        // Grab the logged user info and pass into the fragment
        requestLoggedUserInfo();

        // Instantiate the fragment
        getSupportFragmentManager().beginTransaction().add(R.id.profile_frame, ProfileFragment.newInstance()).commit();
    }

    private void requestLoggedUserInfo() {

        // Retrieve the user UID
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserUID = mFirebaseUser.getUid();

    }
}
