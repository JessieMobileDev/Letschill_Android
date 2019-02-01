package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import com.example.prajwalramamurthy.letschill_finalproject.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchFilterFragment extends Fragment implements View.OnClickListener,
        ListView.OnItemClickListener, SeekBar.OnSeekBarChangeListener {

    // Variables
    private RadioButton mRButton_title, mRButton_city, mRButton_interests;
    private SeekBar mSeekBar_miles;
    private TextView mTextView_radiusTitle, mTextView_interestsTitle;
    private ListView mListView_interests;
    private SharedPreferences mPrefs;
    private List<String> mInterests;
    private ArrayList<String> mSelectedInterests = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;

    // Constants
    public static final String PREFS_FILTER_SELECTION = "PREFS_FILTER_SELECTION";
    public static final String PREFS_FILTER_MILES = "PREFS_FILTER_MILES";
    public static final String PREFS_FILTER_INTERESTS = "PREFS_FILTER_INTERESTS";

    public static SearchFilterFragment newInstance() {

        Bundle args = new Bundle();

        SearchFilterFragment fragment = new SearchFilterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_filter_search, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() != null && getContext() != null) {

            // Find the views
            mRButton_title = getView().findViewById(R.id.radioButton_title);
            mRButton_city = getView().findViewById(R.id.radioButton_search_city);
            mRButton_interests = getView().findViewById(R.id.radioButton_search_interests);
            mSeekBar_miles = getView().findViewById(R.id.seekBar_search_radius);
            mTextView_radiusTitle = getView().findViewById(R.id.textView_radius);
            mTextView_interestsTitle = getView().findViewById(R.id.textView_search_interests);
            mListView_interests = getView().findViewById(R.id.listView_search_interests);

            // Assign listeners
            mRButton_title.setOnClickListener(this);
            mRButton_city.setOnClickListener(this);
            mRButton_interests.setOnClickListener(this);
            mListView_interests.setOnItemClickListener(this);
            mListView_interests.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            mListView_interests.setItemsCanFocus(false);
            mSeekBar_miles.setOnSeekBarChangeListener(this);

            // Convert the xml list of strings into an array list
            mInterests = Arrays.asList(getResources().getStringArray(R.array.spinner_category));

            // Set the adapter to the listview
            mAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_multiple_choice, mInterests);
            mListView_interests.setAdapter(mAdapter);

            // Instantiate the SharedPreferences
            mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

            // Update the UI with the previously saved preferences
            updatePreferencesUI();
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.radioButton_title:

                // Hide the interests text view and listview
                mTextView_interestsTitle.setVisibility(View.GONE);
                mListView_interests.setVisibility(View.GONE);

                // Save selection to user defaults
                mPrefs.edit().putString(PREFS_FILTER_SELECTION, "title").apply();

                // Display the miles on the seekbar
                mSeekBar_miles.setProgress(mPrefs.getInt(PREFS_FILTER_MILES, 3));

                break;
            case R.id.radioButton_search_city:

                // Hide the interests text view and listview
                mTextView_interestsTitle.setVisibility(View.GONE);
                mListView_interests.setVisibility(View.GONE);

                // Save selection to user defaults
                mPrefs.edit().putString(PREFS_FILTER_SELECTION, "city").apply();

                // Display the miles on the seekbar
                mSeekBar_miles.setProgress(mPrefs.getInt(PREFS_FILTER_MILES, 3));

                break;
            case R.id.radioButton_search_interests:

                // Display the interests text view and listview
                mTextView_interestsTitle.setVisibility(View.VISIBLE);
                mListView_interests.setVisibility(View.VISIBLE);

                // Save selection to user defaults
                mPrefs.edit().putString(PREFS_FILTER_SELECTION, "interests").apply();

                // Display the miles on the seekbar
                mSeekBar_miles.setProgress(mPrefs.getInt(PREFS_FILTER_MILES, 3));
                break;
        }
    }

    private void updatePreferencesUI() {

        // Display the listview selected rows colors
        displaySelectedRows();

        switch (mPrefs.getString(PREFS_FILTER_SELECTION, "nothing")) {

            case "title":

                // Select the title radius button
                mRButton_title.setChecked(true);

                // Also make the interests text view and list view disappear for selection
                mTextView_interestsTitle.setVisibility(View.GONE);
                mListView_interests.setVisibility(View.GONE);

                // Display the miles on the seekbar
                mSeekBar_miles.setProgress(mPrefs.getInt(PREFS_FILTER_MILES, 3));

                break;
            case "city":

                // Select the city radius button
                mRButton_city.setChecked(true);

                // Also make the interests text view and list view disappear for selection
                mTextView_interestsTitle.setVisibility(View.GONE);
                mListView_interests.setVisibility(View.GONE);

                // Display the miles on the seekbar
                mSeekBar_miles.setProgress(mPrefs.getInt(PREFS_FILTER_MILES, 3));

                break;
            case "interests":

                // Select the interests radius button
                mRButton_interests.setChecked(true);

                // Also make the interests text view and list view appear for selection
                mTextView_interestsTitle.setVisibility(View.VISIBLE);
                mListView_interests.setVisibility(View.VISIBLE);

                // Display the miles on the seekbar
                mSeekBar_miles.setProgress(mPrefs.getInt(PREFS_FILTER_MILES, 3));

                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Set<String> mSetInterests = new HashSet<>();

        if (mListView_interests.isItemChecked(position)) {

            // Save the selected items to another array list
            mSelectedInterests.add(mInterests.get(position));

            // Save the array as a HashSet to preferences
            mSetInterests.addAll(mSelectedInterests);
            mPrefs.edit().putStringSet(PREFS_FILTER_INTERESTS, mSetInterests).apply();

        } else {

            // Remove the items that were unselected
            mSelectedInterests.remove(mInterests.get(position));

            // Save the array as a HashSet to preferences
            mSetInterests.addAll(mSelectedInterests);
            mPrefs.edit().putStringSet(PREFS_FILTER_INTERESTS, mSetInterests).apply();
        }
    }

    private void displaySelectedRows() {

        // Retrieve the saved HashSet that contains all the selected interests
        Set<String> mRetrievedSet = mPrefs.getStringSet(PREFS_FILTER_INTERESTS, new HashSet<String>());
        ArrayList<String> mSetInterests = new ArrayList<>(mRetrievedSet);

        if (mSetInterests.size() != 0) {

            // Loop through the HashSet and select the saved interests
            for (int i = 0; i < mInterests.size(); i++) {
                for (int y = 0; y < mSetInterests.size(); y++) {
                    if (mInterests.get(i).equals(mSetInterests.get(y))) {

                        // Check the item on the list
                        mListView_interests.setItemChecked(i, true);

                    }
                }
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        // Save the selected amount of miles to preferences whenever the user changes
        mPrefs.edit().putInt(PREFS_FILTER_MILES, progress).apply();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
