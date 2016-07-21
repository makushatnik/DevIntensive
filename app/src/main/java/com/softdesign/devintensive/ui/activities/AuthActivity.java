package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.managers.PreferencesManager;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.data.storage.models.Repository;
import com.softdesign.devintensive.data.storage.models.RepositoryDao;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.data.storage.models.UserDao;
import com.softdesign.devintensive.ui.adapters.UsersAdapter;
import com.softdesign.devintensive.utils.AppConfig;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.NetworkStatusChecker;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = ConstantManager.TAG_PREFIX + "AuthActivity";
    private Button mSignIn;
    private TextView mRememberPassword;
    private CheckBox mRemember;
    private EditText mLogin, mPassword;

    private DataManager mDataManager;
    private RepositoryDao mRepositoryDao;
    private UserDao mUserDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

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

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_container);
        mSignIn = (Button) findViewById(R.id.login_btn);
        mRemember = (CheckBox) findViewById(R.id.remember_cbx);
        mRememberPassword = (TextView) findViewById(R.id.remember_txt);
        mLogin = (EditText) findViewById(R.id.auth_email_et);
        mPassword = (EditText) findViewById(R.id.auth_password_et);

        mRememberPassword.setOnClickListener(this);
        mSignIn.setOnClickListener(this);

        mLogin.setText(login);
        mPassword.setText(pass);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                signIn();
                break;
            case R.id.remember_txt:
                rememberPassword();
                break;
        }
    }

    private void rememberPassword() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                "http://devintensive.softdesign-apps.ru/forgotpass"));
        startActivity(intent);
    }

    private void loginSuccess(UserModelRes.Data data) {
        if (data == null) return;

        showSnackbar(data.getToken());
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

    private void signIn() {
        if (NetworkStatusChecker.isNetworkAvailable(this)) {
            Call<UserModelRes> call = mDataManager.loginUser(new UserLoginReq(
                    mLogin.getText().toString().trim(),
                    mPassword.getText().toString().trim()));
            call.enqueue(new Callback<UserModelRes>() {
                @Override
                public void onResponse(Call<UserModelRes> call, Response<UserModelRes> response) {
                    if (response.code() == 200) {
                        loginSuccess(response.body().getData());
                    } else if (response.code() == 404) {
                        showSnackbar("Неверный логин или пароль");
                    } else {
                        showSnackbar("Все пропало, Шеф!!!");
                    }
                }

                @Override
                public void onFailure(Call<UserModelRes> call, Throwable t) {
                    showSnackbar(t.getMessage());
                    Log.e(TAG, t.getMessage());
                }
            });
        } else {
            showSnackbar("Сеть на данный момент не доступна, попробуйте позже");
        }
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
                        showSnackbar("Список пользователей не может быть получен!");
                        Log.e(TAG, " onResponse: " + String.valueOf(response.errorBody().source()));
                    }

                } catch (NullPointerException e) {
                    e.printStackTrace();
                    showSnackbar(e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<UserListRes> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                showSnackbar(t.getMessage());
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

