package com.softdesign.devintensive.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.ui.views.AspectRatioImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ageev Evgeny on 16.07.2016.
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    private Context mContext;
    private List<UserListRes.UserData> mUsers;
    private UserViewHolder.CustomClickListener mListener;

    public UsersAdapter(List<UserListRes.UserData> users, UserViewHolder.CustomClickListener listener) {
        mUsers = users;
        mListener = listener;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list, parent, false);
        return new UserViewHolder(convertView, mListener);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        UserListRes.UserData user = mUsers.get(position);

        Picasso.with(mContext)
                .load(user.getPublicInfo().getPhoto())
                .placeholder(mContext.getResources().getDrawable(R.drawable.login_bg))
                .error(mContext.getResources().getDrawable(R.drawable.login_bg))
                .into(holder.userPhoto);

        holder.mFullName.setText(user.getFullName());
        holder.mRating.setText(String.valueOf(user.getProfileValues().getRaiting()));
        holder.mCodeLines.setText(String.valueOf(user.getProfileValues().getLinesCode()));
        holder.mProjects.setText(String.valueOf(user.getProfileValues().getProjects()));

        String about = user.getPublicInfo().getBio();
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

    public static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected AspectRatioImageView userPhoto;
        protected TextView mFullName, mRating, mCodeLines, mProjects, mBio;
        protected Button mShowMore;

        private CustomClickListener mListener;

        public UserViewHolder(View itemView, CustomClickListener listener) {
            super(itemView);
            mListener = listener;

            userPhoto = (AspectRatioImageView) itemView.findViewById(R.id.user_photo);
            mFullName = (TextView) itemView.findViewById(R.id.username_txt);
            mRating = (TextView) itemView.findViewById(R.id.rating_txt);
            mCodeLines = (TextView) itemView.findViewById(R.id.code_lines_txt);
            mProjects = (TextView) itemView.findViewById(R.id.projects_txt);
            mBio = (TextView) itemView.findViewById(R.id.bio_txt);
            mShowMore = (Button) itemView.findViewById(R.id.more_info_btn);


        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemClickListener(getAdapterPosition());
            }
        }

        public interface CustomClickListener {
            void onItemClickListener(int pos);
        }
    }
}
