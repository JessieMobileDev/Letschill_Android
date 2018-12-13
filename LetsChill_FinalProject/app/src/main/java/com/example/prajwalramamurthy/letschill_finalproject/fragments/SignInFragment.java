package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
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
import com.example.prajwalramamurthy.letschill_finalproject.utility.ConnectionHandler;
import com.example.prajwalramamurthy.letschill_finalproject.utility.FormValidation;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
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
    private LoginButton mButton_facebook;
    private SignInFragmentInterface mSignInFragmentInterface;
    private CallbackManager callbackManager;
    private final ArrayList<EditText> mAllEditTexts = new ArrayList<>();
    private SharedPreferences mPrefs;
    private FacebookLoginInterface facebookLogin;

    // Constants
    private static final String TAG = "test";
    public static final String PREFS_REMEMBER_ME = "PREFS_REMEMBER_ME";

    public interface SignInFragmentInterface {
        void swapToSignUp();
        void swapToResetPasswordFragment();
        void moveToInterestsFromSignIn();
        void moveToMainActivityFromSignIn();
    }

    public interface FacebookLoginInterface {
        void facebookLogin(LoginButton button);
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

        if (context instanceof FacebookLoginInterface) {
            facebookLogin = (FacebookLoginInterface) context;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() != null && getContext() != null) {

            // Find Views
            mCheckBox_rememberMe = getView().findViewById(R.id.checkBox_create_recurring);
            mTextView_signUp = getView().findViewById(R.id.textView_noAccount);
            mTextView_forgotPassword = getView().findViewById(R.id.textView_forgotPw);
            mButton_signIn = getView().findViewById(R.id.button_signin);
            mButton_facebook = getView().findViewById(R.id.login_button);
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
            mCheckBox_rememberMe.setTypeface(ResourcesCompat.getFont(getContext(), R.font.roboto_regular));

            // Get firebase auth instance
            mAuth = FirebaseAuth.getInstance();

            // Add all edit texts to an array list so that we can clear them easily
            mAllEditTexts.add(mEditText_email);
            mAllEditTexts.add(mEditText_password);

            // The method below handles the facebook click
            if (ConnectionHandler.isConnected(getContext())) {

                signInWithFacebook();

            } else {

                // Disable facebook button and show toast
                mButton_facebook.setClickable(false);
                Toast.makeText(getContext(), R.string.toast_noInternet, Toast.LENGTH_SHORT).show();
            }

            // Instantiate the SharedPreferences
            mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

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

                        if (getContext() != null) {

                            Toast.makeText(getContext(), "Successfully signed in!", Toast.LENGTH_SHORT).show();

                            // Clear the text fields
                            FormValidation.clearEditTexts(mAllEditTexts);

                            // Save the user's UID to SharedPreferences
                            mPrefs.edit().putString(SignUpFragment.PREFS_USER_UID, task.getResult().getUser().getUid()).apply();

                            // If the "remember me" checkbox is checked, save to defaults
                            if (mCheckBox_rememberMe.isChecked()) {

                                mPrefs.edit().putBoolean(PREFS_REMEMBER_ME, true).apply();
                            } else {

                                mPrefs.edit().putBoolean(PREFS_REMEMBER_ME, false).apply();
                            }

                            // Move to the "MainActivity"
                            mSignInFragmentInterface.moveToMainActivityFromSignIn();

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



    private void signInWithFacebook() {

        // instantiate firebase auth
        mAuth = FirebaseAuth.getInstance();

        facebookLogin.facebookLogin(mButton_facebook);


    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.textView_noAccount:

                // Sign up button: Tell SignInUpActivity to replace fragments
                mSignInFragmentInterface.swapToSignUp();

                break;
            case R.id.textView_forgotPw:

                // Reset button: Tell SignInUpActivity to replace fragments
                mSignInFragmentInterface.swapToResetPasswordFragment();

                break;
            case R.id.button_signin:

                userSignIn();
                break;

        }
    }
}
