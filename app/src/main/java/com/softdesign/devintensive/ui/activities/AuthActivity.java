package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.utils.NetworkStatusChecker;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends BaseActivity implements View.OnClickListener {
    private Button mSignIn;
    private TextView mRememberPassword;
    private EditText mLogin, mPassword;

    private DataManager mDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mDataManager = DataManager.getInstance();

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_container);
        mSignIn = (Button) findViewById(R.id.login_btn);
        mRememberPassword = (TextView) findViewById(R.id.remember_txt);
        mLogin = (EditText) findViewById(R.id.auth_email_et);
        mPassword = (EditText) findViewById(R.id.auth_password_et);

        mRememberPassword.setOnClickListener(this);
        mSignIn.setOnClickListener(this);

        String[] authInfo = mDataManager.getPreferencesManager().getAuthInfo();
        if (authInfo.length == 2 && authInfo[0] != null && authInfo[1] != null) {
            mLogin.setText(authInfo[0]);
            mPassword.setText(authInfo[1]);
        }
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
        showSnackbar(data.getToken());
        mDataManager.getPreferencesManager().saveAuthToken(data.getToken());
        mDataManager.getPreferencesManager().saveUserId(data.getUser().getId());
        saveUserInvoValue(data);

        mDataManager.getPreferencesManager().saveAuthInfo(new String[] {
                mLogin.getText().toString().trim(),
                mPassword.getText().toString().trim()
        });

        Intent loginIntent = new Intent(this, MainActivity.class);
        startActivity(loginIntent);
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
                    //TODO Обработать ошибки
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
        mDataManager.getPreferencesManager().saveUserProfileValues(userValues);
    }
}

