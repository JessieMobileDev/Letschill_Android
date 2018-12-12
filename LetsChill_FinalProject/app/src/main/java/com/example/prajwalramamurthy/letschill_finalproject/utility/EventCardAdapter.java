package com.example.prajwalramamurthy.letschill_finalproject.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.Event;

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
        final TextView mEventHost;


        public ViewHolder(View mLayout){

            mEventImage = mLayout.findViewById(R.id.card_imageView);
            mEventTitle = mLayout.findViewById(R.id.title_event);
            mEventTime = mLayout.findViewById(R.id.time_event);
            mEventLocation = mLayout.findViewById(R.id.location_event);
            mEventHost = mLayout.findViewById(R.id.host_name);

        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder mViewHolder;

        if (convertView == null){

            convertView = LayoutInflater.from(mContext).inflate(R.layout.baseadapter_eventcards, parent, false);

            mViewHolder = new ViewHolder(convertView);
            convertView.setTag(mViewHolder);

        }else{

            mViewHolder = (ViewHolder)convertView.getTag();
        }

        Event mEvent = (Event) getItem(position);

        if (mEvent != null){

//            // To round the edges of the image view (does not work)
//            Bitmap mBitmap = ((BitmapDrawable) this.mContext.getResources().getDrawable(R.drawable.create_back)).getBitmap();
//            Bitmap mImageRounded = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), mBitmap.getConfig());
//            Canvas mCanvas = new Canvas(mImageRounded);
//            Paint mPaint = new Paint();
//            mPaint.setAntiAlias(true);
//            mPaint.setShader(new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
//            mCanvas.drawRoundRect((new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight())), 100, 100, mPaint);

//            mViewHolder.mEventImage.setImageBitmap(mImageRounded);
            mViewHolder.mEventImage.setImageResource(R.drawable.create_back);
            mViewHolder.mEventTitle.setText(mEvent.getmEventName());
            mViewHolder.mEventTime.setText("From " + mEvent.getmEventTimeFinish() + " to " + mEvent.getmEventTimeFinish());
            mViewHolder.mEventLocation.setText(mEvent.getmEventLocation());
//            mViewHolder.mEventHost.setText("Hosted by " + mEvent.get);

        }

        return convertView;
    }
}
