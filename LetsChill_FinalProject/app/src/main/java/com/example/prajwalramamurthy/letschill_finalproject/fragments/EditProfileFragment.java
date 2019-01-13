package com.example.prajwalramamurthy.letschill_finalproject.fragments;

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
import android.widget.EditText;
import android.widget.TextView;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.User;
import com.example.prajwalramamurthy.letschill_finalproject.utility.MenuIntentHandler;

public class EditProfileFragment extends Fragment {

    // Variables
    private EditText mEditText_fullName, mEditText_username, mEditText_email, mEditText_phone,
            mEditText_facebookEmail;
    private TextView mTextView_interests;
    private Button mButton_chooseInterests;
    private ConstraintLayout mContraintLayout_verified;

    // Constants
    private static final String ARG_LOGGED_USER = "ARG_LOGGED_USER";

    public static EditProfileFragment newInstance(User loggedUser) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_LOGGED_USER, loggedUser);

        EditProfileFragment fragment = new EditProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);


        inflater.inflate(R.menu.menu_edit_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (getContext() != null && getActivity() != null) {

            if (item.getItemId() == R.id.action_submit_edittedProfile) {

                saveProfileChangesToDatabase();

            } else if (item.getItemId() == R.id.action_settings) {

                MenuIntentHandler.getMenuIntents(item, getContext(), getActivity());
            }
        }

        return false;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        if (getView() != null && getArguments() != null) {

            // Get the views
            mEditText_fullName = getView().findViewById(R.id.editText_editProfile_fullName);
            mEditText_username = getView().findViewById(R.id.editText_editProfile_username);
            mEditText_email = getView().findViewById(R.id.editText_editProfile_email);
            mEditText_phone = getView().findViewById(R.id.editText_editProfile_phone);
            mEditText_facebookEmail = getView().findViewById(R.id.editText_editProfile_fb);
            mTextView_interests = getView().findViewById(R.id.textView_editProfile_interests);
            mButton_chooseInterests = getView().findViewById(R.id.button_editProfile_interests);
            mContraintLayout_verified = getView().findViewById(R.id.constraint_notVerified);

            // Collect the user object passed into this fragment
            User retrievedUser = getArguments().getParcelable(ARG_LOGGED_USER);

            if (retrievedUser != null) {

                // Assign the data back to the edit texts and text views where needed
                mEditText_username.setText(retrievedUser.getUsername());
                mEditText_email.setText(retrievedUser.getEmail());
            }
        }
    }

    private void saveProfileChangesToDatabase() {


    }
}
