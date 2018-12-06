package com.example.prajwalramamurthy.letschill_finalproject.utility;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class FormValidation {

    public static String isPasswordValid2(String mPassword) {

        // Password:
        // -- Must be greater than 8 characters;
        // -- Must contain 1 uppercase letter;
        // -- Must contain 1 number;
        // -- Must not have special characters.

        String mMessage = "";

        // Conditions
        Pattern specialCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Pattern upperCasePatten = Pattern.compile("[A-Z ]");
        Pattern digitCasePatten = Pattern.compile("[0-9 ]");

        // Validation
        if (specialCharPatten.matcher(mPassword).find()) {
            mMessage = "Cannot contain special characters";
        }

        if (!upperCasePatten.matcher(mPassword).find()) {
            mMessage = "Must contain at least 1 uppercase letter";
        }

        if (mPassword.length() < 8) {
            mMessage = "Must contain more than 8 characters long";
        }

        if (!digitCasePatten.matcher(mPassword).find()) {
            mMessage = "Must contain at least one number";
        }

        return mMessage;
    }

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
}
