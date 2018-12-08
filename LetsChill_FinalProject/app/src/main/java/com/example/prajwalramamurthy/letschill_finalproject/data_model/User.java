package com.example.prajwalramamurthy.letschill_finalproject.data_model;

public class User {

    // Member variables
    private String mUsername;
    private String mEmail;
    private String mProfilePhoto;

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
    public User() { }

    // Getters
    public String getUsername() {
        return mUsername;
    }

    public String getEmail() {
        return mEmail;
    }
}
