package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.activities.InterestsActivity;
import com.example.prajwalramamurthy.letschill_finalproject.activities.MainActivity;
import com.example.prajwalramamurthy.letschill_finalproject.activities.SignInUpActivity;
import com.example.prajwalramamurthy.letschill_finalproject.utility.FormValidation;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class SignInFragment extends Fragment implements View.OnClickListener {

    // Variables
    private FirebaseAuth mAuth;
    private ProgressBar mProgressBar;
    private CheckBox mCheckBox_rememberMe;
    private TextView mTextView_signUp, mTextView_forgotPassword;
    private EditText mEditText_email, mEditText_password;
    private Button mButton_signIn;
    private SignInFragmentInterface mSignInFragmentInterface;
    private CallbackManager callbackManager;
    private ArrayList<EditText> mAllEditTexts = new ArrayList<>();

    public interface SignInFragmentInterface {
        void swapToSignUp();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code

                        Toast.makeText(getContext(), "Success! You are successfully logged in!", Toast.LENGTH_SHORT).show();

                        Intent navigationIntent = new Intent(getContext(), InterestsActivity.class);
                        startActivity(navigationIntent);


                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }


    public static SignInFragment newInstance() {

        Bundle args = new Bundle();

        SignInFragment fragment = new SignInFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signin, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Verify if the interface is an instance of this context
        if (context instanceof SignInFragmentInterface) {
            mSignInFragmentInterface = (SignInFragmentInterface)context;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() != null && getContext() != null) {

            // Find Views
            mCheckBox_rememberMe = getView().findViewById(R.id.checkBox_rememberme);
            mTextView_signUp = getView().findViewById(R.id.textView_noAccount);
            mTextView_forgotPassword = getView().findViewById(R.id.textView_forgotPw);
            mButton_signIn = getView().findViewById(R.id.button_signin);
            mEditText_email = getView().findViewById(R.id.editText_email);
            mEditText_password = getView().findViewById(R.id.editText_password);
            mProgressBar = getView().findViewById(R.id.progressBar_signin);

            // Hide progress bar at start
            mProgressBar.setVisibility(View.INVISIBLE);

            // Set onClick listeners
            mTextView_signUp.setOnClickListener(this);
            mTextView_forgotPassword.setOnClickListener(this);
            mButton_signIn.setOnClickListener(this);

            // Set the check box font family to Helvetica Neue. It does not change in the xml.
            mCheckBox_rememberMe.setTypeface(ResourcesCompat.getFont(getContext(), R.font.helvetica_neue));

            // Get firebase auth instance
            mAuth = FirebaseAuth.getInstance();

            // Add all edit texts to an array list so that we can clear them easily
            mAllEditTexts.add(mEditText_email);
            mAllEditTexts.add(mEditText_password);
        }
    }

    private void userSignIn() {

        if (!mEditText_email.getText().toString().isEmpty() && !mEditText_password.getText().toString().isEmpty()) {

            String mEmail = mEditText_email.getText().toString();
            String mPassword = mEditText_password.getText().toString();

            // Display progress bar
            mProgressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    // Hide progress bar
                    mProgressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {

                        // TODO: User will be redirected to the "MainActiviy". Showing toast for testing.
                        if (getContext() != null) {

                            Toast.makeText(getContext(), "Successfully signed in!", Toast.LENGTH_SHORT).show();

                            // Clear the text fields
                            FormValidation.clearEditTexts(mAllEditTexts);

                        }

                    } else {

                        if (getContext() != null) {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.textView_noAccount:

                // Sign up button: Tell SignInUpActivity to replace fragments
                mSignInFragmentInterface.swapToSignUp();

                break;
            case R.id.textView_forgotPw:
                // TODO: Forgot password button
                break;
            case R.id.button_signin:
                userSignIn();
                break;

        }
    }
}
