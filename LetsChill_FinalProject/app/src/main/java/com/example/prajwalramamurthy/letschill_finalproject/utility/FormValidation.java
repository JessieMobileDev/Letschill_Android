package com.example.prajwalramamurthy.letschill_finalproject.utility;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class FormValidation {

    public static boolean isPasswordValid(String mPassword, EditText mEditText) {

        // Password:
        // -- Must be greater than 8 characters;
        // -- Must contain 1 uppercase letter;
        // -- Must contain 1 number;
        // -- Must not have special characters.

        Boolean isValid = true;

        // Conditions
        Pattern specialCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Pattern upperCasePatten = Pattern.compile("[A-Z ]");
        Pattern digitCasePatten = Pattern.compile("[0-9 ]");

        // Validation
        if (specialCharPatten.matcher(mPassword).find()) {
            mEditText.setError("Cannot contain special characters");
            isValid = false;
        }

        if (!upperCasePatten.matcher(mPassword).find()) {
            mEditText.setError("Must contain at least 1 uppercase letter");
            isValid = false;
        }

        if (mPassword.length() < 8) {
            mEditText.setError("Must contain more than 8 characters long");
            isValid = false;
        }

        if (!digitCasePatten.matcher(mPassword).find()) {
            mEditText.setError("Must contain at least one number");
            isValid = false;
        }

        return isValid;
    }

    public static boolean doesPasswordContainSymbols(String pw) {

        Boolean isValid = true;
        Pattern specialCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);

        if (specialCharPatten.matcher(pw).find()) {
            isValid = false;
        }

        return isValid;
    }

    public static boolean doesPasswordContainNumbers(String pw) {

        Boolean isValid = true;
        Pattern digitCasePatten = Pattern.compile("[0-9 ]");

        if (!digitCasePatten.matcher(pw).find()) {
            isValid = false;
        }

        return isValid;
    }

    public static boolean isPassword8CharsLong(String pw) {

        Boolean isValid = true;

        if (pw.length() < 8) {
            isValid = false;
        }

        return isValid;
    }

    public static boolean doesPasswordHaveUppercasedLetter(String pw) {

        Boolean isValid = true;
        Pattern upperCasePatten = Pattern.compile("[A-Z ]");

        if (!upperCasePatten.matcher(pw).find()) {
            isValid = false;
        }
        return isValid;
    }

    public static boolean isEmailValid(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    public static boolean doPasswordsMatch(String mPasswordOne, String mPasswordTwo, EditText mEditText) {

        boolean doPwMatch = false;
        if (mPasswordTwo.equals(mPasswordOne)) {
            doPwMatch = true;
        } else {

            mEditText.setError("Passwords do not match");
        }
        return doPwMatch;
    }

    public static void displayAlert(int title, int message, int button, Context context) {

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(context.getResources().getString(title));
        alert.setMessage(context.getResources().getString(message));
        alert.setPositiveButton(context.getResources().getString(button), null);
        alert.show();
    }

    public static void clearEditTexts(ArrayList<EditText> mAllEditTexts) {

        for (EditText mEditText:mAllEditTexts) {

            mEditText.setText("");
        }
    }

    public static boolean isStartTimeBeforeEndTime(String time, String endTime) {

        Log.d("tag", "isStartTimeBeforeEndTime: time 1: " + time + " - time 2: " + endTime);
        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endTime);

            if(date1.before(date2)) {
                return true;
            } else {

                return false;
            }
        } catch (ParseException e){
            e.printStackTrace();
        }
        return false;
    }
}
