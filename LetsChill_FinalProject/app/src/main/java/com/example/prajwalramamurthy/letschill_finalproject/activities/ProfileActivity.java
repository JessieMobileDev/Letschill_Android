package com.example.prajwalramamurthy.letschill_finalproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.User;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.EditProfileFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity implements ProfileFragment.ProfileInterface {

    // Variables
    private FirebaseUser mFirebaseUser;
    private String mUserUID;
    private DatabaseReference mDBReference;

    // Constants
    public static final String INTENT_LOGGED_USER_OBJECT = "INTENT_LOGGED_USER_OBJECT";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Profile");

        // Grab the logged user info and pass into the fragment
        requestLoggedUserInfo();
    }

    private void requestLoggedUserInfo() {

        // Retrieve the user UID
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserUID = mFirebaseUser.getUid();

        // Get the reference to the "users" node in the database
        mDBReference = FirebaseDatabase.getInstance().getReference("Users").child(mUserUID);

        mDBReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User loggedUser = dataSnapshot.getValue(User.class);

                if (loggedUser != null) {

                    // Instantiate the fragment
                    getSupportFragmentManager().beginTransaction().add(R.id.profile_frame, ProfileFragment.newInstance(loggedUser)).commit();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void openProfileEditActivity(User user) {

        // Open the "EditProfileActivity"
        Intent mEditProfileIntent = new Intent(this, EditProfileActivity.class);
        mEditProfileIntent.putExtra(INTENT_LOGGED_USER_OBJECT, user);
        startActivity(mEditProfileIntent);

        // Close this activity
        finish();
    }
}
