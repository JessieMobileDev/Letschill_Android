package com.example.prajwalramamurthy.letschill_finalproject.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.ResultReceiver;
import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.User;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.ProfileFragment;
import com.example.prajwalramamurthy.letschill_finalproject.utility.ConnectionHandler;
import com.example.prajwalramamurthy.letschill_finalproject.utility.ImageDownloadHandler;
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
    private User mUser;
    private final Handler mHandler = new Handler();

    // Constants
    public static final String INTENT_LOGGED_USER_OBJECT = "INTENT_LOGGED_USER_OBJECT";
    public static final String INTENT_IMAGE_TYPE = "INTENT_IMAGE_TYPE";
    public static final String INTENT_IMAGE_URL = "INTENT_IMAGE_URL";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Profile");

        mUser = getIntent().getParcelableExtra(DetailsEventActivity.ARGS_USER_OBJECT);


        if (mUser != null) {

            // Instantiate the profile fragment and pass the user object to it
            getSupportFragmentManager().beginTransaction().replace(R.id.profile_frame,
                    ProfileFragment.newInstance(mUser, null)).commitAllowingStateLoss();

        } else {

            // Grab the logged user info and pass into the fragment
            requestLoggedUserInfo();
        }
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

                    if (ConnectionHandler.isConnected(ProfileActivity.this)) {

                        if (loggedUser.getProfilePhoto().contains("https")) {

                            // Download the profile image (0 for facebook)
                            Intent mFetchImage = new Intent(ProfileActivity.this, ImageDownloadHandler.class);
                            mFetchImage.putExtra(ImageDownloadHandler.EXTRA_RESULT_RECEIVER, new ImageDownloaderDataReceiver());
                            mFetchImage.putExtra(INTENT_IMAGE_TYPE, 0);
                            mFetchImage.putExtra(INTENT_IMAGE_URL, loggedUser.getProfilePhoto());
                            mFetchImage.putExtra(INTENT_LOGGED_USER_OBJECT, loggedUser);
                            startService(mFetchImage);
                        } else {

                            // Instantiate the fragment
                            getSupportFragmentManager().beginTransaction().replace(R.id.profile_frame,
                                    ProfileFragment.newInstance(loggedUser, null)).commitAllowingStateLoss();
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void openProfileEditActivity(User user, Bitmap fbImage) {

        // Open the "EditProfileActivity"
        Intent mEditProfileIntent = new Intent(this, EditProfileActivity.class);
        mEditProfileIntent.putExtra(INTENT_LOGGED_USER_OBJECT, user);
        mEditProfileIntent.putExtra(ProfileFragment.ARG_PROFILE_IMAGE, fbImage);
        startActivity(mEditProfileIntent);

//        // Close this activity
//        finish();
    }

    public class ImageDownloaderDataReceiver extends ResultReceiver {

        ImageDownloaderDataReceiver() {
            super(mHandler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            if (resultData != null) {

                User loggedUser = resultData.getParcelable(INTENT_LOGGED_USER_OBJECT);
                Bitmap image = resultData.getParcelable(ImageDownloadHandler.EXTRA_BITMAP);

                if (loggedUser != null && image != null) {

                    // Instantiate the fragment
                    getSupportFragmentManager().beginTransaction().replace(R.id.profile_frame,
                            ProfileFragment.newInstance(loggedUser, image)).commitAllowingStateLoss();
                }
            }
        }
    }
}
