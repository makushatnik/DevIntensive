package com.softdesign.devintensive.data.managers;

import android.content.Context;

import com.softdesign.devintensive.data.network.PicassoCache;
import com.softdesign.devintensive.data.network.RestService;
import com.softdesign.devintensive.data.network.ServiceGenerator;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UploadPhotoRes;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.data.storage.models.DaoSession;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDao;
import com.softdesign.devintensive.utils.DevIntensiveApplication;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;

/**
 * Created by Ageev Evgeny on 27.06.2016.
 * Singleton
 */
//@EBean
public class DataManager {
    private static DataManager INSTANCE = null;
    private Picasso mPicasso;

    private Context mContext;
    private RestService mRestService;
    private final PreferencesManager mPreferencesManager;

    private DaoSession mDaoSession;

    private DataManager() {
        mPreferencesManager = new PreferencesManager();
        mContext = DevIntensiveApplication.getContext();
        mRestService = ServiceGenerator.createService(RestService.class);
        mPicasso = new PicassoCache(mContext).getPicassoInstance();
        mDaoSession = DevIntensiveApplication.getDaoSession();
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

    public Context getContext() { return mContext; }

    public Picasso getPicasso() {
        return mPicasso;
    }

    //region ========== Network ==========

    public Call<UserModelRes> loginUser(UserLoginReq req) {
        return mRestService.loginUser(req);
    }

    public Call<UploadPhotoRes> uploadPhoto(String userId, MultipartBody.Part photoFile) {
        return mRestService.uploadPhoto(userId, photoFile);
    }

    public Call<UserListRes> getUserList() {
        return mRestService.getUserList();
    }

    public Call<UserListRes> getUserListFromNetwork() {
        return mRestService.getUserList();
    }

    //end region

    //region =========== Database ===========

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public List<User> getUserListFromDb() {
        List<User> userList = new ArrayList<>();
        try {
            userList = mDaoSession.queryBuilder(User.class)
                    .where(UserDao.Properties.CodeLines.gt(0))
                    .orderDesc(UserDao.Properties.CodeLines)
                    .build()
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userList;
    }

    public List<User> getUserListByName(String queryName) {
        List<User> userList = new ArrayList<>();
        try {
            userList = mDaoSession.queryBuilder(User.class)
                        .where(UserDao.Properties.Rating.gt(0),
                                UserDao.Properties.SearchName.like("%" + queryName.toUpperCase() + "%"))
                        .orderDesc(UserDao.Properties.CodeLines)
                        .build()
                        .list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;
    }

    //endregion
}
