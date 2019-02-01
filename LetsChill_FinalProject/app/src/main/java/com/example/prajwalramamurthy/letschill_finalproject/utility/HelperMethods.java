package com.example.prajwalramamurthy.letschill_finalproject.utility;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HelperMethods {

    public static String getCurrentMonth(String eventDate) {

        Date date;

        String dateString = "Month";

        try {

            // Convert the string date to a date variable and extract the month out of it
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
            date = mSimpleDateFormat.parse(eventDate);
            dateString = String.valueOf(date.getMonth());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateString;
    }

    public static boolean isUserVerified(User mRetrievedUser, Context context) {

        boolean isVerified = false;

        // Checks if the user is already verified
        if (mRetrievedUser != null && context != null) {

            // If the user is verified, then display a pop up message
            if (!mRetrievedUser.getEmail().equals("N/A") && !mRetrievedUser.getPhone().equals("N/A")
                    && !mRetrievedUser.getFacebookEmail().equals("N/A")) {

                isVerified = true;
            }
        }

        return isVerified;
    }

    public static String getCityName(Context context, double lat, double lng) {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        List<Address> addresses = new ArrayList<>();

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
        } catch (Exception e) {

            e.printStackTrace();
        }

        return addresses.get(0).getAddressLine(0);
    }

    public static double getDistanceBetweenTwoPlaces(double lat1, double lat2, double lon1,
                                  double lon2) {

        final int R = 6371; // Radius of the earth
        double valueMiles = 0.00062137;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = (R * c * 1000) * valueMiles; // convert to meters and to miles

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }
}
