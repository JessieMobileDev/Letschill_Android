package com.example.prajwalramamurthy.letschill_finalproject.data_model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class User implements Parcelable {

    // Member variables
    private String userID;
    private String profilePhoto;
    private String fullName;
    private String username;
    private String email;
    private String phone;
    private String device_token;

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    private String facebookEmail;
    private ArrayList<String> interests = new ArrayList<>();
//    private ArrayList<String> joinedEvents = new ArrayList<>();

    // Constructor
//    public User(String userID, String fullName, String username, String email, String phone,
//                String facebookEmail, String profilePhoto) {
//        this(userID, fullName, username, email, phone, facebookEmail, profilePhoto);
//    }

    // Constructor
    public User(String userID, String fullName, String username, String email, String phone,
                String facebookEmail, String profilePhoto, String device_token) {
        this.userID = userID;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.facebookEmail = facebookEmail;
        this.profilePhoto = profilePhoto;
        this.device_token = device_token;
    }

    // Constructor
    public User(String userID, String username, String email, String profilePhoto, String device_token) {
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.profilePhoto = profilePhoto;
        this.device_token = device_token;
    }

    // Constructor
    public User(String userID, String profilePhoto, String fullName, String username, String email, String phone,
                String device_token, String facebookEmail, ArrayList<String> interests) {
        this.userID = userID;
        this.profilePhoto = profilePhoto;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.device_token = device_token;
        this.facebookEmail = facebookEmail;
        this.interests = interests;
//        this.joinedEvents = joinedEvents;
    }


    // Blank Constructor
    public User() {


    }

    protected User(Parcel in) {
        userID = in.readString();
        profilePhoto = in.readString();
        fullName = in.readString();
        username = in.readString();
        email = in.readString();
        phone = in.readString();
        facebookEmail = in.readString();
        interests = in.createStringArrayList();
//        joinedEvents = in.createStringArrayList();
        device_token = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userID);
        dest.writeString(profilePhoto);
        dest.writeString(fullName);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(facebookEmail);
        dest.writeStringList(interests);
        dest.writeString(device_token);
//        dest.writeStringList(joinedEvents);
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

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<String> getInterests() {
        return interests;
    }

    public String getUserID() {
        return userID;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getFacebookEmail() {
        return facebookEmail;
    }

    //    public ArrayList<String> getJoinedEvents() {
//        return joinedEvents;
//    }




}
