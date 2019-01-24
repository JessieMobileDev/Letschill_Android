package com.example.prajwalramamurthy.letschill_finalproject.utility;

import android.content.Context;
import android.util.Log;

import com.example.prajwalramamurthy.letschill_finalproject.activities.MainActivity;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.EventLocalStorage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class LocalStorageHandler {

    // Variables
    private static String FILE_STORAGE = "FILE_STORAGE";

    // Constants
    private static final String FILE_EVENTS_LOCAL_STORAGE = "FILE_EVENTS_LOCAL_STORAGE";
    private static final String FILE_LAST_KNOWN_LOCATION = "FILE_LAST_KNOWN_LOCATION";
    private static final String TAG = "localStorage";

    private static void saveEventsToLocalFile(ArrayList<EventLocalStorage> mAllEvents, Context context, String typeOfList) {

        switch (typeOfList) {

            case "allEvents":
                FILE_STORAGE = "FILE_ALL_EVENTS_STORAGE";
                break;
            case "joined":
                FILE_STORAGE = "FILE_JOINED_EVENTS_STORAGE";
                break;
            case "hosting":
                FILE_STORAGE = "FILE_HOSTING_EVENTS_STORAGE";
                break;
        }

        try {
            FileOutputStream fos = context.openFileOutput(FILE_STORAGE, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(mAllEvents);
            oos.close();

            Log.d(TAG, "saveObjectToFile: File SAVED.");

        } catch(Exception e) {

            e.printStackTrace();
            Log.d(TAG, "saveObjectToFile: File NOT saved.");
        }
    }

    private static ArrayList<EventLocalStorage> loadEventsFromLocalFile(Context context, String typeOfList) {

        switch (typeOfList) {

            case "allEvents":
                FILE_STORAGE = "FILE_ALL_EVENTS_STORAGE";
                break;
            case "joined":
                FILE_STORAGE = "FILE_JOINED_EVENTS_STORAGE";
                break;
            case "hosting":
                FILE_STORAGE = "FILE_HOSTING_EVENTS_STORAGE";
                break;
        }

        // Variables
        ArrayList<EventLocalStorage> mRetrievedEvents = new ArrayList<>();

        try {
            FileInputStream fis = context.openFileInput(FILE_STORAGE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            mRetrievedEvents = (ArrayList<EventLocalStorage>) ois.readObject();
            ois.close();

            Log.d(TAG, "saveObjectToFile: File LOADED.");

        } catch(Exception e) {

            e.printStackTrace();
            Log.d(TAG, "getStoredFiles: File NOT loaded.");
        }

        return  mRetrievedEvents;
    }

    public static void convertFromEventToEventLocalStorageAndSave(ArrayList<Event> mAllEvents, Context context, String typeOfList) {

        // Variables
        ArrayList<EventLocalStorage> mEvents = new ArrayList<>();

        for (Event event: mAllEvents) {

            mEvents.add(new EventLocalStorage(event.getmEventId(), event.getmEventName(), event.getmEventLocation(),
                    event.getmEventDate(), event.getmEventTimeStart(), event.getmEventTimeFinish(), event.getmDescription(),
                    event.getmParticipants(), event.getmCategory(), event.getmHost(), event.ismIsRecurringEvent(),
                    event.ismPublicOrPrivate(), event.getmUrl(), event.ismIsDeleted(), event.getmLatitude(), event.getmLongitude(),
                    event.getmJoinedPeople(), event.getmHost_uid(), event.getmJoinedPeopleIds()));
        }

        // Save the list locally
        LocalStorageHandler.saveEventsToLocalFile(mEvents, context, typeOfList);
    }

    public static ArrayList<Event> loadFromFileAndConvertEventLocalStorageToEvent(Context context, String typeOfList) {

        ArrayList<EventLocalStorage> mRetrievedEvents = LocalStorageHandler.loadEventsFromLocalFile(context, typeOfList);
        ArrayList<Event> mAllEvents = new ArrayList<>();

        for (EventLocalStorage event: mRetrievedEvents) {

            mAllEvents.add(new Event(event.getmEventId(), event.getmEventName(), event.getmEventLocation(),
                    event.getmEventDate(), event.getmEventTimeStart(), event.getmEventTimeFinish(), event.getmDescription(),
                    event.getmParticipants(), event.getmCategory(), event.getmHost(), event.ismIsRecurringEvent(),
                    event.ismPublicOrPrivate(), event.getmUrl(), event.ismIsDeleted(), event.getmLatitude(), event.getmLongitude(),
                    event.getmJoinedPeople(), event.getmHost_uid(), event.getmJoinedPeopleIds()));
        }

        Log.d(TAG, "getStoredFiles: File LOADED.");

        return mAllEvents;
    }

    public static void saveUserLastKnownLocation(double mLatitude, double mLongitude, Context context) {

        // Variables
        ArrayList<Double> mLastKnownLocation = new ArrayList<>();
        mLastKnownLocation.add(mLatitude);
        mLastKnownLocation.add(mLongitude);

        try {
            FileOutputStream fos = context.openFileOutput(FILE_LAST_KNOWN_LOCATION, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(mLastKnownLocation);
            oos.close();

            Log.d(TAG, "saveObjectToFile: Location SAVED.");

        } catch(Exception e) {

            e.printStackTrace();
            Log.d(TAG, "saveObjectToFile: Location NOT saved.");
        }
    }

    public static ArrayList<Double> loadUserLastKnownLocation(Context context) {

        // Variables
        ArrayList<Double> mLastKnownLocation = new ArrayList<>();

        try {
            FileInputStream fis = context.openFileInput(FILE_LAST_KNOWN_LOCATION);
            ObjectInputStream ois = new ObjectInputStream(fis);
            mLastKnownLocation = (ArrayList<Double>) ois.readObject();
            ois.close();

            Log.d(TAG, "getStoredFiles: Location LOADED.");

        } catch(Exception e) {

            e.printStackTrace();
            Log.d(TAG, "getStoredFiles: Location NOT loaded.");
        }

        return  mLastKnownLocation;
    }
}
