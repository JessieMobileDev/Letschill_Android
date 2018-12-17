package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.widget.Toast;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DetailsEventFragment extends Fragment implements View.OnClickListener
{
    // Variables
    private TextView textView_title, textView_dateTime, textView_location, textView_host,
            textView_description, textView_participants, textView_category, textView_pplJoined,
            textView_pplRsvp;
    private Button button_rsvp, button_leave, button_join, button_edit;
    private ImageView imageView_background;
    private DetailsEventInterface mDetailsEventInterface;
    private FirebaseAuth mAuth;

    // Constants
    private static final String ARGS_OBJECT = "ARGS_OBJECT";
    private Event mEvent;

    public interface DetailsEventInterface {

        void closeDetailsEventActivity(Event mEvent);
    }

    public static DetailsEventFragment newInstance(Event mEvent) {

        Bundle args = new Bundle();
        args.putParcelable(ARGS_OBJECT, mEvent);

        DetailsEventFragment fragment = new DetailsEventFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof DetailsEventInterface) {

            mDetailsEventInterface = (DetailsEventInterface)context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.fragment_details_event, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() != null && getContext() != null) {

            // Find views
            textView_title = getView().findViewById(R.id.textView_title);
            textView_dateTime = getView().findViewById(R.id.textView_detail_dateTime);
            textView_location = getView().findViewById(R.id.textView_detail_location);
            textView_host = getView().findViewById(R.id.textView_detail_host);
            textView_description = getView().findViewById(R.id.textView_detail_description);
            textView_participants = getView().findViewById(R.id.textView_detail_participants);
            textView_category = getView().findViewById(R.id.textView_detail_category);

            textView_pplJoined = getView().findViewById(R.id.textView_detail_pplJoined);
            textView_pplRsvp = getView().findViewById(R.id.textView_detail_rsvp);
            button_rsvp = getView().findViewById(R.id.button_detail_rsvp);
            button_leave = getView().findViewById(R.id.button_detail_leave);
            button_join = getView().findViewById(R.id.button_detail_joinEvent);
            button_edit = getView().findViewById(R.id.button_detail_edit);
            imageView_background = getView().findViewById(R.id.imageView_details_eventImage);

            // Set on click listeners
            button_edit.setOnClickListener(this);
            button_join.setOnClickListener(this);
            button_leave.setOnClickListener(this);
            button_rsvp.setOnClickListener(this);

            button_leave.setVisibility(View.INVISIBLE);
            button_rsvp.setVisibility(View.INVISIBLE);


            // Retrieve the custom object selected from the list
            mEvent = getArguments().getParcelable(ARGS_OBJECT);

            if (mEvent != null) {

                // Assign data to each UI element and display
                textView_title.setText(mEvent.getmEventName());
                textView_dateTime.setText(mEvent.getmEventDate() + " / " + mEvent.getmEventTimeStart() +
                " - " + mEvent.getmEventTimeFinish());
                textView_location.setText(mEvent.getmEventLocation());
                textView_host.setText(mEvent.getmHost());
                textView_description.setText(mEvent.getmDescription());
                textView_category.setText(mEvent.getmCategory());

                // TODO: missing the "participants"

            }
        }

    }

    private void joinButtonClick()
    {
        button_join.setVisibility(View.INVISIBLE);

        button_leave.setVisibility(View.VISIBLE);
        button_rsvp.setVisibility(View.VISIBLE);

        // show toast
        Toast.makeText(getContext(), R.string.toast_event_joined, Toast.LENGTH_SHORT).show();


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!= null) {
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

            Query query = ref.child("Events").equalTo(mEvent.getmEventId());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Toast.makeText(getContext(), R.string.toast_firebase_join, Toast.LENGTH_LONG).show();
                    } else {
                        ref.child(user.getUid()).child("Events").push().setValue(mEvent.getmEventId());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }


    private void leaveButtonClick()
    {


    }

    private void rsvpButtonClick()
    {

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

                // Retrieve the custom object selected from the list
                Event mEvent = getArguments().getParcelable(ARGS_OBJECT);

                // Close current activity and open the Edit Event
                mDetailsEventInterface.closeDetailsEventActivity(mEvent);

                break;

            case R.id.button_detail_joinEvent:

                joinButtonClick();

                break;

            case R.id.button_detail_rsvp:


                break;

            case R.id.button_detail_leave:


                break;
        }
    }
}
