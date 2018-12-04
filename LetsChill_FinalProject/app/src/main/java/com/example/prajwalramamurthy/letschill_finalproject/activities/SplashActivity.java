package com.example.prajwalramamurthy.letschill_finalproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.prajwalramamurthy.letschill_finalproject.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        // Delays the activity for a second before opening the SignInUpActivity class
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mSignInIntent = new Intent(SplashActivity.this, SignInUpActivity.class);
                startActivity(mSignInIntent);
                finish();

                // TODO: Setting delay to 1 second for testing purposes. Change it back to 2 seconds for delivery.
            }
        }, 1000);
    }
}
