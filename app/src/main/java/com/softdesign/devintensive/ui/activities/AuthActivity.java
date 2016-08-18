package com.softdesign.devintensive.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.managers.PreferencesManager;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.data.storage.models.Repository;
import com.softdesign.devintensive.data.storage.models.RepositoryDao;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDao;
import com.softdesign.devintensive.utils.AppConfig;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.NetworkStatusChecker;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends BaseActivity {
    private static final String TAG = ConstantManager.TAG_PREFIX + "AuthActivity";

    @BindView(R.id.main_coordinator_container) CoordinatorLayout mCoordinatorLayout;
    //@ViewById(R.id.login_btn) Button mSignIn;
    //@ViewById(R.id.remember_txt) TextView mRememberPassword;
    @BindView(R.id.remember_cbx) CheckBox mRemember;
    @BindView(R.id.auth_email_et) EditText mLogin;
    @BindView(R.id.auth_password_et) EditText mPassword;

    //@Bean
    private DataManager mDataManager;
    //@OrmLiteDao
    private RepositoryDao mRepositoryDao;
    //@OrmLiteDao
    private UserDao mUserDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);

        mDataManager = DataManager.getInstance();
        mUserDao = mDataManager.getDaoSession().getUserDao();
        mRepositoryDao = mDataManager.getDaoSession().getRepositoryDao();

        String login = "";
        String pass = "";
        String[] authInfo = mDataManager.getPreferencesManager().getAuthInfo();
        if (authInfo.length == 2 && !authInfo[0].equals("null") && !authInfo[1].equals("null")) {
            login = authInfo[0];
            pass  = authInfo[1];
            //Start Activity
            runHandler();
        }

        if (mLogin != null && mPassword != null) {
            mLogin.setText(login);
            mPassword.setText(pass);
        }
    }

    @OnClick(R.id.remember_txt)
    public void rememberPassword() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                "http://devintensive.softdesign-apps.ru/forgotpass"));
        startActivity(intent);
    }

    private void loginSuccess(UserModelRes.Data data) {
        if (data == null) return;

        showSnackbar(mCoordinatorLayout, data.getToken());
        mDataManager.getPreferencesManager().saveAuthToken(data.getToken());
        mDataManager.getPreferencesManager().saveUserId(data.getUser().getId());
        saveUserInvoValue(data);
        saveUserProfileValue(data);
        saveUserInDb();

        if (mRemember.isChecked()) {
            mDataManager.getPreferencesManager().saveAuthInfo(new String[]{
                    mLogin.getText().toString().trim(),
                    mPassword.getText().toString().trim()
            });
        }

        runHandler();
        hideProgress();
    }

    private void runHandler() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent loginIntent = new Intent(AuthActivity.this, MainActivity.class);
                startActivity(loginIntent);
            }
        }, AppConfig.START_DELAY);
    }

    @OnClick(R.id.login_btn)
    public void signIn() {
        Log.d(TAG, "Great! It's worked!");
        Log.d(TAG, "Login = " + mLogin.getText().toString().trim());
        if (NetworkStatusChecker.isNetworkAvailable(this)) {
            showProgress();
            Call<UserModelRes> call = mDataManager.loginUser(new UserLoginReq(
                    mLogin.getText().toString().trim(),
                    mPassword.getText().toString().trim()));
            call.enqueue(new Callback<UserModelRes>() {
                @Override
                public void onResponse(Call<UserModelRes> call, Response<UserModelRes> response) {
                    if (response.code() == 200) {
                        loginSuccess(response.body().getData());
                    } else if (response.code() == 404) {
                        onLoginError();
                    } else {
                        showSnackbar(mCoordinatorLayout, "Все пропало, Шеф!!!");
                    }
                }

                @Override
                public void onFailure(Call<UserModelRes> call, Throwable t) {
                    showSnackbar(mCoordinatorLayout, t.getMessage());
                    Log.e(TAG, "Login failure - " + t.getMessage());
                }
            });
        } else {
            showSnackbar(mCoordinatorLayout, "Сеть на данный момент не доступна, попробуйте позже");
        }
    }

    private void onLoginError() {
        showSnackbar(mCoordinatorLayout, "Неверный логин или пароль");
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(AppConfig.ERROR_VIBRATE_TIME);
    }

    private void saveUserInvoValue(UserModelRes.Data data) {
        UserModelRes.ProfileValues prof = data.getUser().getProfileValues();
        int[] userValues = {
                prof.getRaiting(),
                prof.getLinesCode(),
                prof.getProjects()
        };
        Log.d(TAG, "RATING - " + prof.getRaiting());
        Log.d(TAG, "LINES CODE - " + prof.getLinesCode());
        Log.d(TAG, "PROJECTS - " + prof.getProjects());
        mDataManager.getPreferencesManager().saveUserProfileValues(userValues);
    }

    private void saveUserProfileValue(UserModelRes.Data data) {
        UserModelRes.User user = data.getUser();
        PreferencesManager pref = mDataManager.getPreferencesManager();
        pref.saveFullName(user.getFullName());

        List<String> userValues = new ArrayList<>();
        userValues.add(user.getContacts().getPhone());
        userValues.add(user.getContacts().getEmail());
        userValues.add(user.getContacts().getVk());
        userValues.add(user.getRepositories().getRepo().get(0).getGit());
        //why commented? - it's empty now!
        //userValues.add(user.getPublicInfo().getBio());
        userValues.add("null");
        pref.saveUserProfileData(userValues);

        pref.saveUserPhoto(user.getPublicInfo().getPhoto());
    }

    private void saveUserInDb() {
        Call<UserListRes> call = mDataManager.getUserListFromNetwork();

        call.enqueue(new Callback<UserListRes>() {
            @Override
            public void onResponse(Call<UserListRes> call, Response<UserListRes> response) {
                try {
                    if (response.code() == 200) {
                        List<Repository> allRepositories = new ArrayList<>();
                        List<User> allUsers = new ArrayList<>();

                        for (UserListRes.UserData userRes : response.body().getData()) {
                            allRepositories.addAll(getRepoListFromUserRes(userRes));
                            allUsers.add(new User(userRes));
                        }

                        mRepositoryDao.insertOrReplaceInTx(allRepositories);
                        mUserDao.insertOrReplaceInTx(allUsers);
                    } else {
                        showSnackbar(mCoordinatorLayout, "Список пользователей не может быть получен!");
                        Log.e(TAG, " onResponse: " + String.valueOf(response.errorBody().source()));
                    }

                } catch (NullPointerException e) {
                    showError(TAG, e.getMessage(), e);
                }
            }

            @Override
            public void onFailure(Call<UserListRes> call, Throwable t) {
                Log.d(TAG, "Save users failure - " + t.getMessage());
                showSnackbar(mCoordinatorLayout, t.getMessage());
            }
        });
    }

    private List<Repository> getRepoListFromUserRes(UserListRes.UserData userData) {
        final String userId = userData.getId();

        List<Repository> repositories = new ArrayList<>();
        for (UserModelRes.Repo curRepo : userData.getRepositories().getRepo()) {
            repositories.add(new Repository(curRepo, userId));
        }

        return repositories;
    }
}
