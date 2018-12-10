package com.example.prajwalramamurthy.letschill_finalproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.ForgotPasswordFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.SignInFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.SignUpFragment;

public class SignInUpActivity extends AppCompatActivity implements SignInFragment.SignInFragmentInterface, ForgotPasswordFragment.ForgotPasswordInterface, SignUpFragment.SignUpFragmentInterface {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        getSupportFragmentManager().beginTransaction().add(R.id.main_frame, SignInFragment.newInstance()).commit();
    }

    @Override
    public void swapToSignUp() {

        // Swap the SignInFragment for the SignUpFragment
        getSupportFragmentManager().beginTransaction().addToBackStack(null).add(R.id.main_frame, SignUpFragment.newInstance()).commit();
    }

    @Override
    public void swapToResetPasswordFragment() {

        // Swap the SignInFragment for the ForgotPasswordFragment
        getSupportFragmentManager().beginTransaction().addToBackStack(null).add(R.id.main_frame, ForgotPasswordFragment.newInstance()).commit();
    }

    @Override
    public void moveToInterestsFromSignIn() {

        // Move to the "InterestsActivity"
        Intent mInterestsIntent = new Intent(SignInUpActivity.this, CreateEventActivity.class);
        startActivity(mInterestsIntent);
    }

    @Override
    public void moveToMainScreen() {

        // Move to the "CreateEventActivity"
        Intent mCreateEventIntent = new Intent(SignInUpActivity.this, CreateEventActivity.class);
        startActivity(mCreateEventIntent);
    }

    @Override
    public void swapToSignIn() {

        // Swap the ForgotPasswordFragment for the SignInFragment
        getSupportFragmentManager().beginTransaction().addToBackStack(null).add(R.id.main_frame, SignInFragment.newInstance()).commit();
    }

    @Override
    public void moveToInterestsFromSignUp() {

        // TODO swapped activity from interest to create
        // Move to the "InterestsActivity"
        Intent mInterestsIntent = new Intent(SignInUpActivity.this, CreateEventActivity.class);
        startActivity(mInterestsIntent);
    }
}
