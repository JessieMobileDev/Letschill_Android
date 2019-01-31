package com.example.prajwalramamurthy.letschill_finalproject.utility;

import android.support.annotation.NonNull;

import com.example.prajwalramamurthy.letschill_finalproject.data_model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
}
