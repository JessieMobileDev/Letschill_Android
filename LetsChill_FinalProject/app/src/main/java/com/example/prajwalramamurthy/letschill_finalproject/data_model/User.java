package com.example.prajwalramamurthy.letschill_finalproject.data_model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class User implements Parcelable {

    // Member variables
    private String mUsername;
    private String mEmail;
    private String mProfilePhoto;
    private ArrayList<String> mInterests;

    // Constructor
    public User(String mUsername, String mEmail) {
        this.mUsername = mUsername;
        this.mEmail = mEmail;
    }

    // Constructor
    public User(String mUsername, String mEmail, String mProfilePhoto) {
        this.mUsername = mUsername;
        this.mEmail = mEmail;
        this.mProfilePhoto = mProfilePhoto;
    }


    // Blank Constructor
    public User() {

    }

    protected User(Parcel in) {
        mUsername = in.readString();
        mEmail = in.readString();
        mProfilePhoto = in.readString();
        mInterests = in.createStringArrayList();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    // Getters
    public String getUsername() {
        return mUsername;
    }

    public String getEmail() {
        return mEmail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUsername);
        dest.writeString(mEmail);
        dest.writeString(mProfilePhoto);
        dest.writeStringList(mInterests);
    }
}
