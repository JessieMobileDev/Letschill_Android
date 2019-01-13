package com.example.prajwalramamurthy.letschill_finalproject.data_model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class User implements Parcelable {

    // Member variables
    private String username;
    private String email;
    private String mProfilePhoto;
    private ArrayList<String> interests;

    // Constructor
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    // Constructor
    public User(String username, String email, String mProfilePhoto) {
        this.username = username;
        this.email = email;
        this.mProfilePhoto = mProfilePhoto;
    }


    // Blank Constructor
    public User() {

    }

    protected User(Parcel in) {
        username = in.readString();
        email = in.readString();
        mProfilePhoto = in.readString();
        interests = in.createStringArrayList();
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
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getmProfilePhoto() {
        return mProfilePhoto;
    }

    public ArrayList<String> getInterests() {
        return interests;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(mProfilePhoto);
        dest.writeStringList(interests);
    }
}
