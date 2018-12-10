package com.example.prajwalramamurthy.letschill_finalproject.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.ForgotPasswordFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.SignInFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.SignUpFragment;

import static com.example.prajwalramamurthy.letschill_finalproject.fragments.SignInFragment.PREFS_REMEMBER_ME;

public class SignInUpActivity extends AppCompatActivity implements SignInFragment.SignInFragmentInterface, ForgotPasswordFragment.ForgotPasswordInterface, SignUpFragment.SignUpFragmentInterface {

    // Variables
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // Instantiate the SharedPreferences
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

//        getSupportFragmentManager().beginTransaction().add(R.id.signin_frame, SignInFragment.newInstance()).commit();

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
}
