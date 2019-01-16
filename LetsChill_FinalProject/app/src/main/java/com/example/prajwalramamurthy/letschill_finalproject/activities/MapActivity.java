package com.example.prajwalramamurthy.letschill_finalproject.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.fragments.MapFragment;
import com.example.prajwalramamurthy.letschill_finalproject.utility.AddressValidation;
import com.example.prajwalramamurthy.letschill_finalproject.utility.FormValidation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements LocationListener, MapFragment.MapFragmentInterface {

    // Variables
    private EditText mEditText_search;
    private Bundle allDataBundle = new Bundle();

    //Constants
    private static final int REQUEST_LOCATION_PERMISSION = 0x01101;
    public static final String INTENT_RESULT_ADDRESS = "INTENT_RESULT_ADDRESS";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Assign the id to the edit text
        mEditText_search = findViewById(R.id.editText_search);

        // Get the data from the intent
        Intent receivedIntent = getIntent();
        int requestFromCreateEventOrEditEvent = receivedIntent.getIntExtra(EditEventActivity.AFTER_CLICKING_MAP_ICON, 3);

        if (requestFromCreateEventOrEditEvent == 0) {

            allDataBundle = receivedIntent.getBundleExtra(EventActivity.BUNDLE_FORM_ALL_DATA);

            // Get current location
            requestLocation();

        } else if (requestFromCreateEventOrEditEvent == 1) {

            allDataBundle = receivedIntent.getBundleExtra(EditEventActivity.BUNDLE_FORM_ALL_DATA_EDIT);

            // Get current location
            requestLocation();
        }

        // Edit text on touch listener
        mEditText_search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (mEditText_search.getRight() - mEditText_search.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        List<Address> addresses = null;

                        // Capture the text inside the EditText
                        if (!mEditText_search.getText().toString().isEmpty()
                                && !mEditText_search.getText().toString().equals("")
                                && mEditText_search.getText().toString() != null) {

                            String address = mEditText_search.getText().toString();
                            Geocoder geocoder = new Geocoder(MapActivity.this);

                            // Try to find the location
                            try {

                                addresses = geocoder.getFromLocationName(address, 1);

                            } catch (IOException e) {

                                e.printStackTrace();
                            }

                            if (addresses != null && addresses.size() != 0) {

                                // Isolate the address
                                Address mTypedAddress = addresses.get(0);

                                // Reloads the fragment and pass the lat and long
                                getFragmentManager().beginTransaction().add(R.id.frame_activity_map, MapFragment.newInstance(mTypedAddress.getLongitude(), mTypedAddress.getLatitude(), mTypedAddress.getAddressLine(0), true, allDataBundle)).commit();
                            } else {

                                FormValidation.displayAlert(R.string.alert_title_address,
                                        R.string.alert_message_address, R.string.alert_button_address, MapActivity.this);
                            }
                        }

                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {

            requestLocation();

        }
    }

    private void requestLocation() {

        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Get the current latitude and logitude of the device
            Location mCurrentLocation;
            if (mLocationManager != null) {

                mCurrentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (mCurrentLocation == null) {

                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            2000, 10.0f, this);
                }

                mCurrentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                double mCurrentLongitude = mCurrentLocation.getLongitude();
                double mCurrentLatitude = mCurrentLocation.getLatitude();

                // Start the fragment that will hold the map
                getFragmentManager().beginTransaction().add(R.id.frame_activity_map, MapFragment.newInstance(mCurrentLongitude, mCurrentLatitude, "no address", false, allDataBundle)).commit();

            }
        } else {

            // If the app does not have the permission, then request it
            ActivityCompat.requestPermissions(this, new String[]
                            { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    public void passAddressBackToMapActivity(String address, Bundle allDataBundle) {

        Log.d("address", "passAddressBackToMapActivity: address: " + address);
        // Send the address as a result back to the EventActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra(INTENT_RESULT_ADDRESS, address);
        resultIntent.putExtra(MapFragment.ARG_ALL_DATA_BUNDLE, allDataBundle);
        setResult(Activity.RESULT_OK, resultIntent);

        finish();
    }
}
