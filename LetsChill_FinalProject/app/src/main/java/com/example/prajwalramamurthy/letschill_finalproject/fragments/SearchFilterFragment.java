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
import com.example.prajwalramamurthy.letschill_finalproject.utility.SearchInterestsAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchFilterFragment extends Fragment implements View.OnClickListener,
        ListView.OnItemClickListener, SeekBar.OnSeekBarChangeListener {

    // Variables
    private RadioButton mRButton_title, mRButton_city, mRButton_interests;
    private SeekBar mSeekBar_miles;
    private TextView mTextView_radiusTitle, mTextView_interestsTitle;
    private ListView mListView_interests;
    private SharedPreferences mPrefs;
    private List<String> mInterests;
    private SearchInterestsAdapter mAdapter;

    // Constants
    public static final String PREFS_FILTER_SELECTION = "PREFS_FILTER_SELECTION";

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

            // Convert the xml list of strings into an array list
            mInterests = Arrays.asList(getResources().getStringArray(R.array.spinner_category));

            // Set the adapter to the listview
            mAdapter = new SearchInterestsAdapter(getContext(), mInterests);
            mListView_interests.setAdapter(mAdapter);

            // Instantiate the SharedPreferences
            mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

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

                break;
            case R.id.radioButton_search_city:

                // Hide the interests text view and listview
                mTextView_interestsTitle.setVisibility(View.GONE);
                mListView_interests.setVisibility(View.GONE);

                // Save selection to user defaults
                mPrefs.edit().putString(PREFS_FILTER_SELECTION, "city").apply();

                break;
            case R.id.radioButton_search_interests:

                // Display the interests text view and listview
                mTextView_interestsTitle.setVisibility(View.VISIBLE);
                mListView_interests.setVisibility(View.VISIBLE);

                // Save selection to user defaults
                mPrefs.edit().putString(PREFS_FILTER_SELECTION, "interests").apply();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
