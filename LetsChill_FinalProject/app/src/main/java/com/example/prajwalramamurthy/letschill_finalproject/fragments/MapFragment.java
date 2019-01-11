package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.prajwalramamurthy.letschill_finalproject.data_model.MapMarker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapFragment extends com.google.android.gms.maps.MapFragment implements OnMapReadyCallback,
        GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener {

    // Variables
    private GoogleMap mGoogleMap;
    private double mCurrentLat = 0.0;
    private double mCurrentLong = 0.0;
    private boolean wasMapTapped = false;
    private LatLng mMapTappedLatLnd;
    private boolean wasMapLongTapped = false;
    private LatLng mMapLongTappedLatLng;
    private int mSelectedMarker;
    private ArrayList<MapMarker> mSavedMarkers = new ArrayList<>();
    private Geocoder mGeocoder;
    private MapFragmentInterface mMapFragmentInterface;

    // Constants
    private static final String ARG_LONGITUDE = "ARG_LONGITUDE";
    private static final String ARG_LATITUDE = "ARG_LATITUDE";
    private static final String ARG_ADDRESS = "ARG_ADDRESS";
    private static final String ARG_ISTHISASEARCH = "ARG_ISTHISASEARCH";
    public static final String ARG_ALL_DATA_BUNDLE = "ARG_ALL_DATA_BUNDLE";

    public interface MapFragmentInterface {

        void passAddressBackToMapActivity(String address, Bundle allDataBundle);
    }

    public static MapFragment newInstance(double mCurrentLong, double mCurrentLat, String mAddress, boolean mIsThisASearch, Bundle allDataBundle) {

        Bundle args = new Bundle();
        args.putDouble(ARG_LONGITUDE, mCurrentLong);
        args.putDouble(ARG_LATITUDE, mCurrentLat);
        args.putBoolean(ARG_ISTHISASEARCH, mIsThisASearch);
        args.putBundle(ARG_ALL_DATA_BUNDLE, allDataBundle);

        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MapFragmentInterface) {

            mMapFragmentInterface = (MapFragmentInterface)context;
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        if (getContext() != null) {

            mGeocoder = new Geocoder(getContext(), Locale.getDefault());
        }
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        // Implemented OnMapReadyCallback before-hand in order to call it here
        getMapAsync(this);

    }

    private void createMarker(String mAddress, double mLatitude, double mLongitude) {

        // Check if the map is not null
        if (mGoogleMap == null) {
            return;
        }

        // Add the new marker to the array list
        mSavedMarkers.add(new MapMarker(mAddress, mLatitude, mLongitude));
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        Log.d("marker", "onInfoWindowClick: marker address: " + marker.getSnippet());

        // Grab the data bundle
        if (getArguments() != null) {

            Bundle allDataBundle = getArguments().getBundle(ARG_ALL_DATA_BUNDLE);

            // Pass the address back to the MapActivity
            mMapFragmentInterface.passAddressBackToMapActivity(marker.getSnippet(), allDataBundle);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {

        // Map was tapped on
        wasMapTapped = true;

        // Store the lat and long to a local variable
        mMapTappedLatLnd = latLng;

        // Move the camera to where the user tapped on
        CameraUpdate mCameraMovement = CameraUpdateFactory.newLatLngZoom(mMapTappedLatLnd,16);
        mGoogleMap.animateCamera(mCameraMovement);

        // Retrieve the address of the place tapped on
        List<Address> mAddresses = new ArrayList<>();

        try {

            mAddresses = mGeocoder.getFromLocation(latLng.latitude, latLng.longitude,1);

        } catch (IOException e) {

            e.printStackTrace();
        }

        // Retrieve the address from the list
        Address mSelectedAddress = mAddresses.get(0);

        if (mSelectedAddress != null && getContext() != null) {

            Toast.makeText(getContext(), mSelectedAddress.getAddressLine(0), Toast.LENGTH_LONG).show();

            mSavedMarkers.clear();

            createMarker(mSelectedAddress.getAddressLine(0), latLng.latitude, latLng.longitude);

            // Put the created marker on the map
            mGoogleMap.clear();

            for (MapMarker marker: mSavedMarkers) {
                MarkerOptions options = new MarkerOptions();
                options.title("Address:");
                options.snippet(marker.getmAddress());

                // Create a LatLng variable
                LatLng pinPointedLocation = new LatLng(marker.getmLatitude(), marker.getmLongitude());

                // Retrieves the position where the marker will be placed
                options.position(pinPointedLocation);

                marker.setmMarkerId(mGoogleMap.addMarker(options).getId());
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        // Map was tapped on
        wasMapTapped = true;

        // Store the lat and long to a local variable
        mMapTappedLatLnd = latLng;

        // Move the camera to where the user tapped on
        CameraUpdate mCameraMovement = CameraUpdateFactory.newLatLngZoom(mMapTappedLatLnd,16);
        mGoogleMap.animateCamera(mCameraMovement);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Assign variables to its values and set up click listeners
        this.mGoogleMap = googleMap;
        this.mGoogleMap.setInfoWindowAdapter(this);
        this.mGoogleMap.setOnInfoWindowClickListener(this);
        this.mGoogleMap.setOnMapClickListener(this);
        this.mGoogleMap.setOnMapLongClickListener(this);

        // When the fragment opens, the map will be zoomed in to the device's current location
        showInitialLocationZoomedIn();

        if (getArguments() != null) {

            boolean mIsThisASearch = getArguments().getBoolean(ARG_ISTHISASEARCH);

            if (mIsThisASearch) {

                // Create a marker with the address that was passed to this fragment and add to the map
                List<Address> mAddresses = new ArrayList<>();
                double mLat = getArguments().getDouble(ARG_LATITUDE);
                double mLon = getArguments().getDouble(ARG_LONGITUDE);

                if (mLat != 0.0 && mLon != 0.0) {

                    try {

                        mAddresses = mGeocoder.getFromLocation(mLat, mLon, 1);

                    } catch (IOException e) {

                        e.printStackTrace();
                    }

                    // Retrieve the address from the list
                    Address mSelectedAddress = mAddresses.get(0);

                    // Create the marker and add to the list
                    createMarker(mSelectedAddress.getAddressLine(0), mLat, mLon);

                    // Recreate the markers
                    for (MapMarker marker : mSavedMarkers) {

                        MarkerOptions options = new MarkerOptions();
                        options.title("Address:");
                        options.snippet(marker.getmAddress());

                        // Create a LatLng variable
                        LatLng mPinPointedLocation = new LatLng(marker.getmLatitude(), marker.getmLongitude());

                        // Retrieve the position where the marker will be placed
                        options.position(mPinPointedLocation);

                        // Save the id of the marker and set on the map
                        marker.setmMarkerId(googleMap.addMarker(options).getId());
                    }
                }
            }
        }
    }

    private void showInitialLocationZoomedIn() {

        // Check if the variable mGoogleMap was previously set on "onMapReady"
        if (mGoogleMap == null) {
            Log.d("maptest", "showInitialLocationZoomedIn: map is null");
            return;
        }

        Log.d("maptest", "showInitialLocationZoomedIn: map is not null");

        // Get the current device's longitude and latitude in order to zoom in when the app opens
        if (getActivity() != null && getArguments() != null) {

            mCurrentLat = getArguments().getDouble(ARG_LATITUDE);
            mCurrentLong = getArguments().getDouble(ARG_LONGITUDE);

            // Set the current latitude and longitude to the zoom feature
            LatLng mDeviceLocation = new LatLng(mCurrentLat, mCurrentLong);
            CameraUpdate mCameraMovement = CameraUpdateFactory.newLatLngZoom(mDeviceLocation, 16);
            mGoogleMap.animateCamera(mCameraMovement);
        }
    }
}
