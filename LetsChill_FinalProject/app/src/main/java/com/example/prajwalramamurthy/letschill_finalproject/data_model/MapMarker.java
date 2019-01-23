package com.example.prajwalramamurthy.letschill_finalproject.data_model;

public class MapMarker {

    // Member variables
    private String mAddress;
    private double mLatitude;
    private double mLongitude;
    private String mEventId = "0";

    // Constructor
    public MapMarker(String mEventId, String mAddress, double mLatitude, double mLongitude) {
        this.mEventId = mEventId;
        this.mAddress = mAddress;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
    }

    // Getters
    public String getmAddress() {
        return mAddress;
    }

    public double getmLatitude() {
        return mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    // Setters

    public void setmMarkerId(String mMarkerId) {
        this.mEventId = mMarkerId;
    }
}
