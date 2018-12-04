package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpFragment extends Fragment implements View.OnClickListener {

    // Variables
    private FirebaseAuth mAuth;
    private EditText mEditText_username, mEditText_email, mEditText_password, mEditText_repeatPassword;
    private Button mButton_signUp;
    private ProgressBar mProgressBar;
    private String mUsername, mEmail, mPassword, mRepeatPassword;

    public static SignUpFragment newInstance() {

        Bundle args = new Bundle();

        SignUpFragment fragment = new SignUpFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() != null && getContext() != null) {

            // Find views
            mEditText_username = getView().findViewById(R.id.editText_username_signup);
            mEditText_email = getView().findViewById(R.id.editText_email_signup);
            mEditText_password = getView().findViewById(R.id.editText_password_signup);
            mEditText_repeatPassword = getView().findViewById(R.id.editText_repeatPassword_signup);
            mButton_signUp = getView().findViewById(R.id.button_signup);
            mProgressBar = getView().findViewById(R.id.progressBar_signup);

            // Hide progress bar at start
            mProgressBar.setVisibility(View.INVISIBLE);

            // Set on click listeners
            mButton_signUp.setOnClickListener(this);

            // Get firebase auth instance
            mAuth = FirebaseAuth.getInstance();

        }

    }

    private void validateUserInputs() {

        if (mEditText_username.getText().toString().isEmpty()) {
            mEditText_username.setError("Don't leave this field empty");
        } else {
            mUsername = mEditText_username.getText().toString();
        }
        if (mEditText_email.getText().toString().isEmpty()) {
            mEditText_email.setError("Don't leave this field empty");
        } else {
            mEmail = mEditText_email.getText().toString();
        }
        if (mEditText_password.getText().toString().isEmpty()) {
            mEditText_password.setError("Don't leave this field empty");
        }
        if (mEditText_repeatPassword.getText().toString().isEmpty()) {
            mEditText_repeatPassword.setError("Don't leave this field empty");
        }

        // If both passwords fields are not empty
        if (!mEditText_password.getText().toString().isEmpty() &&
                !mEditText_repeatPassword.getText().toString().isEmpty()) {

            // If both passwords match
            if (mEditText_repeatPassword.getText().toString().equals(mEditText_password.getText().toString())) {

                mPassword = mEditText_password.getText().toString();

            } else {
                // If repeated password does not match, display error message
                mEditText_repeatPassword.setError("Password does not match");
            }
        }

        if (mUsername != null && mEmail != null && mPassword != null) {

            // Show the progress bar
            mProgressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        // Store the additional data in the Realtime database
                        User user = new User(mUsername, mEmail);

                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                // Hide progress bar if successful
                                mProgressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {

                                    Toast.makeText(getContext(), "Account created successfully", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else {

                        // Hide progress bar if not successful
                        mProgressBar.setVisibility(View.GONE);

                        if (getContext() != null) {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.button_signup:

                // Validate user inputs
                validateUserInputs();
                break;
        }
    }
}
