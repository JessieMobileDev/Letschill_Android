package com.example.prajwalramamurthy.letschill_finalproject.data_model;

import java.io.Serializable;
import java.util.ArrayList;

public class EventLocalStorage implements Serializable {

    // Member variables
    private String mEventId;
    private String mEventName;
    private String mEventLocation;
    private String mEventDate;
    private String mEventTimeStart;
    private String mEventTimeFinish;
    private String mDescription;
    private String mParticipants;
    private String mCategory;
    private String mHost;
    private String mUrl;
    private boolean mIsRecurringEvent;
    private boolean mPublicOrPrivate;
    private boolean mIsDeleted;
    private double mLatitude;
    private double mLongitude;
    private int mJoinedPeople;
    private String mHost_uid;
    private ArrayList<String> mJoinedPeopleIds;

    // Constructor
    public EventLocalStorage(String mEventId, String mEventName, String mEventLocation, String mEventDate, String mEventTimeStart,
                 String mEventTimeFinish, String mDescription, String mParticipants,
                 String mCategory, String mHost, boolean mIsRecurringEvent, boolean mPublicOrPrivate, String mUrl,
                 boolean mIsDeleted, double mLatitude, double mLongitude, int mJoinedPeople, String mHost_uid, ArrayList<String> mJoinedPeopleIds) {

        this.mEventId = mEventId;
        this.mEventName = mEventName;
        this.mEventLocation = mEventLocation;
        this.mEventDate = mEventDate;
        this.mEventTimeStart = mEventTimeStart;
        this.mEventTimeFinish = mEventTimeFinish;
        this.mDescription = mDescription;
        this.mParticipants = mParticipants;
        this.mCategory = mCategory;
        this.mHost = mHost;
        this.mIsRecurringEvent = mIsRecurringEvent;
        this.mPublicOrPrivate = mPublicOrPrivate;
        this.mUrl = mUrl;
        this.mIsDeleted = mIsDeleted;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mJoinedPeople = mJoinedPeople;
        this.mHost_uid = mHost_uid;
        this.mJoinedPeopleIds = mJoinedPeopleIds;
    }

    // Empty Constructor
    public EventLocalStorage(String eventId, String movies_at_lincoln, String s, String s1, String s2,
                 String s3, String s4, String movies, boolean b, boolean b1, double lat, double lon,
                 int mJoinedPeople, ArrayList<String> mJoinedPeopleIds, String mHost_uid) {

    }

    public EventLocalStorage() {

    }

    // Getters
    public String getmEventName() {
        return mEventName;
    }

    public String getmEventLocation() {
        return mEventLocation;
    }

    public String getmEventDate() {
        return mEventDate;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getmEventTimeStart() {
        return mEventTimeStart;
    }

    public String getmEventTimeFinish() {
        return mEventTimeFinish;
    }

    public String getmDescription() {
        return mDescription;
    }

    public String getmParticipants() {
        return mParticipants;
    }

    public String getmCategory() {
        return mCategory;
    }

    public boolean ismIsRecurringEvent() {
        return mIsRecurringEvent;
    }

    public boolean ismPublicOrPrivate() {
        return mPublicOrPrivate;
    }

    public String getmHost() {
        return mHost;
    }

    public String getmHost_uid() {
        return mHost_uid;
    }

    public String getmEventId() {
        return mEventId;
    }

    public boolean ismIsDeleted() {
        return mIsDeleted;
    }

    public double getmLatitude() {
        return mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public int getmJoinedPeople() {
        return mJoinedPeople;
    }

    public void setmJoinedPeople(int mJoinedPeople) {
        this.mJoinedPeople = mJoinedPeople;
    }

    public ArrayList<String> getmJoinedPeopleIds() {
        return mJoinedPeopleIds;
    }

    public void setmJoinedPeopleIds(ArrayList<String> mJoinedPeopleIds) {
        this.mJoinedPeopleIds = mJoinedPeopleIds;
    }
}
