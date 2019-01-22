package com.example.prajwalramamurthy.letschill_finalproject.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;
import com.example.prajwalramamurthy.letschill_finalproject.utility.EventCardAdapter;
import com.example.prajwalramamurthy.letschill_finalproject.utility.MainPageAdapter;
import java.util.ArrayList;

public class TabListViewFragment extends ListFragment implements SearchView.OnQueryTextListener {

    // Variables
    private ArrayList<Event> mAllEvents = new ArrayList<>();
    private TabTodayInterface mTabListViewInterface;
    private EventCardAdapter mAdapter;

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (mAdapter != null && !newText.isEmpty()) {
            mAdapter.getFilter().filter(newText);
            mAdapter.notifyDataSetChanged();
        }
        return false;
    }

    public interface TabTodayInterface {

        void openDetailsPageFromTodayTab(Event mEvent);
    }

    public static TabListViewFragment newInstance() {
        
        Bundle args = new Bundle();
        
        TabListViewFragment fragment = new TabListViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof TabTodayInterface) {

            mTabListViewInterface = (TabTodayInterface)context;
        }
    }
    

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_tab_today, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAllEvents = (ArrayList<Event>) getArguments().getSerializable(MainPageAdapter.ARGS_ALL_EVENTS);

        if (mAllEvents != null) {

            // Adapter that will populate the list view
            mAdapter = new EventCardAdapter(getContext(), mAllEvents);
            setListAdapter(mAdapter);


            getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // Pass the selected event object to the "DetailsEventActivity"
                    mTabListViewInterface.openDetailsPageFromTodayTab(mAllEvents.get(position));

                }
            });

            mAdapter.notifyDataSetChanged();
        }
    }
}
