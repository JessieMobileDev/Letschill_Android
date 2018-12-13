package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.prajwalramamurthy.letschill_finalproject.R;

public class DetailsEventFragment extends Fragment implements View.OnClickListener
{

    private TextView tv_dateTime, tv_location, tv_host, tv_description, tv_participants,
            tv_category, tv_pplJoined, tv_pplRsvp;
    private Button button_rsvp, button_leave, button_join, button_edit;
    private ImageView img_view_background;

    public static DetailsEventFragment newInstance() {

        Bundle args = new Bundle();

        DetailsEventFragment fragment = new DetailsEventFragment();
        fragment.setArguments(args);
        return fragment;
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setHasOptionsMenu(true);
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.fragment_details_event, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        if (getView() != null && getContext() != null) {
            tv_dateTime = getView().findViewById(R.id.textView_detail_dateTime);
            tv_location = getView().findViewById(R.id.textView_detail_location);
            tv_host = getView().findViewById(R.id.textView_detail_host);
            tv_description = getView().findViewById(R.id.textView_detail_desc);
            tv_participants = getView().findViewById(R.id.textView_detail_participants);
            tv_category = getView().findViewById(R.id.textView_detail_category);
            tv_pplJoined = getView().findViewById(R.id.textView_detail_pplJoined);
            tv_pplRsvp = getView().findViewById(R.id.textView_detail_rsvp);
            button_rsvp = getView().findViewById(R.id.button_detail_rsvp);
            button_leave = getView().findViewById(R.id.button_detail_leave);
            button_join = getView().findViewById(R.id.button_detail_joinEvent);
            button_edit = getView().findViewById(R.id.button_detail_edit);
            img_view_background = getView().findViewById(R.id.imageView_details_eventImage);
        }

        }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.button_detail_edit:


                break;

            case R.id.button_detail_joinEvent:


                break;

            case R.id.button_detail_rsvp:


                break;

            case R.id.button_detail_leave:


                break;
        }
    }
}
