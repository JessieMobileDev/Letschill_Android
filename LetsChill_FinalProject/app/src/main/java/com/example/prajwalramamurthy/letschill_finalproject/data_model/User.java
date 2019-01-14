package com.example.prajwalramamurthy.letschill_finalproject.data_model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class User implements Parcelable {

    // Member variables
    private String profilePhoto;
    private String fullName;
    private String username;
    private String email;
    private String phone;
    private String facebookEmail;
    private ArrayList<String> interests = new ArrayList<>();
//    private ArrayList<String> joinedEvents = new ArrayList<>();

    // Constructor
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    // Constructor
    public User(String username, String email, String profilePhoto) {
        this.username = username;
        this.email = email;
        this.profilePhoto = profilePhoto;
    }

    // Constructor
    public User(String profilePhoto, String fullName, String username, String email, String phone,
                String facebookEmail, ArrayList<String> interests) {

        this.profilePhoto = profilePhoto;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.facebookEmail = facebookEmail;
        this.interests = interests;
//        this.joinedEvents = joinedEvents;
    }


    // Blank Constructor
    public User() {

    }

    protected User(Parcel in) {
        profilePhoto = in.readString();
        fullName = in.readString();
        username = in.readString();
        email = in.readString();
        phone = in.readString();
        facebookEmail = in.readString();
        interests = in.createStringArrayList();
//        joinedEvents = in.createStringArrayList();
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
        return profilePhoto;
    }

    public ArrayList<String> getInterests() {
        return interests;
    }

//    public ArrayList<String> getJoinedEvents() {
//        return joinedEvents;
//    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(profilePhoto);
        dest.writeString(fullName);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(facebookEmail);
        dest.writeStringList(interests);
//        dest.writeStringList(joinedEvents);
    }
}
