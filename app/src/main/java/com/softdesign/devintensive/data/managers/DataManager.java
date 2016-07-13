package com.softdesign.devintensive.data.managers;

import android.content.Context;

import com.softdesign.devintensive.data.network.RestService;
import com.softdesign.devintensive.data.network.ServiceGenerator;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.utils.DevIntensiveApplication;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by Ageev Evgeny on 27.06.2016.
 * Singleton
 */
public class DataManager {
    private static DataManager INSTANCE = null;

    private Context mContext;
    private RestService mRestService;
    private final PreferencesManager mPreferencesManager;

    private DataManager() {
        mPreferencesManager = new PreferencesManager();
        mContext = DevIntensiveApplication.getContext();
        mRestService = ServiceGenerator.createService(RestService.class);
    }

    public static DataManager getInstance() {
        if (INSTANCE == null) {
            synchronized (new Object()) {
                INSTANCE = new DataManager();
            }
        }
        return INSTANCE;
    }

    public PreferencesManager getPreferencesManager() {
        return mPreferencesManager;
    }

    public Context getContext() {
        return mContext;
    }

    //region ========== Network ==========

    public Call<UserModelRes> loginUser(UserLoginReq req) {
        return mRestService.loginUser(req);
    }

    public Call<UserModelRes> getUserInfo(String userId) { return mRestService.getUserInfo(userId); }

    public Call<ResponseBody> uploadPhoto(MultipartBody.Part photoFile, String userId) {
        return mRestService.uploadPhoto(photoFile, userId);
    }
    //end region
}
