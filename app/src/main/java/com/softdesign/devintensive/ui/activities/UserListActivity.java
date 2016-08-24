package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.ui.adapters.UsersAdapter;
import com.softdesign.devintensive.utils.AppConfig;
import com.softdesign.devintensive.utils.ConstantManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class UserListActivity extends BaseActivity {
    private static final String TAG = ConstantManager.TAG_PREFIX + "UserListActivity";

    @BindView(R.id.main_coordinator)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.navigation_drawer)
    DrawerLayout mNavigationDrawer;
    @BindView(R.id.user_list)
    RecyclerView mRecyclerView;

    private DataManager mDataManager;
    private UsersAdapter mUsersAdapter;
    private List<User> mUsers;

    private MenuItem mSearchItem;
    private String mQuery;
    private int mOrder;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        mDataManager = DataManager.getInstance();

        ButterKnife.bind(this);

        mHandler = new Handler();

        //GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        setupToolbar();
        //setupDrawer();
        loadUsersFromDb();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        } else if (itemId == R.id.lines_action) {
            mOrder = ConstantManager.ORDER_BY_CODELINES;
            showUsersByQuery(mQuery);
        } else if (itemId == R.id.rating_action) {
            mOrder = ConstantManager.ORDER_BY_RATING;
            showUsersByQuery(mQuery);
        } else if (itemId == R.id.projects_action) {
            mOrder = ConstantManager.ORDER_BY_PROJECTS;
            showUsersByQuery(mQuery);
        } else if (itemId == R.id.likes_action) {
            mOrder = ConstantManager.ORDER_BY_LIKES;
            showUsersByQuery(mQuery);
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadUsersFromDb() {
        //List<User> users = mDataManager.getUserListFromDb();
        List<User> users = mDataManager.getUserListByOrder("", 0);
        if (users.size() == 0) {
            showSnackbar(mCoordinatorLayout, "Список пользователей не может быть загружен");
        } else {
            showUsers(users);
        }
    }

//    private void setupDrawer() {
//        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(MenuItem item) {
//                Intent profileIntent = new Intent(UserListActivity.this, MainActivity.class);
//                startActivity(profileIntent);
//                finish();
//                return false;
//            }
//        });
//    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        mSearchItem = menu.findItem(R.id.search_action);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        searchView.setQueryHint(getString(R.string.search_query_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mQuery = newText;
                showUsersByQuery(newText);
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                //showUsers(mDataManager.getUserListFromDb());
                mQuery = "";
                showUsers(mDataManager.getUserListByOrder("", mOrder));
                return false;
            }
        });
        return true;
        //return super.onPrepareOptionsMenu(menu);
    }

    private void showUsers(List<User> users) {
        mUsers = users;
        mUsersAdapter = new UsersAdapter(mUsers, new UsersAdapter.UserViewHolder.CustomClickListener() {
            @Override
            public void onUserItemClickListener(int pos) {
                UserDTO userDTO = new UserDTO(mUsers.get(pos));

                Intent profileIntent = new Intent(UserListActivity.this, ProfileUserActivity.class);
                profileIntent.putExtra(ConstantManager.PARCELABLE_KEY, userDTO);
                startActivity(profileIntent);
            }
        },
                new UsersAdapter.UserViewHolder.LikeClickListener() {
                    @Override
                    public void onLikeClickListener(final int pos) {
                        showToast("Not implemented yet!");
                        Log.d(TAG, "Position = " + pos);
//                        final User user = mUsers.get(pos);
//                        Log.d(TAG, "user = " + user.getId() + ", name " + user.getFullName() + ", remote - " + user.getRemoteId());
//                        final int numLikes = user.getLikeRating();
//                        final boolean up = UIHelper.isFirstLike(user);
//                        Call<LikeRes> call = mDataManager.likeUser(String.valueOf(user.getId()));
//                        call.enqueue(new Callback<LikeRes>() {
//                            @Override
//                            public void onResponse(Call<LikeRes> call, Response<LikeRes> response) {
//                                if (response.code() == 200) {
//                                    View view = mRecyclerView.getChildAt(pos);
//                                    TextView tv = (TextView) view.findViewById(R.id.numOfLikes);
//                                    ImageView iv = (ImageView) view.findViewById(R.id.like_btn);
//
//                                    tv.setText(user.getLikeRating());
//                                    if (up) {
//                                        user.setLikeRating(user.getLikeRating() + 1);
//                                        iv.setImageResource(R.drawable.ic_thumb_down_black_24dp);
//                                    } else {
//                                        user.setLikeRating(user.getLikeRating() - 1);
//                                        iv.setImageResource(R.drawable.ic_thumb_up_black_24dp);
//                                    }
//                                } else {
//                                    showSnackbar(mCoordinatorLayout, "Ошибка сервера");
//                                    Log.e(TAG, " onResponse: " + String.valueOf(response.errorBody().source()));
//                                }
//                            }
//                            @Override
//                            public void onFailure(Call<LikeRes> call, Throwable t) {
//                                Log.d(TAG, "Like user failure - " + t.getMessage());
//                                showSnackbar(mCoordinatorLayout, t.getMessage());
//                            }
//                        });
                    }
                });
        mRecyclerView.swapAdapter(mUsersAdapter, false);
    }

    private void showUsersByQuery(final String query) {
        mQuery = query;

        Runnable searchUsers = new Runnable() {
            @Override
            public void run() {
                //showUsers(mDataManager.getUserListByName(query));
                showUsers(mDataManager.getUserListByOrder(query, mOrder));
            }
        };

        mHandler.removeCallbacks(searchUsers);
        mHandler.postDelayed(searchUsers, AppConfig.SEARCH_DELAY);
    }
}
