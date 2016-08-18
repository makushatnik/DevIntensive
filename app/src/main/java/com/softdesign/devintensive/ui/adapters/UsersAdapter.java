package com.softdesign.devintensive.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.ui.views.AspectRatioImageView;
import com.softdesign.devintensive.utils.ConstantManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;

import java.util.Iterator;
import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Ageev Evgeny on 16.07.2016.
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    private static final String TAG = ConstantManager.TAG_PREFIX + "UsersAdapter";
    private Context mContext;
    private List<User> mUsers;
    private UserViewHolder.CustomClickListener mListener;

    private DataManager mDataManager;

    public UsersAdapter(List<User> users, UserViewHolder.CustomClickListener listener) {
        mUsers = users;
        //delete myself because app breaks
        mDataManager = DataManager.getInstance();
        String userId = mDataManager.getPreferencesManager().getUserId();
        if (!userId.equals("null")){
            Iterator<User> iter = users.iterator();
            while (iter.hasNext()) {
                User userData = iter.next();
                if (userId.equals(userData.getId())) {
                    mUsers.remove(userData);
                    break;
                }
            }
        }
        mListener = listener;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.item_user_list, parent, false);
        return new UserViewHolder(convertView, mListener);
    }

    @Override
    public void onBindViewHolder(final UserViewHolder holder, int position) {
        final User user = mUsers.get(position);
        final String userPhoto;
        if (user.getPhoto().isEmpty()) {
            userPhoto = "null";
            Log.e(TAG, "onBindViewHolder: user with name - " + user.getFullName() + " has empty name");
        } else {
            userPhoto = user.getPhoto();
        }

        mDataManager.getInstance().getPicasso()
                .load(userPhoto)
                .error(holder.mDummy)
                .placeholder(holder.mDummy)
                .fit()
                .centerCrop()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.mUserPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, " load from cache");
                    }

                    @Override
                    public void onError() {
                        mDataManager.getInstance().getPicasso()
                                .load(userPhoto)
                                .error(holder.mDummy)
                                .placeholder(holder.mDummy)
                                .fit()
                                .centerCrop()
                                .into(holder.mUserPhoto, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d(TAG, " load from cache");
                                    }

                                    @Override
                                    public void onError() {
                                        Log.d(TAG, " could not fetch image");
                                    }
                                });
                    }
                });

        holder.mFullName.setText(user.getFullName());
        holder.mRating.setText(String.valueOf(user.getRating()));
        holder.mCodeLines.setText(String.valueOf(user.getCodeLines()));
        holder.mProjects.setText(String.valueOf(user.getProjects()));

        String about = user.getBio();
        if (about == null && about.isEmpty()) {
            holder.mBio.setVisibility(View.GONE);
        } else {
            holder.mBio.setVisibility(View.VISIBLE);
            holder.mBio.setText(about);
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_photo) protected AspectRatioImageView mUserPhoto;
        @BindView(R.id.username_txt) protected TextView mFullName;
        @BindView(R.id.rating_txt) protected TextView mRating;
        @BindView(R.id.code_lines_txt) protected TextView mCodeLines;
        @BindView(R.id.projects_txt) protected TextView mProjects;
        @BindView(R.id.bio_txt) protected TextView mBio;
        @BindView(R.id.more_info_btn) protected Button mShowMore;
        @BindDrawable(R.drawable.user_bg) protected Drawable mDummy;

        private CustomClickListener mListener;

        public UserViewHolder(View itemView, CustomClickListener listener) {
            super(itemView);
            mListener = listener;

//            mUserPhoto = (AspectRatioImageView) itemView.findViewById(R.id.user_photo);
//            mDummy = mUserPhoto.getContext().getResources().getDrawable(R.drawable.login_bg);
//
//            mShowMore = (Button) itemView.findViewById(R.id.more_info_btn);
//            mShowMore.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.more_info_btn)
        public void onMoreClick(View view) {
            if (mListener != null) {
                mListener.onUserItemClickListener(getAdapterPosition());
            }
        }

        public interface CustomClickListener {
            void onUserItemClickListener(int pos);
        }
    }
}
