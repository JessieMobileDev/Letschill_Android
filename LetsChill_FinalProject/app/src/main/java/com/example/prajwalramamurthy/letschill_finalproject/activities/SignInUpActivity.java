package com.example.prajwalramamurthy.letschill_finalproject.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.User;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.ForgotPasswordFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.SignInFragment;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.SignUpFragment;
import com.example.prajwalramamurthy.letschill_finalproject.utility.DatabaseEventIntentService;
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
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;

import static com.example.prajwalramamurthy.letschill_finalproject.fragments.SignInFragment.PREFS_REMEMBER_ME;

public class SignInUpActivity extends AppCompatActivity implements SignInFragment.SignInFragmentInterface,
        ForgotPasswordFragment.ForgotPasswordInterface, SignUpFragment.SignUpFragmentInterface, SignInFragment.FacebookLoginInterface {

    // Variables
    private SharedPreferences mPrefs;
    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;

    // Constants
    private static final int REQUEST_LOCATION_PERMISSION = 0x01101;
    private static final int REQUEST_WRITE_PERMISSION = 0x01101111;
    public static final String EXTRA_REQUESTER = "EXTRA_REQUESTER";
    public static final String INTENT_COMES_FROM_SIGN_UP = "INTENT_COMES_FROM_SIGN_UP";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        instantiateActivityWithPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // TODO: add request read permision and network permission
        if (requestCode == REQUEST_LOCATION_PERMISSION || requestCode == REQUEST_WRITE_PERMISSION) {

            instantiateActivityWithPermissions();

        }
    }

    private void instantiateActivityWithPermissions() {

        // Check permissions before doing anything
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            // Get Firebase auth instance
            mAuth = FirebaseAuth.getInstance();


            // Instantiate the SharedPreferences
            mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

            if (mPrefs.getBoolean(PREFS_REMEMBER_ME, false)) {

                Intent mMainIntent = new Intent(SignInUpActivity.this, MainActivity.class);
                startActivity(mMainIntent);
                finish();

            } else {

                getSupportFragmentManager().beginTransaction().add(R.id.signin_frame, SignInFragment.newInstance()).commit();

            }
        } else {

            // If the app does not have the permission, then request it
            ActivityCompat.requestPermissions(this, new String[]
                    { Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUEST_LOCATION_PERMISSION);

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

        // Close this activity
        finish();
    }

    @Override
    public void moveToInterestsFromSignIn() {

        // Move to the "InterestsActivity"
        Intent mInterestsIntent = new Intent(SignInUpActivity.this, InterestsActivity.class);
        startActivity(mInterestsIntent);

        // Close this activity
        finish();
    }

    @Override
    public void moveToMainActivityFromSignIn() {

//        requestUsernameUsingUid(1);
        // Move to the "MainActivity"
        Intent mMainActivityIntent = new Intent(SignInUpActivity.this, MainActivity.class);
        startActivity(mMainActivityIntent);

        // Close this activity
        finish();
    }

    @Override
    public void swapToSignIn() {

        // Swap the ForgotPasswordFragment for the SignInFragment
        getSupportFragmentManager().beginTransaction().addToBackStack(null).add(R.id.signin_frame, SignInFragment.newInstance()).commit();

        // Close this activity
        finish();
    }

    @Override
    public void moveToInterestsFromSignUp() {

        // Move to the "InterestsActivity"
        Intent mInterestsIntent = new Intent(SignInUpActivity.this, InterestsActivity.class);
        mInterestsIntent.putExtra(INTENT_COMES_FROM_SIGN_UP, 0);
        startActivity(mInterestsIntent);

        // Close this activity
        finish();
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

                    // user.getPhotoUrl().toString()
                    String facebookPicture = "N/A";
                    String facebookEmail = "N/A";

                    try {

                        facebookPicture = user.getPhotoUrl().toString();
                        facebookEmail = user.getEmail();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    String token = FirebaseInstanceId.getInstance().getToken();


                    User createdUser = new User(user.getUid(), user.getDisplayName(), user.getDisplayName(), "N/A",
                            "N/A", facebookEmail, facebookPicture,token);

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(user.getUid())
                            .setValue(createdUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                        }
                    });

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

