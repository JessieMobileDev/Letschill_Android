package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.activities.ProfileActivity;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.User;
import com.example.prajwalramamurthy.letschill_finalproject.utility.FormValidation;
import com.example.prajwalramamurthy.letschill_finalproject.utility.ImageDownloadHandler;
import com.example.prajwalramamurthy.letschill_finalproject.utility.MenuIntentHandler;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    // Variables
    private TextView mTextView_fullName, mTextView_username, mTextView_email, mTextView_interests, mTextView_verified;
    private Button mButton_edit;
    private ConstraintLayout mContraintLayout_verifiedTag;
    private ImageView mImageView_profilePicture;
    private ProfileInterface mProfileInterface;
    private User mRetrievedUser;
    private DatabaseReference mDBReference;
    private FirebaseUser mFirebaseUser;
    private String isVerified = "false";

    // Constants
    private static final String ARG_LOGGED_USER = "ARG_LOGGED_USER";
    public static final String ARG_PROFILE_IMAGE = "ARG_PROFILE_IMAGE";
    private static final String TAG = "tag123";

    public interface ProfileInterface {

        void openProfileEditActivity(User user, Bitmap fbImage);
        void openVerificationActivity(User currentLoggedUser);
    }

    public static ProfileFragment newInstance(User loggedUser, Bitmap profileImage) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_LOGGED_USER, loggedUser);
        args.putParcelable(ARG_PROFILE_IMAGE, profileImage);

        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ProfileInterface) {

            mProfileInterface = (ProfileInterface)context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();
        inflater.inflate(R.menu.menu_settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (getContext() != null && getActivity() != null) {

            MenuIntentHandler.getMenuIntents(item, getContext(), getActivity(), MenuIntentHandler.PROFILE_ACTIVITY);
        }

        return false;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        if (getView() != null && getArguments() != null) {

            // Find views
            mTextView_fullName = getView().findViewById(R.id.textView_fullNameSubTitle);
            mTextView_username = getView().findViewById(R.id.textView_usernameSubTitle);
            mTextView_email = getView().findViewById(R.id.textView_emailAddressSubTitle);
            mTextView_interests = getView().findViewById(R.id.textView_interestsSubTitle);
            mTextView_verified = getView().findViewById(R.id.textView_profile_verified);
            mContraintLayout_verifiedTag = getView().findViewById(R.id.constraint_profile_notVerified);
            mButton_edit = getView().findViewById(R.id.button_profileEdit);
            mImageView_profilePicture = getView().findViewById(R.id.imageView_profilePicture);

            // Assign click listeners
            mButton_edit.setOnClickListener(this);
//            mContraintLayout_verifiedTag.setOnClickListener(this);
            mTextView_verified.setOnClickListener(this);

            // Retrieve the user passed into this fragment
            mRetrievedUser = getArguments().getParcelable(ARG_LOGGED_USER);

            // Set the color of the verification bar
            isUserVerified(false);

            if (mRetrievedUser != null) {

                // Apply the retrieved object data to where it's needed on the UI
                mTextView_fullName.setText(mRetrievedUser.getFullName());
                mTextView_username.setText(mRetrievedUser.getUsername());
                mTextView_email.setText(mRetrievedUser.getEmail());

                // Check if photo contains facebook in it, if so, download the image to display
                if (mRetrievedUser.getProfilePhoto().contains("https")) {

                    // Retrieve the image passed to this fragment
                    Bitmap profileImage = getArguments().getParcelable(ARG_PROFILE_IMAGE);

                    if (profileImage != null) {

                        mImageView_profilePicture.setImageBitmap(profileImage);
                    }
//                    mImageView_profilePicture.setImageBitmap(ImageDownloadHandler
//                            .downloadFacebookImageToBitmap(mRetrievedUser.getProfilePhoto()));
                } else {

                    if (mRetrievedUser.getProfilePhoto().contains("IDImages")) {

                        ImageDownloadHandler.downloadFirebaseImageAndSetBitmap
                                (mRetrievedUser.getProfilePhoto(), mImageView_profilePicture);
                    }
                }

                // Concatenate all the interests into one string, breaking line right after each one
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < mRetrievedUser.getInterests().size(); i++) {

                    if (i == mRetrievedUser.getInterests().size() - 1) {

                        sb.append(mRetrievedUser.getInterests().get(i));
                    } else {

                        sb.append(mRetrievedUser.getInterests().get(i)).append("\n");
                    }
                }

                mTextView_interests.setText(sb.toString());

                if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(mRetrievedUser.getUserID())) {

                    mButton_edit.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.button_profileEdit:

                if (mRetrievedUser != null) {

                    // Open the "EditProfile" and pass the current event object to it
                    mProfileInterface.openProfileEditActivity(mRetrievedUser, (Bitmap) getArguments().getParcelable(ARG_PROFILE_IMAGE));
                }

                break;
            case R.id.textView_profile_verified:

                // Checks if the user is already verified
                isUserVerified(true);

                break;
        }
    }

    private void isUserVerified(boolean wasTappedOnIt) {

        // Note: Right now, checks for phone number, email and facebook email.
        // If they exist in the database, user is verified

        // Checks if the user is already verified
        if (mRetrievedUser != null && getContext() != null) {

            // If the user is verified, then display a pop up message
            if (!mRetrievedUser.getEmail().equals("N/A") && !mRetrievedUser.getPhone().equals("N/A")
                    && !mRetrievedUser.getFacebookEmail().equals("N/A")) {

                Log.d("a0a0a0a0", "isUserVerified: user is verified");

                // Save to the firebase
                FirebaseDatabase.getInstance().getReference("Users").child(mRetrievedUser.getUserID())
                        .child("isVerified").setValue(true);

                if (wasTappedOnIt) {

                    // Display an alert dialog letting the user know about their current state
                    FormValidation.displayAlert(R.string.alert_verified_title, R.string.alert_verified_message,
                            R.string.alert_verified_button, getContext());
                }

                // Change the color of the constraint background to green and change text on textView
                mContraintLayout_verifiedTag.setBackgroundColor(getResources().getColor(R.color.verified));
                mTextView_verified.setText("verified");

                // Takes the user to the verification activity if they are not yet verified
//                mProfileInterface.openVerificationActivity(mRetrievedUser);
            } else {

                // Change the isVerified value in the database if any information is missing
                FirebaseDatabase.getInstance().getReference("Users").child(mRetrievedUser.getUserID())
                        .child("isVerified").setValue(false);

                ArrayList<String> missingData = new ArrayList<>();

                if (mRetrievedUser.getPhone().equals("N/A")) {
                    missingData.add("phone number");
                }
                if (mRetrievedUser.getFacebookEmail().equals("N/A")) {
                    missingData.add("Facebook email");
                }

                if (missingData.size() == 1) {

                    if (wasTappedOnIt) {

                        // Display an alert
                        FormValidation.displayAlertNoId("Missing info", "Please update your "
                                + missingData.get(0) + " in your profile!", "OK", getContext());
                    }
                } else if (missingData.size() == 2) {

                    if (wasTappedOnIt) {
                        // Display an alert
                        FormValidation.displayAlertNoId("Missing info", "Please update your "
                                        + missingData.get(0) + " and your " + missingData.get(1) + " in your profile!",
                                "OK", getContext());
                    }
                }

                missingData.clear();

                // Change the color of the verified tag to red and change text label
                mContraintLayout_verifiedTag.setBackgroundColor(getResources().getColor(R.color.not_verified));
                mTextView_verified.setText("not verified");

            }
        }
    }
}
