package com.example.prajwalramamurthy.letschill_finalproject.activities;

import android.Manifest;
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
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class MapActivity extends AppCompatActivity implements LocationListener {

    // Variables
    private EditText mEditText_search;

    //Constants
    private static final int REQUEST_LOCATION_PERMISSION = 0x01101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Assign the id to the edit text
        mEditText_search = findViewById(R.id.editText_search);

        // Get current location
        requestLocation();

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

                            // Isolate the address
                            Address mTypedAddress = addresses.get(0);

                            // Reloads the fragment and pass the lat and long
                            getFragmentManager().beginTransaction().add(R.id.frame_activity_map, MapFragment.newInstance(mTypedAddress.getLongitude(), mTypedAddress.getLatitude())).commit();

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
                getFragmentManager().beginTransaction().add(R.id.frame_activity_map, MapFragment.newInstance(mCurrentLongitude, mCurrentLatitude)).commit();

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


}
