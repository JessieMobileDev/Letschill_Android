package com.example.prajwalramamurthy.letschill_finalproject.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.User;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.VerificationFragment;

public class VerificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        setTitle("Verification");

//        User user = getIntent().getParcelableExtra();

//        if (user != null) {
//
//            getSupportFragmentManager().beginTransaction().replace(R.id.verification_frame,
//                    VerificationFragment.newInstance(user)).commit();
//        }
    }
}
