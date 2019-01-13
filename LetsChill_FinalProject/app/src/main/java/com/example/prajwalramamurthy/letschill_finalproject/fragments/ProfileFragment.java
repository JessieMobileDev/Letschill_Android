package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.User;
import com.example.prajwalramamurthy.letschill_finalproject.utility.MenuIntentHandler;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    // Variables
    private TextView mTextView_fullName, mTextView_username, mTextView_email, mTextView_interests;
    private Button mButton_edit;
    private ConstraintLayout mContraintLayout_verifiedTag;
    private ProfileInterface mProfileInterface;
    private User mRetrievedUser;

    // Constants
    private static final String ARG_LOGGED_USER = "ARG_LOGGED_USER";

    public interface ProfileInterface {

        void openProfileEditActivity(User user);
    }

    public static ProfileFragment newInstance(User loggedUser) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_LOGGED_USER, loggedUser);

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

        inflater.inflate(R.menu.menu_settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (getContext() != null && getActivity() != null) {

            MenuIntentHandler.getMenuIntents(item, getContext(), getActivity());
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
            mContraintLayout_verifiedTag = getView().findViewById(R.id.constraint_profile_notVerified);
            mButton_edit = getView().findViewById(R.id.button_profileEdit);

            // Assign click listeners
            mButton_edit.setOnClickListener(this);
            mContraintLayout_verifiedTag.setOnClickListener(this);

            // Retrieve the user passed into this fragment
            mRetrievedUser = getArguments().getParcelable(ARG_LOGGED_USER);

            if (mRetrievedUser != null) {

                // Apply the retrieved object data to where it's needed on the UI
                mTextView_username.setText(mRetrievedUser.getUsername());
                mTextView_email.setText(mRetrievedUser.getEmail());
                mTextView_interests.setText(mRetrievedUser.getInterests().get(0));
            }
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.button_profileEdit:

                if (mRetrievedUser != null) {

                    // Open the "EditProfile" and pass the current event object to it
                    mProfileInterface.openProfileEditActivity(mRetrievedUser);
                }

                break;
            case R.id.constraint_profile_notVerified:

                // Takes the user to the verification activity

                break;
        }
    }
}
