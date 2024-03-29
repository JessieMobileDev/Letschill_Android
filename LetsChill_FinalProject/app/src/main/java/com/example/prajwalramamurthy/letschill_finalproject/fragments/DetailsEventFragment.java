package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.User;
import com.example.prajwalramamurthy.letschill_finalproject.utility.ConnectionHandler;
import com.example.prajwalramamurthy.letschill_finalproject.utility.FormValidation;
import com.example.prajwalramamurthy.letschill_finalproject.utility.HelperMethods;
import com.example.prajwalramamurthy.letschill_finalproject.utility.ImageDownloadHandler;
import com.example.prajwalramamurthy.letschill_finalproject.utility.JoinedPeopleAdapter;
import com.example.prajwalramamurthy.letschill_finalproject.utility.MenuIntentHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class DetailsEventFragment extends Fragment implements View.OnClickListener, ListView.OnItemClickListener {

    // Variables
    private TextView textView_title, textView_dateTime, textView_location, textView_host,
            textView_description, textView_category, textView_pplJoined,
            textView_pplRsvp;
    private Button button_rsvp, button_leave, button_join, button_edit;
    private ListView mListView_joinedPeople;
    private ImageView imageView_background;
    private DetailsEventInterface mDetailsEventInterface;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private String mUid;
    private DatabaseReference mDBReference;
    private ArrayList<User> mJoinedUsersNames = new ArrayList<>();
    private static final String CHANNEL_ID = "id";
    private static final String CHANNEL_NAME = "name";
    private static final String CHANNEL_DESC = "desc";
    private Event mEvent;
    private User mUser;

    // Constants
    private static final String ARGS_OBJECT = "ARGS_OBJECT";

    public interface DetailsEventInterface {

        void closeDetailsEventActivity(Event mEvent);
        void openUserDetailsProfileScreen(User user);
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
            textView_category = getView().findViewById(R.id.textView_detail_category);

            textView_pplJoined = getView().findViewById(R.id.textView_detail_pplJoined);
//            textView_pplRsvp = getView().findViewById(R.id.textView_detail_rsvp);
            button_rsvp = getView().findViewById(R.id.button_detail_rsvp);
            button_leave = getView().findViewById(R.id.button_detail_leave);
            button_join = getView().findViewById(R.id.button_detail_joinEvent);
            button_edit = getView().findViewById(R.id.button_detail_edit);
            imageView_background = getView().findViewById(R.id.imageView_details_eventImage);
            mListView_joinedPeople = getView().findViewById(R.id.listView_detail_participants);

            // Set on click listeners
            button_edit.setOnClickListener(this);
            button_join.setOnClickListener(this);
            button_leave.setOnClickListener(this);
            button_rsvp.setOnClickListener(this);
            textView_location.setOnClickListener(this);
            mListView_joinedPeople.setOnItemClickListener(this);

            button_leave.setVisibility(View.INVISIBLE);
            button_rsvp.setVisibility(View.INVISIBLE);


            // Retrieve the custom object selected from the list
            mEvent = getArguments().getParcelable(ARGS_OBJECT);

            // Update the label
            textView_pplJoined.setText(mEvent.getmJoinedPeopleIds().size() + "/" + mEvent.getmParticipants() + " people joined");
//            textView_pplRsvp.setText(mEvent.);

//            final ArrayList<String> usersRspved = new ArrayList<>();
//
//            FirebaseDatabase.getInstance().getReference("Events")
//                    .child(HelperMethods.getCurrentMonth(mEvent.getmEventDate())).child(mEvent.getmEventDate())
//                    .child("usersRsvp").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                    for (DataSnapshot rsvp: dataSnapshot.getChildren()) {
//
//                        if (rsvp != null) {
//
//                            String rsvpId = rsvp.getValue(String.class);
//
//                            if (rsvpId != null) {
//
//                                usersRspved.add(rsvpId);
//
//                                textView_pplRsvp.setText(usersRspved.size() + "/" + mEvent.getmParticipants() + " confirmed");
//                            }
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });

            if (mEvent != null) {

                if (mEvent.getmUrl() != null && !mEvent.getmUrl().isEmpty()) {

                    // Variables
                    FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
                    StorageReference mStorageReference = mFirebaseStorage.getReference().child(mEvent.getmUrl());
                    final long ONE_MEGABYTE = 1024 * 1024;

                    Log.d("image", "onActivityCreated: url: " + mEvent.getmUrl());

                    mStorageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {

                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imageView_background.setImageBitmap(bmp);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            exception.printStackTrace();
                        }
                    });

                    // If there is no connection, display default images for the cards based on their categories
                    if (!ConnectionHandler.isConnected(getContext())) {

                        ImageDownloadHandler.showDefaultImagesIfOffline(mEvent.getmCategory(), imageView_background);
                    }
                }

                // Assign data to each UI element and display
                textView_title.setText(mEvent.getmEventName());
                textView_dateTime.setText(mEvent.getmEventDate() + " / " + mEvent.getmEventTimeStart() +
                " - " + mEvent.getmEventTimeFinish());
                textView_location.setText(mEvent.getmEventLocation());
                textView_host.setText(mEvent.getmHost());
                textView_description.setText(mEvent.getmDescription());
                textView_category.setText(mEvent.getmCategory());

                // TODO: missing the "participants"
                // Get the event's joined users names
                mDBReference = FirebaseDatabase.getInstance().getReference("Users");

                // Clear the list before adding anything to it
                mJoinedUsersNames = new ArrayList<>();


                // Loop through the users and find the ones that have joined the event
                FirebaseDatabase.getInstance().getReference("Users")
                        .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot user: dataSnapshot.getChildren()) {

                            if (user != null) {
                                User retrievedUser = user.getValue(User.class);

                                if (retrievedUser != null && mEvent.getmJoinedPeopleIds() != null) {

                                    for (String id : mEvent.getmJoinedPeopleIds()) {

                                        if (user.getKey().equals(id)) {

                                            mJoinedUsersNames.add(retrievedUser);

                                        }
                                    }
                                }
                            }
                        }


                        if (getContext() != null) {

                            // Set the joined users' name into the adapter on the list view
                            JoinedPeopleAdapter mAdapter = new JoinedPeopleAdapter(getContext(), mJoinedUsersNames);
                            mAdapter.notifyDataSetChanged();
                            mListView_joinedPeople.setAdapter(mAdapter);
                            mJoinedUsersNames = new ArrayList<>();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            // Check if the logged in user is the same as the event host
            // If they don't match, do not display the edit button
            getCurrentSignedInUser();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // Get the user object from the cell tapped on and pass to the profile details page
        mDetailsEventInterface.openUserDetailsProfileScreen(mJoinedUsersNames.get(position));
    }

    private void getCurrentSignedInUser() {

        // Retrieve the username from the current logged in user
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mUid = mFirebaseUser.getUid();

        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot users: dataSnapshot.getChildren()) {
                    if (users != null) {
                        User tempUser = users.getValue(User.class);
                        if (tempUser.getUserID().equals(mUid)) {
                            mUser = tempUser;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

                            FirebaseDatabase.getInstance().getReference("Users").child(mUid).child("joinedEvents").addValueEventListener(new ValueEventListener() {
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

                                            } else {

                                                // Display only the "rsvp" and "leave" buttons
                                                button_join.setVisibility(View.VISIBLE);
                                                button_rsvp.setVisibility(View.GONE);
                                                button_leave.setVisibility(View.GONE);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            FirebaseDatabase.getInstance().getReference("Users").child(mUid).child("eventsRsvp").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for (DataSnapshot rsvpEvent: dataSnapshot.getChildren()) {

                                        if (rsvpEvent != null) {

                                            String rsvpEventId = rsvpEvent.getValue(String.class);

                                            if (mEvent.getmEventId().equals(rsvpEventId)) {

                                                // If even id is equal to the user's rsvp'ed event id, display "cancel Rsvp" on the button
                                                button_rsvp.setText("Cancel RSVP");
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

    private void displayNotification()
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_white_24dp)
                .setContentTitle("Notification")
                .setContentText("You have joined the event")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        NotificationManagerCompat notfiManagerCompact = NotificationManagerCompat.from(getContext());
        notfiManagerCompact.notify(1, mBuilder.build());

    }

    private void joinButtonClick()
    {
        // Find out if current logged user is verified
        // Retrieve the username from the current logged in user
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mUid = mFirebaseUser.getUid();

        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot users: dataSnapshot.getChildren()) {
                    if (users != null) {
                        User tempUser = users.getValue(User.class);
                        if (tempUser.getUserID().equals(mUid)) {
                            mUser = tempUser;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (button_join.getText().toString().equals(getResources().getString(R.string.join))) {

            if (HelperMethods.isUserVerified(mUser, getContext())) {

                // If the button text is equal to "Join", continue below
                // This part will only work if the logged in user is not the same as the event host
                button_join.setVisibility(View.INVISIBLE);

                button_leave.setVisibility(View.VISIBLE);
                button_rsvp.setVisibility(View.VISIBLE);

                // Display in-app notification
                displayNotification();


                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                // Make an array to hold all the joined ids
                ArrayList<String> joinedIds;

                // Get all the list from the event
                joinedIds = mEvent.getmJoinedPeopleIds();

                // Add the new user on it
                joinedIds.add(mUid);

                // Join an event: Save the current logged user id to the event node mJoinedPeopleIds
                // Join an event: Save the current event to the current logged user node joinedEvents
                FirebaseDatabase.getInstance().getReference("Events").child(HelperMethods.getCurrentMonth(mEvent.getmEventDate()))
                        .child(mEvent.getmEventDate()).child(mEvent.getmEventId()).child("mJoinedPeopleIds").setValue(joinedIds)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Toast.makeText(getContext(), R.string.toast_firebase_join, Toast.LENGTH_LONG).show();
                            }
                        });

                // Make an array to hold all the joined events ids and get all the events list from the user node
                ArrayList<String> eventsJoined = mUser.getJoinedEvents();

                // Add the new event id to the existing list
                eventsJoined.add(mEvent.getmEventId());

                // Save the new updated list back to the node in the user joinedEvents
                FirebaseDatabase.getInstance().getReference("Users").child(mUid)
                        .child("joinedEvents").setValue(eventsJoined);

                FirebaseDatabase.getInstance().getReference("Users").child(mUid).child("joinedEvents")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot joinedEvent : dataSnapshot.getChildren()) {

                                    String eventId = joinedEvent.getValue(String.class);

                                    if (eventId != null && eventId.equals(mEvent.getmEventId())) {

                                        if (getContext() != null) {
                                            Toast.makeText(getContext(), R.string.toast_firebase_join, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                HashMap<String, String> notification = new HashMap<>();

                notification.put("from", user.getUid());
                notification.put("to", mEvent.getmHost_uid());
                notification.put("type", "joined");

                FirebaseDatabase.getInstance().getReference().child("Notifications").push().setValue(notification);
            } else {

                // Display and alert about not being verified
                FormValidation.displayAlertNoId("Not verified", "Your account is not verified. Verify now in your profile in order to join an event!",
                "OK", getContext());
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
        // Display an alert to make sure that's what they want to do
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Leave event");
        alertDialogBuilder.setMessage("Are you sure you want to leave the current event?");
                alertDialogBuilder.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                                // Remove the current event from the joinedEvents array list
                                ArrayList<String> joinedEventsIds = mUser.getJoinedEvents();

                                if (joinedEventsIds != null) {

                                    for (String joinedEvent: joinedEventsIds) {
                                        if (joinedEvent.equals(mEvent.getmEventId())) {
                                            joinedEventsIds.remove(joinedEvent);
                                        }
                                    }

                                    // Save the updated array list back to the database
                                    FirebaseDatabase.getInstance().getReference("Users").child(mUid)
                                            .child("joinedEvents").setValue(joinedEventsIds);
                                }

                                // Remove the current user id from the event node mJoinedPeopleIds
                                ArrayList<String> joinedPeopleIds = mEvent.getmJoinedPeopleIds();

                                if (joinedPeopleIds != null) {

                                    for (String joinedUser: joinedPeopleIds) {
                                        if (joinedUser.equals(mUser.getUserID())) {
                                            joinedPeopleIds.remove(joinedUser);
                                        }
                                    }

                                    // Save the updated array list back to the database
                                    FirebaseDatabase.getInstance().getReference("Events")
                                            .child(HelperMethods.getCurrentMonth(mEvent.getmEventDate()))
                                            .child(mEvent.getmEventDate()).child(mEvent.getmEventId())
                                            .child("mJoinedPeopleIds").setValue(joinedPeopleIds);
                                }


                                // Remove the rsvp from the event if there is any matching
                                FirebaseDatabase.getInstance().getReference().child("Events")
                                        .child(HelperMethods.getCurrentMonth(mEvent.getmEventDate())).child(mEvent.getmEventDate())
                                        .child(mEvent.getmEventId()).child("usersRsvp").child(mUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        button_rsvp.setText("RSVP");
                                    }
                                });

                                // Remove the event from the user's node eventsRsvp
                                FirebaseDatabase.getInstance().getReference().child("Users").child(mUid).child("eventsRsvp")
                                        .child(mEvent.getmEventId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        button_rsvp.setText("RSVP");
                                    }
                                });

                                // Display a toast to confirm that they left the event
                                Toast.makeText(getContext(),"You left the event",Toast.LENGTH_LONG).show();

                                // This part will only work if the logged in user is not the same as the event host
                                button_join.setVisibility(View.VISIBLE);

                                button_leave.setVisibility(View.INVISIBLE);
                                button_rsvp.setVisibility(View.INVISIBLE);
                            }
                        });

        alertDialogBuilder.setNegativeButton("NO", null);

        alertDialogBuilder.show();
    }

    private void rsvpButtonClick() {

        // Note: this method is being called inside the "joinButtonClick()". I'm recycling the buttons
        // instead of having a bunch of buttons on the layout. It's all connected and working. Just do
        // the rsvp functionality in this method and it will work.

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userUid = user.getUid();

        if (button_rsvp.getText().equals("RSVP")) {

            if (HelperMethods.isUserVerified(mUser, getContext())) {

                Toast.makeText(getContext(), "Thank You for the event RSVP", Toast.LENGTH_LONG).show();

                // Notify the host that someone has just rsvp'ed to the event
                HashMap<String, String> notification = new HashMap<>();

                notification.put("from", user.getUid());
                notification.put("to", mEvent.getmHost_uid());
                notification.put("type", "rsvp");

                // Save the notification uid in the database
                FirebaseDatabase.getInstance().getReference().child("Notifications").push().setValue(notification);

                // Save the current logged user uid into the event's rsvp'ed node
                if (!userUid.isEmpty()) {
// && userUid != null && mEvent.getmEventId() != null && !mEvent.getmEventId().isEmpty()

                    mDBReference = FirebaseDatabase.getInstance().getReference().child("Events")
                            .child(HelperMethods.getCurrentMonth(mEvent.getmEventDate())).child(mEvent.getmEventDate())
                            .child(mEvent.getmEventId()).child("usersRsvp");
                    mDBReference.child(userUid).setValue(userUid);

                    // Save the event uid to the user's rsvp'ed node
                    mDBReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userUid).child("eventsRsvp");
                    mDBReference.child(mEvent.getmEventId()).setValue(mEvent.getmEventId());

                    // Change the rsvp button title to "cancel RSVP"
                    button_rsvp.setText("Cancel RSVP");

                }
            } else {

                // Display and alert about not being verified
                FormValidation.displayAlertNoId("Not verified", "Your account is not verified. Verify now in your profile in order to join an event!",
                        "OK", getContext());
            }

        } else if (button_rsvp.getText().equals("Cancel RSVP")) {


            // TESTING
            FirebaseDatabase.getInstance().getReference().child("Events")
                    .child(HelperMethods.getCurrentMonth(mEvent.getmEventDate())).child(mEvent.getmEventDate())
                    .child(mEvent.getmEventId()).child("usersRsvp").child(userUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    button_rsvp.setText("RSVP");
                }
            });

            FirebaseDatabase.getInstance().getReference().child("Users").child(userUid).child("eventsRsvp")
                    .child(mEvent.getmEventId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    button_rsvp.setText("RSVP");
                }
            });

        }
    }

    private void openMapOnClick() {

        String query = "geo:0,0?q=" + mEvent.getmEventLocation();
        Uri gmmIntentUri = Uri.parse(query);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        getContext().startActivity(mapIntent);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (getContext() != null && getActivity() != null) {

            MenuIntentHandler.getMenuIntents(item, getContext(), getActivity(), MenuIntentHandler.DETAILS_EVENT_ACTIVITY);
        }

        return false;

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

                if (ConnectionHandler.isConnected(getContext())) {

                    joinButtonClick();

                } else {
                    Toast.makeText(getContext(), "No Network. Please check network connection for further activity.", Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.button_detail_rsvp:

                // This method will be called ONLY when host is not equal to logged in user
                Log.d("test", "onClick (rsvp): Host is not equal to logged in user");

                if (ConnectionHandler.isConnected(getContext())) {

                    rsvpButtonClick();

                } else {
                    Toast.makeText(getContext(), "No Network. Please check network connection for further activity.", Toast.LENGTH_LONG).show();
                }


                break;

            case R.id.button_detail_leave:


                if (ConnectionHandler.isConnected(getContext())) {

                    leaveButtonClick();

                } else {
                    Toast.makeText(getContext(), "No Network. Please check network connection for further activity.", Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.textView_detail_location:

                if (ConnectionHandler.isConnected(getContext())) {

                    openMapOnClick();

                }
                else
                {
                    Toast.makeText(getContext(), "No Network. Please check network connection for further activity.", Toast.LENGTH_LONG).show();
                }


                break;
        }
    }
}
