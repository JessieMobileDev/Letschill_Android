package com.example.prajwalramamurthy.letschill_finalproject.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Objects;

public class ConnectionHandler {

    public static boolean isConnected(Context context) {

        // Check if the user's phone is connected to the internet
        ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = Objects.requireNonNull(connManager).getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()){

            android.net.NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            return  (mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting());

        } else {

            return false;
        }
    }
}
