package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.content.Context;
import android.content.Intent;
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
import com.example.prajwalramamurthy.letschill_finalproject.utility.ConnectionHandler;
import com.example.prajwalramamurthy.letschill_finalproject.utility.FormValidation;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SignUpFragment extends Fragment implements View.OnClickListener {

    // Variables
    private FirebaseAuth mAuth;
    private EditText mEditText_username, mEditText_email, mEditText_password, mEditText_repeatPassword;
    private Button mButton_signUp;
    private LoginButton mButton_facebook;
    private ProgressBar mProgressBar;
    private String mUsername, mEmail, mPassword, mRepeatPassword;
    private ArrayList<EditText> mAllEditTexts = new ArrayList<>();
    private SignUpFragmentInterface mSignUpFragmentInterface;

    public interface SignUpFragmentInterface {
        void moveToInterestsFromSignUp();
    }

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
    public void onAttach(Context context) {
        super.onAttach(context);

        // Verify if the interface is an instance of this context
        if (context instanceof SignUpFragment.SignUpFragmentInterface) {
            mSignUpFragmentInterface = (SignUpFragment.SignUpFragmentInterface)context;
        }
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

            // Add all edit texts to an array list to clear them easily upon signing up
            mAllEditTexts.add(mEditText_username);
            mAllEditTexts.add(mEditText_email);
            mAllEditTexts.add(mEditText_password);
            mAllEditTexts.add(mEditText_repeatPassword);

        }

    }

    private void validateUserInputs() {

        // Collect data from the edit texts
        mUsername = mEditText_username.getText().toString();
        mEmail = mEditText_email.getText().toString();
        mPassword = mEditText_password.getText().toString();
        mRepeatPassword = mEditText_repeatPassword.getText().toString();

        // Check if they are all empty
        if (!mUsername.isEmpty() && !mEmail.isEmpty() && !mPassword.isEmpty() && !mRepeatPassword.isEmpty()) {

            // Check if email is valid
            final boolean mIsEmailValid = FormValidation.isEmailValid(mEmail);

            // Check if password is valid
            final boolean mIsPasswordValid = FormValidation.isPasswordValid(mPassword, mEditText_password);

            // Check if passwords match
            final boolean mDoPasswordsMatch = FormValidation.doPasswordsMatch(mPassword, mRepeatPassword, mEditText_repeatPassword);

            // If email and passwords are valid, then save the user to the database
            if (mIsEmailValid && mIsPasswordValid && mDoPasswordsMatch) {

                signUp();
            }

        } else {

            for (EditText mEditText:mAllEditTexts) {

                // Check if edit texts are empty
                if (mEditText.getText().toString().isEmpty()) {
                    mEditText.setError("Don't leave this field empty");
                }
            }
        }
    }

    private void signUp() {

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

                                    // Clear the edit texts
                                    FormValidation.clearEditTexts(mAllEditTexts);

                                    // Perform an intent to open the "InterestsActivity"
                                    mSignUpFragmentInterface.moveToInterestsFromSignUp();
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

                if (getContext() != null) {

                    if (ConnectionHandler.isConnected(getContext())) {

                        // Validate user inputs
                        validateUserInputs();

                    } else {

                        // Display an alert
                        FormValidation.displayAlert(R.string.alert_title_noInternet, R.string.alert_content_noInternet, R.string.alert_buttonOk_noInternet, getContext());
                    }
                }

                break;
        }
    }
}
