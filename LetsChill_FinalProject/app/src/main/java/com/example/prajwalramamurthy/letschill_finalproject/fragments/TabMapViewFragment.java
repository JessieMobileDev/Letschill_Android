package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.content.Context;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.MapMarker;
import com.example.prajwalramamurthy.letschill_finalproject.utility.MainPageAdapter;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Locale;

public class TabMapViewFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener  {

    // Variables
    private GoogleMap mGoogleMap;
    private MapView mMapView;
    private double mCurrentLat = 0.0;
    private double mCurrentLong = 0.0;
    private LatLng mMapTappedLatLng;
    private boolean wasMapTapped = false;
    private ArrayList<MapMarker> mSavedMarkers = new ArrayList<>();
    private ArrayList<Event> mAllEvents;
    private TabMapViewInterface mTabMapViewInterface;

    public interface TabMapViewInterface {

        void openDetailsEventFragment(Event event);
    }

    public static TabMapViewFragment newInstance() {

        Bundle args = new Bundle();

        TabMapViewFragment fragment = new TabMapViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof TabMapViewInterface) {

            mTabMapViewInterface = (TabMapViewInterface)context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_map_view, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() != null) {

            // Find the id for the map view
            mMapView = getView().findViewById(R.id.view_event_map);

            mMapView.onCreate(savedInstanceState);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

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

        // Take the object and pass to the DetailsEventActivity
        Event event = (Event) marker.getTag();
        mTabMapViewInterface.openDetailsEventFragment(event);
    }

    @Override
    public void onMapClick(LatLng latLng) {

        // Map was tapped on
        wasMapTapped = true;

        // Store the lat and long to a local variable
        mMapTappedLatLng = latLng;

        // Move the camera to where the user tapped on
        CameraUpdate mCameraMovement = CameraUpdateFactory.newLatLngZoom(mMapTappedLatLng,16);
        mGoogleMap.animateCamera(mCameraMovement);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        // Map was tapped on
        wasMapTapped = true;

        // Store the lat and long to a local variable
        mMapTappedLatLng = latLng;

        // Move the camera to where the user tapped on
        CameraUpdate mCameraMovement = CameraUpdateFactory.newLatLngZoom(mMapTappedLatLng,16);
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

            // Retrieve the array list with the data
            mAllEvents = (ArrayList<Event>) getArguments().getSerializable(MainPageAdapter.ARGS_ALL_EVENTS);

            if (mAllEvents != null) {

                createMarker();

                // Create one pin on the map for each event object in the arrayList
                for (int i = 0; i < mAllEvents.size(); i++) {

                    MarkerOptions options = new MarkerOptions();
                    options.title(mAllEvents.get(i).getmEventName());
                    options.snippet("On " + mAllEvents.get(i).getmEventDate() + " From " + mAllEvents.get(i).getmEventTimeStart()
                            + " to " + mAllEvents.get(i).getmEventTimeFinish());

                    // Create a LatLng variable
                    LatLng mPinPointedLocation = new LatLng(mAllEvents.get(i).getmLatitude(), mAllEvents.get(i).getmLongitude());

                    // Retrieve the position where the marker will be placed
                    options.position(mPinPointedLocation);

                    // Set on the map
                    Marker marker = googleMap.addMarker(options);
                    marker.setTag(mAllEvents.get(i));
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

            mCurrentLat = getArguments().getDouble(MainPageAdapter.ARGS_LATITUDE);
            mCurrentLong = getArguments().getDouble(MainPageAdapter.ARGS_LONGITUDE);

            // Set the current latitude and longitude to the zoom feature
            LatLng mDeviceLocation = new LatLng(mCurrentLat, mCurrentLong);
            CameraUpdate mCameraMovement = CameraUpdateFactory.newLatLngZoom(mDeviceLocation, 16);
            mGoogleMap.animateCamera(mCameraMovement);
        }
    }

    private void createMarker() {

        // Check if the map is not null
        if (mGoogleMap == null) {
            return;
        }

        // Clear the list before adding
        mSavedMarkers.clear();

        // Add the new marker to the array list
        for (Event event: mAllEvents) {

            mSavedMarkers.add(new MapMarker(event.getmEventId(), event.getmEventLocation(), event.getmLatitude(), event.getmLongitude()));
        }
    }
}
