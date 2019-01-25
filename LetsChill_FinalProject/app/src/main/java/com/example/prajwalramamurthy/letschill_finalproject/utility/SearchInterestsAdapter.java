package com.example.prajwalramamurthy.letschill_finalproject.utility;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.prajwalramamurthy.letschill_finalproject.R;
import java.util.List;

public class SearchInterestsAdapter extends BaseAdapter {

    // Variable
    private static final long BASE_ID = 0x01011;
    private final Context mContext;
    private final List<String> mInterestsList;

    public SearchInterestsAdapter(Context mContext, List<String> mInterestsList) {
        this.mContext = mContext;
        this.mInterestsList = mInterestsList;
    }

    @Override
    public int getCount() {

        if(mInterestsList != null && mInterestsList.size() > 0){

            return mInterestsList.size();

        }

        return 0;
    }

    @Override
    public Object getItem(int position) {

        if(mInterestsList != null && position >= 0 || position < mInterestsList.size()){

            return mInterestsList.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return BASE_ID + position;
    }

    static class ViewHolder {

        final TextView mTextView_interests;

        public ViewHolder(View mLayout) {

            mTextView_interests = mLayout.findViewById(R.id.textView_interests_filter);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder mViewHolder;

        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_interests_filter,
                    parent, false);

            mViewHolder = new ViewHolder(convertView);
            convertView.setTag(mViewHolder);

        } else {

            mViewHolder = (ViewHolder)convertView.getTag();
        }

        final String interest = (String) getItem(position);

        if (interest != null && !interest.isEmpty()) {

            mViewHolder.mTextView_interests.setText(interest);
        }

        return convertView;
    }
}
