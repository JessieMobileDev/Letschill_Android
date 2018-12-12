package com.example.prajwalramamurthy.letschill_finalproject.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.User;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.ForgotPasswordFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.SignInFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.SignUpFragment;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

import static com.example.prajwalramamurthy.letschill_finalproject.fragments.SignInFragment.PREFS_REMEMBER_ME;

public class SignInUpActivity extends AppCompatActivity implements SignInFragment.SignInFragmentInterface,
        ForgotPasswordFragment.ForgotPasswordInterface, SignUpFragment.SignUpFragmentInterface, SignInFragment.FacebookLoginInterface {

    private static final String TAG = "SignInUp" ;
    // Variables
    private SharedPreferences mPrefs;
    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // Get firebase auth instance
        mAuth = FirebaseAuth.getInstance();


        // Instantiate the SharedPreferences
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (mPrefs.getBoolean(PREFS_REMEMBER_ME, false)) {

            Intent mMainIntent = new Intent(SignInUpActivity.this, MainActivity.class);
            startActivity(mMainIntent);

        } else {

            getSupportFragmentManager().beginTransaction().add(R.id.signin_frame, SignInFragment.newInstance()).commit();

        }
    }

    @Override
    public void swapToSignUp() {

        // Swap the SignInFragment for the SignUpFragment
        getSupportFragmentManager().beginTransaction().addToBackStack(null).add(R.id.signin_frame, SignUpFragment.newInstance()).commit();
    }

    @Override
    public void swapToResetPasswordFragment() {

        // Swap the SignInFragment for the ForgotPasswordFragment
        getSupportFragmentManager().beginTransaction().addToBackStack(null).add(R.id.signin_frame, ForgotPasswordFragment.newInstance()).commit();
    }

    @Override
    public void moveToInterestsFromSignIn() {

        // Move to the "InterestsActivity"
        Intent mInterestsIntent = new Intent(SignInUpActivity.this, InterestsActivity.class);

        startActivity(mInterestsIntent);
    }

    @Override
    public void moveToMainActivityFromSignIn() {

        // Move to the "MainActivity3"
        Intent mCreateEventIntent = new Intent(SignInUpActivity.this, MainActivity.class);
        startActivity(mCreateEventIntent);
    }

    @Override
    public void swapToSignIn() {

        // Swap the ForgotPasswordFragment for the SignInFragment
        getSupportFragmentManager().beginTransaction().addToBackStack(null).add(R.id.signin_frame, SignInFragment.newInstance()).commit();
    }

    @Override
    public void moveToInterestsFromSignUp() {

        // Move to the "InterestsActivity"
        Intent mInterestsIntent = new Intent(SignInUpActivity.this, InterestsActivity.class);
        startActivity(mInterestsIntent);
    }

    @Override
    public void facebookLogin(LoginButton button) {
        callbackManager = CallbackManager.Factory.create();

        button.setReadPermissions(Arrays.asList("email", "public_profile"));

        button.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code

                        handleFacebookAccessToken(loginResult.getAccessToken());

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


    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    Toast.makeText(SignInUpActivity.this, "Success! You are successfully logged in!", Toast.LENGTH_SHORT).show();


                    final FirebaseUser user = mAuth.getCurrentUser();

                    User createdUser = new User(user.getDisplayName(),user.getEmail(), user.getPhotoUrl().toString());

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(user.getUid())
                            .setValue(createdUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                            // Hide progress bar if successful
//                                mProgressBar.setVisibility(View.GONE);

//
//                                if (task.isSuccessful()) {
//
//                                    Toast.makeText(getContext(), "Account created successfully", Toast.LENGTH_SHORT).show();
//
//                                    // Clear the edit texts
//                                    FormValidation.clearEditTexts(mAllEditTexts);
//                                }
                        }
                    });

                    //Toast.makeText(SignInUpActivity.this, "Account saved to db!", Toast.LENGTH_SHORT).show();

                    // Move to Interests screen
                    moveToInterestsFromSignIn();

                }
                else
                {
                    Toast.makeText(SignInUpActivity.this, R.string.facebook_toast_fail,
                            Toast.LENGTH_SHORT).show();
                    moveToInterestsFromSignIn();
                }
            }
        });

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}

