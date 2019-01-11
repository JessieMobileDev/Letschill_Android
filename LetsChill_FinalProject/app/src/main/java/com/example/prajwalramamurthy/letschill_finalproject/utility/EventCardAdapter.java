package com.example.prajwalramamurthy.letschill_finalproject.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

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


    // Optimize with view holder!
    static class ViewHolder{

        final ImageView mEventImage;
        final TextView mEventTitle;
        final TextView mEventTime;
        final TextView mEventLocation;
//        final TextView mEventHost;


        public ViewHolder(View mLayout){

            mEventImage = mLayout.findViewById(R.id.card_imageView);
            mEventTitle = mLayout.findViewById(R.id.title_event);
            mEventTime = mLayout.findViewById(R.id.time_event);
            mEventLocation = mLayout.findViewById(R.id.location_event);
//            mEventHost = mLayout.findViewById(R.id.host_name);

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

        Event mEvent = (Event) getItem(position);

        if (mEvent != null){

            if (mEvent.getmUrl() != null && !mEvent.getmUrl().isEmpty()) {

                // Variables
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
            }

            mViewHolder.mEventTitle.setText(mEvent.getmEventName());
            mViewHolder.mEventTime.setText("On " + mEvent.getmEventDate() + ", from " + mEvent.getmEventTimeStart() + " to " + mEvent.getmEventTimeFinish());
            mViewHolder.mEventLocation.setText(mEvent.getmEventLocation());
//            mViewHolder.mEventHost.setText("Hosted by " + mEvent.getmHost());


        }

        return convertView;
    }

//    private Bitmap downloadImage(String url) {
//
//        if (url != null && !url.isEmpty()) {
//
//            // Variables
//            StorageReference mStorageReference = mStorage.getReferenceFromUrl(url);
//            byte[] mImageBytes;
//            boolean didSucceed = false;
//
//            final long ONE_MEGABYTE = 1024 * 1024;
//            mStorageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                @Override
//                public void onSuccess(byte[] bytes) {
//
//                    didSucceed = true;
//                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    exception.printStackTrace();
//                }
//            });
//
//            if (didSucceed) {
//
//            }
//
//        }
//
//        return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.create_back);
//    }
}
