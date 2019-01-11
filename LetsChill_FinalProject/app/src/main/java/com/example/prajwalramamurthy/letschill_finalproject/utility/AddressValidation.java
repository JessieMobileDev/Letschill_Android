package com.example.prajwalramamurthy.letschill_finalproject.utility;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddressValidation {

    public static ArrayList<Double> getLatLngFromAddress(String address, Context context) {

        List<Address> addresses = null;
        ArrayList<Double> latAndLng = new ArrayList<>();
        Geocoder geocoder = new Geocoder(context);

        // Try to find the location
        try {

            addresses = geocoder.getFromLocationName(address, 1);

        } catch (IOException e) {

            e.printStackTrace();
        }

        if (addresses != null && addresses.size() != 0) {

            // Isolate the address
            Address mTypedAddress = addresses.get(0);

            // Add the lat and lng to the array list
            latAndLng.add(mTypedAddress.getLatitude());
            latAndLng.add(mTypedAddress.getLongitude());
        }

        return latAndLng;
    }

    public static Address getAddressFromString(String address, Context context) {

        List<Address> addresses = new ArrayList<>();

        if (context != null) {

            Geocoder geocoder = new Geocoder(context);

            // Try to find the location
            try {

                addresses = geocoder.getFromLocationName(address, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Isolate the address
        return addresses.get(0);
    }
}
