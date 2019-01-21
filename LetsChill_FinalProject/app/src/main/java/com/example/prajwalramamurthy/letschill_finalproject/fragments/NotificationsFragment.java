package com.example.prajwalramamurthy.letschill_finalproject.fragments;



import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.utility.MenuIntentHandler;

public class NotificationsFragment extends Fragment
{

    private Switch switchEventChanged, switchGuestJoined, switchEventTimeNotify;

    public static NotificationsFragment newInstance() {

        Bundle args = new Bundle();

        NotificationsFragment fragment = new NotificationsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//
//        inflater.inflate(R.menu.menu_main, menu);
//    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        if (getContext() != null && getActivity() != null) {
//
//            MenuIntentHandler.getMenuIntents(item, getContext(), getActivity(), MenuIntentHandler.NOTIFICATIONS_ACTIVITY);
//        }
//
//        return false;
//
//    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() != null && getContext() != null) {

            // Find views
            switchEventChanged = getView().findViewById(R.id.switch_eventChanged);
            switchGuestJoined = getView().findViewById(R.id.switch_guestJoined);
            switchEventTimeNotify = getView().findViewById(R.id.switch_eventTimeNotify);


        }

    }


}
