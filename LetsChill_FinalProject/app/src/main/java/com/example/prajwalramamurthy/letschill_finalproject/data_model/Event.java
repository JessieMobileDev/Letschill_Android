package com.example.prajwalramamurthy.letschill_finalproject.data_model;

public class Event
{

    // Member variables
    //private String mEventID;
    private String mEventName;
    private String mEventLocation;
    private String mEventDate;
    private String mEventTime;
    private String mDescription;
    private String mParticipants;
    private String mCategory;
    private boolean mIsRecurringEvent;
    private boolean mPublicOrPrivate;

    // Constructor
    public Event(String mEventName, String mEventLocation, String mEventDate,
                 String mEventTime, String mDescription, String mParticipants, String mCategory,
                 boolean mIsRecurringEvent, boolean mPublicOrPrivate)
    {
        //this.mEventID = mEventID;
        this.mEventName = mEventName;
        this.mEventLocation = mEventLocation;
        this.mEventDate = mEventDate;
        this.mEventTime = mEventTime;
        this.mDescription = mDescription;
        this.mParticipants = mParticipants;
        this.mCategory = mCategory;
        this.mIsRecurringEvent = mIsRecurringEvent;
        this.mPublicOrPrivate = mPublicOrPrivate;
    }

    // Empty Constructor
    public Event() {
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

    public String getmEventTime() {
        return mEventTime;
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
}
