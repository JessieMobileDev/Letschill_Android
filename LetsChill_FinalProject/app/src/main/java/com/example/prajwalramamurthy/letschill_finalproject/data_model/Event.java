package com.example.prajwalramamurthy.letschill_finalproject.data_model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Event implements Parcelable
{
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
    private ArrayList<String> mJoinedPeopleIds;

    // Constructor
    public Event(String mEventId, String mEventName, String mEventLocation, String mEventDate, String mEventTimeStart,
                 String mEventTimeFinish, String mDescription, String mParticipants,
                 String mCategory, String mHost, boolean mIsRecurringEvent, boolean mPublicOrPrivate, String mUrl,
                 boolean mIsDeleted, double mLatitude, double mLongitude, int mJoinedPeople, ArrayList<String> mJoinedPeopleIds) {

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
        this.mJoinedPeopleIds = mJoinedPeopleIds;
    }

    // Empty Constructor
    public Event(String eventId, String movies_at_lincoln, String s, String s1, String s2,
                 String s3, String s4, String movies, boolean b, boolean b1, double lat, double lon,
                 int mJoinedPeople, ArrayList<String> mJoinedPeopleIds) {
    }

    public Event() {

    }

    protected Event(Parcel in) {
        mEventId = in.readString();
        mEventName = in.readString();
        mEventLocation = in.readString();
        mEventDate = in.readString();
        mEventTimeStart = in.readString();
        mEventTimeFinish = in.readString();
        mDescription = in.readString();
        mParticipants = in.readString();
        mCategory = in.readString();
        mUrl = in.readString();
        mHost = in.readString();
        mIsRecurringEvent = in.readByte() != 0;
        mPublicOrPrivate = in.readByte() != 0;
        mIsDeleted = in.readByte() != 0;
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
        mJoinedPeople = in.readInt();
        mJoinedPeopleIds = in.readArrayList(String.class.getClassLoader());
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mEventId);
        dest.writeString(mEventName);
        dest.writeString(mEventLocation);
        dest.writeString(mEventDate);
        dest.writeString(mEventTimeStart);
        dest.writeString(mEventTimeFinish);
        dest.writeString(mDescription);
        dest.writeString(mParticipants);
        dest.writeString(mCategory);
        dest.writeString(mUrl);
        dest.writeString(mHost);
        dest.writeByte((byte) (mIsRecurringEvent ? 1 : 0));
        dest.writeByte((byte) (mPublicOrPrivate ? 1 : 0));
        dest.writeByte((byte) (mIsDeleted ? 1 : 0));
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
        dest.writeInt(mJoinedPeople);
        dest.writeList(mJoinedPeopleIds);
    }
}
