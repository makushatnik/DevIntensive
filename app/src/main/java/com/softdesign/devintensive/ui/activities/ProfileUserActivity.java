package com.softdesign.devintensive.ui.activities;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.ui.adapters.RepositoriesAdapter;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.UIHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class ProfileUserActivity extends BaseActivity {
    private static final String TAG = ConstantManager.TAG_PREFIX + "Profile";

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.user_photo_img) ImageView mProfileImage;

    @BindView(R.id.bio_txt) TextView mUserBio;
    @BindView(R.id.rating_txt) TextView mRating;
    @BindView(R.id.code_lines_txt) TextView mCodeLines;
    @BindView(R.id.projects_txt) TextView mProjects;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.main_coordinator) CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.repo_list) ListView mRepoListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        ButterKnife.bind(this);

        setupToolbar();
        initProfileData();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initProfileData() {
        UserDTO userDTO = getIntent().getParcelableExtra(ConstantManager.PARCELABLE_KEY);

        final List<String> repositories = userDTO.getRepositories();
        final RepositoriesAdapter repositoriesAdapter = new RepositoriesAdapter(this, repositories);
        mRepoListView.setAdapter(repositoriesAdapter);

        mUserBio.setText(userDTO.getBio());
        mRating.setText(userDTO.getRating());
        mCodeLines.setText(userDTO.getCodeLines());
        mProjects.setText(userDTO.getProjects());

        mCollapsingToolbarLayout.setTitle(userDTO.getFullName());

        Picasso.with(this)
                .load(userDTO.getPhoto())
                .placeholder(R.drawable.user_bg)
                .error(R.drawable.user_bg)
                .into(mProfileImage);
    }

    @OnItemClick(R.id.repo_list)
    public void onItemClick(int pos) {
        openGithub(mRepoListView.getAdapter().getItem(pos).toString());
    }

    public void openGithub(String repoAddr) {
        Log.d(TAG, "repo = " + repoAddr);
        if (repoAddr == null || repoAddr.isEmpty()) return;
        showSnackbar(mCoordinatorLayout, "Репозиторий " + repoAddr);

        UIHelper.openRepo(this, repoAddr);
    }
}
