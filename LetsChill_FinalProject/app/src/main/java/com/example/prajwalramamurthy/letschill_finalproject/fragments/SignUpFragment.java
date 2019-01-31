package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.User;
import com.example.prajwalramamurthy.letschill_finalproject.utility.ConnectionHandler;
import com.example.prajwalramamurthy.letschill_finalproject.utility.FormValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class SignUpFragment extends Fragment implements View.OnClickListener {

    // Variables
    private FirebaseAuth mAuth;
    private EditText mEditText_username, mEditText_email, mEditText_password, mEditText_repeatPassword;
    private Button mButton_signUp;
    private TextView mTextView_first_length, mTextView_second_letter, mTextView_third_number, mTextView_fourth_special;
    private ImageView mImageView_first_length, mImageView_second_letter, mImageView_third_number, mImageView_fourth_special;
    private ProgressBar mProgressBar;
    private ConstraintLayout mPassword_requirements_background;
    private String mUsername, mEmail, mPassword, mRepeatPassword;
    private final ArrayList<EditText> mAllEditTexts = new ArrayList<>();
    private SignUpFragmentInterface mSignUpFragmentInterface;
    private SharedPreferences mPrefs;

    // Constants
    public static final String PREFS_USER_UID = "PREFS_USER_UID";
    public static final String PREFS_USER_NAME = "PREFS_USER_NAME";

//    private void hideKeyboard() {
//
//        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(mEditText_password.getWindowToken(), 0);
//    }
//
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//
//        hideKeyboard();
//
//        return true;
//    }

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
            mTextView_first_length = getView().findViewById(R.id.textView_first_character);
            mTextView_second_letter = getView().findViewById(R.id.textView_second_letter);
            mTextView_third_number = getView().findViewById(R.id.textView_third_number);
            mTextView_fourth_special = getView().findViewById(R.id.textView_fourth_special);
            mImageView_first_length = getView().findViewById(R.id.imageView_first_character);
            mImageView_second_letter = getView().findViewById(R.id.imageView_second_letter);
            mImageView_third_number = getView().findViewById(R.id.imageView_third_number);
            mImageView_fourth_special = getView().findViewById(R.id.imageView_fourth_special);
            mPassword_requirements_background = getView().findViewById(R.id.background_pw_hints);

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

            // Instantiate the SharedPreferences
            mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

            // Add listener to the password edit text
            mEditText_password.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    // If text up to this point contains 8 characters long, display green checkmark and green text
                    if (FormValidation.isPassword8CharsLong(mEditText_password.getText().toString())) {
                        mImageView_first_length.setImageResource(R.drawable.ic_check_green_24dp);
                        mTextView_first_length.setTextColor(getResources().getColor(R.color.checkmark));
                    } else {
                        mImageView_first_length.setImageResource(R.drawable.ic_block_red_24dp);
                        mTextView_first_length.setTextColor(getResources().getColor(R.color.block));
                    }

                    // If text up to this point contains 1 upper cased letter, display green checkmark and green text
                    if (FormValidation.doesPasswordHaveUppercasedLetter(mEditText_password.getText().toString())) {
                        mImageView_second_letter.setImageResource(R.drawable.ic_check_green_24dp);
                        mTextView_second_letter.setTextColor(getResources().getColor(R.color.checkmark));
                    } else {
                        mImageView_second_letter.setImageResource(R.drawable.ic_block_red_24dp);
                        mTextView_second_letter.setTextColor(getResources().getColor(R.color.block));
                    }

                    // If the text up to this point contains 1 number, display green checkmark and green text
                    if (FormValidation.doesPasswordContainNumbers(mEditText_password.getText().toString())) {
                        mImageView_third_number.setImageResource(R.drawable.ic_check_green_24dp);
                        mTextView_third_number.setTextColor(getResources().getColor(R.color.checkmark));
                    } else {
                        mImageView_third_number.setImageResource(R.drawable.ic_block_red_24dp);
                        mTextView_third_number.setTextColor(getResources().getColor(R.color.block));
                    }

                    // If the text up to this point does not contain symbols, display green checkmar and green text
                    if (FormValidation.doesPasswordContainSymbols(mEditText_password.getText().toString())) {
                        mImageView_fourth_special.setImageResource(R.drawable.ic_check_green_24dp);
                        mTextView_fourth_special.setTextColor(getResources().getColor(R.color.checkmark));
                    } else {
                        mImageView_fourth_special.setImageResource(R.drawable.ic_block_red_24dp);
                        mTextView_fourth_special.setTextColor(getResources().getColor(R.color.block));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

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
                        String mUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        String token = FirebaseInstanceId.getInstance().getToken();

                        if (!mUserUID.isEmpty()) {

                            User user = new User(mUserUID, "N/A", mUsername, mEmail, "N/A",
                                    "N/A", "N/A",token, null, false);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(mUserUID).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    // Hide progress bar if successful
                                    mProgressBar.setVisibility(View.GONE);

                                    if (task.isSuccessful()) {

                                        Toast.makeText(getContext(), "Account created successfully", Toast.LENGTH_SHORT).show();

//                                    // Save the username for later use within the application
//                                    mPrefs.edit().putString(PREFS_USER_NAME, mUsername).apply();

                                        // Save the user uid for later use within the application
                                        mPrefs.edit().putString(PREFS_USER_UID, FirebaseDatabase.getInstance().getReference("Users")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getKey()).apply();

                                        // Clear the edit texts
                                        FormValidation.clearEditTexts(mAllEditTexts);

                                        // Perform an intent to open the "InterestsActivity"
                                        mSignUpFragmentInterface.moveToInterestsFromSignUp();
                                    }
                                }
                            });
                        }

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

