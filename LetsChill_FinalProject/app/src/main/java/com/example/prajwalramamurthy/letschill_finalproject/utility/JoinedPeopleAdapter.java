package com.example.prajwalramamurthy.letschill_finalproject.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.prajwalramamurthy.letschill_finalproject.R;
import com.example.prajwalramamurthy.letschill_finalproject.data_model.User;
import java.util.ArrayList;

public class JoinedPeopleAdapter extends BaseAdapter {

    // Variables
    private static final long BASE_ID = 0x01011;
    private final Context mContext;
    private ArrayList<User> mJoinedUsers;

    public JoinedPeopleAdapter(Context mContext, ArrayList<User> mJoinedUsers) {
        this.mContext = mContext;
        this.mJoinedUsers = mJoinedUsers;
    }

    @Override
    public int getCount() {

        if (mJoinedUsers != null && mJoinedUsers.size() > 0) {

            return mJoinedUsers.size();
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {

        if (mJoinedUsers != null && position >= 0 || position < mJoinedUsers.size()) {

            return mJoinedUsers.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return BASE_ID + position;
    }

    static class ViewHolder {

        TextView mTextView_username;

        public ViewHolder(View mLayout) {

            mTextView_username = mLayout.findViewById(R.id.textView_joined_people_adapter);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder mViewHolder;

        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_joined_users, parent, false);

            mViewHolder = new ViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {

            mViewHolder = (ViewHolder) convertView.getTag();
        }

        User mUser = (User) getItem(position);

        if (mUser != null) {

            if (mUser.getUsername() != null && !mUser.getUsername().isEmpty()) {

                // Set the name to the text view in the view holder
                mViewHolder.mTextView_username.setText(mUser.getUsername());
            }
        }
        return convertView;
    }
}
