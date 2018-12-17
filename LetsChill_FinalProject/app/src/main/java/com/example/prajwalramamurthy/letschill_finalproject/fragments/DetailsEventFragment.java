package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    private FirebaseUser mFirebaseUser;
    private String mUid;

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

            // Check if the logged in user is the same as the event host
            // If they don't match, do not display the edit button
            getCurrentSignedInUser();
        }

    }

    private void getCurrentSignedInUser() {

        // Retrieve the username from the current logged in user
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mUid = mFirebaseUser.getUid();

        FirebaseDatabase.getInstance().getReference("Users").child(mUid).child("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (getArguments() != null) {

                    // Get the current logged in user
                    String mUsername = dataSnapshot.getValue(String.class);

                    // Retrieve the event that was passed to this fragment
                    final Event mEvent = getArguments().getParcelable(ARGS_OBJECT);

                    if (mEvent != null) {

                        if (!mEvent.getmHost().equals(mUsername)) {

                            // Make the delete button disappear
                            button_edit.setVisibility(View.GONE);

                            // Change the "join button" text to "join"
                            button_join.setText(R.string.join);
                            Log.d("test", "joinButtonClick: other's event");

                            // TODO: check if the user has already joined this event

                            FirebaseDatabase.getInstance().getReference("Users").child(mUid).child("Events").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for (DataSnapshot joinedEvent: dataSnapshot.getChildren()) {

                                        String mJoinedEventID = joinedEvent.getValue(String.class);

                                        if (mJoinedEventID != null) {

                                            // Check if the logged in user is currently participating on the
                                            // selected event
                                            if (mEvent.getmEventId().equals(mJoinedEventID)) {

                                                // Display only the "rsvp" and "leave" buttons
                                                button_join.setVisibility(View.GONE);
                                                button_rsvp.setVisibility(View.VISIBLE);
                                                button_leave.setVisibility(View.VISIBLE);

                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                        // If the current logged in user is equal to the event's host name,
                        // change the "join" button to "rsvp" text
                        else if (mEvent.getmHost().equals(mUsername)) {

                            button_join.setText(R.string.rsvp);

                            button_rsvp.setVisibility(View.GONE);
                            Log.d("test", "joinButtonClick: my own event");
                        }
                    }

                    // Get today's date
                    SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("M-dd-yyyy");
                    String mTodayDateString = mSimpleDateFormat.format(Calendar.getInstance().getTime());

                    // If the user tapped on an event that has passed already, they can't see the join button
                    try {

                        Date mSelectedEventDate = mSimpleDateFormat.parse(mEvent.getmEventDate());
                        Date mTodayDate = mSimpleDateFormat.parse(mTodayDateString);

                        if (mSelectedEventDate.before(mTodayDate)) {

                            // If selected event date is past today's date, the user cannot join
                            button_join.setVisibility(View.GONE);
                        }

                    } catch (Exception e) {

                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void joinButtonClick()
    {
        if (button_join.getText().toString().equals(getResources().getString(R.string.join))) {

            // If the button text is equal to "Join", continue below
            // This part will only work if the logged in user is not the same as the event host
            button_join.setVisibility(View.INVISIBLE);

            button_leave.setVisibility(View.VISIBLE);
            button_rsvp.setVisibility(View.VISIBLE);

            // Show toast
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
        } else if (button_join.getText().toString().equals(getResources().getString(R.string.rsvp))) {

            // If the button text is equal to "rsvp", continue below
            // This part will only happen if the logged in user is the same as the event host
            button_join.setVisibility(View.VISIBLE);

            button_leave.setVisibility(View.INVISIBLE);
            button_rsvp.setVisibility(View.INVISIBLE);

            // Perform the rsvp functionality
            Log.d("test", "onClick (rsvp): Host is equal to logged in user");
            rsvpButtonClick();
        }
    }

    private void leaveButtonClick()
    {


    }

    private void rsvpButtonClick()
    {
        // Note: this method is being called inside the "joinButtonClick()". I'm recycling the buttons
        // instead of having a bunch of buttons on the layout. It's all connected and working. Just do
        // the rsvp functionality in this method and it will work.


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

                // This method will be called ONLY when host is not equal to logged in user
                Log.d("test", "onClick (rsvp): Host is not equal to logged in user");
                rsvpButtonClick();

                break;

            case R.id.button_detail_leave:

                leaveButtonClick();

                break;
        }
    }
}
