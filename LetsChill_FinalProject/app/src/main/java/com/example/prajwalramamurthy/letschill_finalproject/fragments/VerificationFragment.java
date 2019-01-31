package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.EditText;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.User;
import com.example.prajwalramamurthy.letschill_finalproject.utility.FormValidation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VerificationFragment extends Fragment {

    // Variables
    private EditText mEditText_phone, mEditText_code;
    private Button mButton_action;
    private String isVerified = "false";
//    private String

    // Constants
    private static final String ARG_USER_OBJECT = "ARG_USER_OBJECT";

    public static VerificationFragment newInstance(User user) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_USER_OBJECT, user);

        VerificationFragment fragment = new VerificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() != null && getArguments() != null && getContext() != null) {

            // Find views
            mEditText_phone = getView().findViewById(R.id.editText_verification_phone);
            mEditText_code = getView().findViewById(R.id.editText_verification_code);
            mButton_action = getView().findViewById(R.id.button_verification_verify);

            // Get the current logged in user
            User user = getArguments().getParcelable(ARG_USER_OBJECT);

            if (user != null) {

                // Find out if the user is already verified
                FirebaseDatabase.getInstance().getReference("Users").child(user.getUserID())
                        .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot userNode : dataSnapshot.getChildren()) {

                            if (userNode.getKey().equals("isVerified")) {

                                isVerified = userNode.getValue(String.class);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                // If the user is verified, then display a pop up message
                if (isVerified.equals("true")) {

                    FormValidation.displayAlert(R.string.alert_verified_title, R.string.alert_verified_message,
                            R.string.alert_verified_button, getContext());
                } else if (isVerified.equals("false")) {

                    // If the user is not verified, check if they have a phone number saved already
                    FormValidation.displayAlert(R.string.alert_verified_title, R.string.alert_verified_message,
                            R.string.alert_verified_button, getContext());
                }
            }
        }

    }
}
