package com.example.prajwalramamurthy.letschill_finalproject.utility;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventCardAdapter extends BaseAdapter {

    // Base ID
    private static final long BASE_ID = 0x01011;

    // Reference to our owning screen (context)
    private final Context mContext;

    // Reference to our collection
    private final ArrayList<Event> mEventList;



    // Constructor
    public EventCardAdapter(Context mContext, ArrayList<Event> mEventList){

        this.mContext = mContext;
        this.mEventList = mEventList;
    }

    // Get count
    @Override
    public int getCount(){


        if(mEventList != null && mEventList.size() > 0){

            return mEventList.size();

        }

        return 0;
    }

    // Item
    @Override
    public Object getItem(int position){

        if(mEventList != null && position >= 0 || position < mEventList.size()){

            return mEventList.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {

        return BASE_ID + position;

    }

    static class ViewHolder{

        final ImageView mEventImage;
        final TextView mEventTitle;
        final TextView mEventTime;
        final TextView mEventLocation;
        final TextView mJoinedPeople;
        final TextView mRecurring;
        final ImageView mRecurringBackground;


        public ViewHolder(View mLayout){

            mEventImage = mLayout.findViewById(R.id.card_imageView);
            mEventTitle = mLayout.findViewById(R.id.title_event);
            mEventTime = mLayout.findViewById(R.id.time_event);
            mEventLocation = mLayout.findViewById(R.id.location_event);
            mJoinedPeople = mLayout.findViewById(R.id.textView_joinedCount);
            mRecurring = mLayout.findViewById(R.id.textView_recurring);
            mRecurringBackground = mLayout.findViewById(R.id.imageView_recurringBackground);

        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder mViewHolder;

        if (convertView == null){

            convertView = LayoutInflater.from(mContext).inflate(R.layout.baseadapter_eventcards, parent, false);

            mViewHolder = new ViewHolder(convertView);
            convertView.setTag(mViewHolder);

        }else{

            mViewHolder = (ViewHolder)convertView.getTag();
        }

        final Event mEvent = (Event) getItem(position);

        if (mEvent != null){

            if (mEvent.getmUrl() != null && !mEvent.getmUrl().isEmpty()) {

                // Variables
                final boolean imageFailedToDownload = false;
                FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
                StorageReference mStorageReference = mFirebaseStorage.getReference().child(mEvent.getmUrl());
                final long ONE_MEGABYTE = 1024 * 1024;

                mStorageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {

                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        mViewHolder.mEventImage.setImageBitmap(bmp);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        exception.printStackTrace();
                    }
                });

                if (!ConnectionHandler.isConnected(mContext)) {

                    ImageDownloadHandler.showDefaultImagesIfOffline(mEvent.getmCategory(), mViewHolder.mEventImage);
                }
            }

            String dateString = "";
            try {

                // Dates saved in the database are numbers, change it to words such as January 11, 2019
                DateFormat format1 = new SimpleDateFormat("MM-dd-yyyy");
                Date date = format1.parse(mEvent.getmEventDate());
                DateFormat format2 = new SimpleDateFormat("MMMM dd, yyyy");
                dateString = format2.format(date);

            } catch (Exception e) {
                e.printStackTrace();
            }

            mViewHolder.mEventTitle.setText(mEvent.getmEventName());
            mViewHolder.mEventTime.setText(dateString + ", from " + mEvent.getmEventTimeStart() + " to " + mEvent.getmEventTimeFinish());
            mViewHolder.mEventLocation.setText(mEvent.getmEventLocation());

//            if (mEvent.getmJoinedPeopleIds() != null) {
                if (mEvent.getmJoinedPeopleIds().size() != 0) {

                    mViewHolder.mJoinedPeople.setText(String.valueOf(mEvent.getmJoinedPeopleIds().size()) + "/" + mEvent.getmParticipants() + " joined");
                    Log.d("test", "getView: it's not null and it's not zero");
                } else if (mEvent.getmJoinedPeopleIds() == null || mEvent.getmJoinedPeopleIds().size() == 0) {
                    mViewHolder.mJoinedPeople.setText("0/" + mEvent.getmParticipants());
                    Log.d("test", "getView: it's not null and it's zero");
                }
//            } else {
//
//                Log.d("test", "getView: joined array is null");
//            }

            if (ConnectionHandler.isConnected(mContext)) {
                mViewHolder.mEventLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String query = "geo:0,0?q=" + mEvent.getmEventLocation();
                        Uri gmmIntentUri = Uri.parse(query);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        mContext.startActivity(mapIntent);
                    }
                });
            }

            if (!mEvent.ismIsRecurringEvent()) {


                mViewHolder.mRecurring.setVisibility(View.GONE);
                mViewHolder.mRecurringBackground.setVisibility(View.GONE);
            }
        }

        return convertView;
    }
}
