package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class InterestsFragment extends Fragment implements ListView.OnItemClickListener {

    // Variables
    private Menu mMenu;
    private ListView mListView;
    private final List<String> mInterestsList = new ArrayList<>(Arrays.asList("Video Game", "Sports", "Technology", "Outdoor Activities", "Indoor Activities", "Arts", "Music", "Movies", "Auto", "Food", "Fitness"));
    private SharedPreferences mPrefs;
    private final ArrayList<String> mSelectedInterests = new ArrayList<>();
    private InterestsFragmentInterface mInterestsFragmentInterface;
    private User retrievedUser;
    private int intentCode;

    // Constants
    public static final String INTENT_CODE_RECEIVED = "INTENT_CODE_RECEIVED";
    public static final String INTENT_LOGGED_USER = "INTENT_LOGGED_USER";
    public static final String INTENT_ALL_TYPED_DATA = "INTENT_ALL_TYPED_DATA";
    public static final String INTENT_INTERESTS = "INTENT_INTERESTS";

    public interface InterestsFragmentInterface {
        void moveToMainActivityFromInterests();
        void sendDataBackToEditProfileActivity(Bundle allTypedData, ArrayList<String> newInterests);
    }

    public static InterestsFragment newInstance(User loggedUser, int intentReceivedCode, Bundle allTypedData) {

        Bundle args = new Bundle();
        args.putParcelable(INTENT_LOGGED_USER, loggedUser);
        args.putInt(INTENT_CODE_RECEIVED, intentReceivedCode);
        args.putBundle(INTENT_ALL_TYPED_DATA, allTypedData);
//        args.putStringArrayList(INTENT_INTERESTS, interests);

        InterestsFragment fragment = new InterestsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // verify if the interface is an instance of this context
        if (context instanceof  InterestsFragmentInterface) {
            mInterestsFragmentInterface = (InterestsFragmentInterface)context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_interests, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        mMenu = menu;
        inflater.inflate(R.menu.menu_done, mMenu);

        if (intentCode == 1 && mListView.getCheckedItemCount() >= 3) {

            mMenu.getItem(0).setEnabled(true);
        } else {

            mMenu.getItem(0).setEnabled(false);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() != null && getContext() != null && getArguments() != null) {

            // Find views
            mListView = getView().findViewById(R.id.listview_interests);

            // Adapter
            ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_multiple_choice, mInterestsList);

            // Set the adapter to the list view
            mListView.setAdapter(mArrayAdapter);

            // Assign click listener on the list view
            mListView.setOnItemClickListener(this);
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

            // Instantiate the SharedPreferences
            mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

            // Retrieve the arguments from the instance
            if (getArguments().getParcelable(INTENT_LOGGED_USER) != null
                    && getArguments().getInt(INTENT_CODE_RECEIVED) != 3) {

                retrievedUser = getArguments().getParcelable(INTENT_LOGGED_USER);
                intentCode = getArguments().getInt(INTENT_CODE_RECEIVED);

                if (intentCode == 1) {

                    // Retrieve only the interests array list from the user object
                    final ArrayList<String> newInterests = retrievedUser.getInterests();

                    // Check the interests pre-selected by the user
                    for (int i = 0; i < mInterestsList.size(); i++) {
                        for (int y = 0; y < newInterests.size(); y++) {
                            if (mInterestsList.get(i).equals(newInterests.get(y))) {

                                // Check the item on the list
                                mListView.setItemChecked(i, true);

                                // Add the item into the selected interests array list
                                mSelectedInterests.add(mInterestsList.get(i));

                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//        // Add a checkmark by the cell that was tapped
//        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        if (mListView.isItemChecked(position)) {

            // Save the selected items to another array list
            mSelectedInterests.add(mInterestsList.get(position));
        } else {

            // Remove the items that were unselected
            mSelectedInterests.remove(mInterestsList.get(position));
        }

        if (mListView.getCheckedItemCount() >= 3) {

            // Show the menu if at least 3 were selected
            mMenu.getItem(0).setEnabled(true);
        }
        if (mListView.getCheckedItemCount() < 3) {

            // Disable the menu button whenever there is less than 3 selected
            mMenu.getItem(0).setEnabled(false);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_done_interests:

                updateUserInterests();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    private void updateUserInterests() {

        FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();

        if (user!= null ) {

            // Get the reference from "Users"
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(user.getUid())
                    .child("interests")
                    .setValue(mSelectedInterests).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        if (intentCode == 1) {

                            // Close this activity and send back the collected data to EditProfileActivity
                            if (getArguments() != null) {

                                Bundle allTypedData = getArguments().getBundle(INTENT_ALL_TYPED_DATA);

                                if (allTypedData != null) {

                                    mInterestsFragmentInterface.sendDataBackToEditProfileActivity(allTypedData, mSelectedInterests);
                                }
                            }


                        } else {

                            // TODO: Move to Main Screen, and delete the toast
                            mInterestsFragmentInterface.moveToMainActivityFromInterests();
                            Toast.makeText(getContext(), "Interests were saved to database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }
}

