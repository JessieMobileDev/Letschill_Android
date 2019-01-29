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
import com.example.prajwalramamurthy.letschill_finalproject.utility.FormValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordFragment extends Fragment implements View.OnClickListener {

    // Variables
    private Button mButton_send;
    private EditText mEditText_email;
    private ProgressBar mProgressBar;
    private FirebaseAuth mAuth;
    private ForgotPasswordInterface mForgotPasswordInterface;

    public interface ForgotPasswordInterface {
        void swapToSignIn();
    }

    public static ForgotPasswordFragment newInstance() {

        Bundle args = new Bundle();

        ForgotPasswordFragment fragment = new ForgotPasswordFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // verify is the interface is an instance of this context
        if (context instanceof ForgotPasswordInterface) {
            mForgotPasswordInterface = (ForgotPasswordInterface)context;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() != null) {

            // Find views
            mButton_send = getView().findViewById(R.id.button_send);
            mEditText_email = getView().findViewById(R.id.editText_email_forgotPw);
            mProgressBar = getView().findViewById(R.id.progressBar_forgotPw);

            // Assign click listeners
            mButton_send.setOnClickListener(this);

            // Get firebase auth instance
            mAuth = FirebaseAuth.getInstance();

            // Disable progress bar at start
            mProgressBar.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.button_send:

                // Send an email to the user's email address with instructions for resetting the password
                sendResetPasswordEmail();
                break;
        }
    }

    private void sendResetPasswordEmail() {

        if (mEditText_email.getText().toString().isEmpty()) {

            // Alert the user that the edit text is empty
            mEditText_email.setError("Do not leave this field empty");
        } else {

            if (FormValidation.isEmailValid(mEditText_email.getText().toString()) && getContext() != null) {

                // Display the progress bar
                mProgressBar.setVisibility(View.VISIBLE);

                // Send the reset password email
                mAuth.sendPasswordResetEmail(mEditText_email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            // If the email is registered in the database, then an email will be sent
                            Toast.makeText(getContext(), R.string.toast_instructions, Toast.LENGTH_SHORT).show();

                            // Hide the progress bar
                            mProgressBar.setVisibility(View.GONE);

                            // Clear the edit text
                            mEditText_email.setText("");

                            // Swap back to sign in screen
                            mForgotPasswordInterface.swapToSignIn();

                        } else {

                            // The email does not exist in the database. Display the exception error message
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {

                // Alert the user that the inserted email is not valid
                mEditText_email.setError("This is not a valid email");
            }
        }


    }
}
