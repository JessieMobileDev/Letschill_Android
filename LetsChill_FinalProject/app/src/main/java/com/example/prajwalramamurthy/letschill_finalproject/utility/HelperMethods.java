package com.example.prajwalramamurthy.letschill_finalproject.utility;

import android.content.Context;
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
}
