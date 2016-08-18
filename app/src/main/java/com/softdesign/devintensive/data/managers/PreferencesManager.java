package com.softdesign.devintensive.data.managers;

import android.content.SharedPreferences;
import android.net.Uri;

import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.DevIntensiveApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ageev Evgeny on 26.06.2016.
 */
public class PreferencesManager {
    private SharedPreferences mSharedPreferences;

    private static final String[] USER_FIELDS = {
            ConstantManager.USER_PHONE_CODE,
            ConstantManager.USER_MAIL_CODE,
            ConstantManager.USER_VK_CODE,
            ConstantManager.USER_GIT_CODE,
            ConstantManager.USER_ABOUT_CODE
    };

    private static final String[] USER_VALUES = {
            ConstantManager.USER_RATING,
            ConstantManager.USER_LINES_CODE,
            ConstantManager.USER_PROJECTS
    };

    public PreferencesManager() {
        mSharedPreferences = DevIntensiveApplication.getSharedPreferences();
    }

    //region ::::::::::: Settings :::::::::::::
    public boolean checkFields() {
        return mSharedPreferences.getBoolean(ConstantManager.SETTING_CHECK_FIELD, true);
    }
    //end region

    public void saveFullName(String fullName) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_FULL_NAME, fullName);
        editor.apply();
    }

    public String getFullName() {
        return mSharedPreferences.getString(ConstantManager.USER_FULL_NAME, "null");
    }

    public void saveUserProfileData(List<String> userFields) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        for (int i=0; i < userFields.size(); i++) {
            editor.putString(USER_FIELDS[i], userFields.get(i));
        }
        editor.apply();
    }

    public List<String> loadUserProfileData() {
        List<String> userFields = new ArrayList<>();
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_PHONE_CODE, "null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_MAIL_CODE, "null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_VK_CODE, "null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_GIT_CODE, "null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_ABOUT_CODE, "null"));
        return userFields;
    }

    public void saveUserPhoto(Uri uri) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_PHOTO_KEY, uri.toString());
        editor.apply();
    }

    public void saveUserPhoto(String path) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_PHOTO_KEY, path);
        editor.apply();
    }

    public Uri loadUserPhoto() {
        return Uri.parse(mSharedPreferences.getString(ConstantManager.USER_PHOTO_KEY,
                "android.resource://com.softdesign.devintensive/drawable/userphoto_3"));
    }

    public boolean isSendPhotoEnabled() {
        return  mSharedPreferences.getBoolean("send_photo", false);
    }

    public void saveUserProfileValues(int[] userValues) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (int i=0; i < userValues.length; i++) {
            editor.putString(USER_VALUES[i], String.valueOf(userValues[i]));
        }
        editor.apply();
    }

    public List<String> loadUserProfileValues() {
        List<String> userValues = new ArrayList<>();
        userValues.add(mSharedPreferences.getString(ConstantManager.USER_RATING, "0"));
        userValues.add(mSharedPreferences.getString(ConstantManager.USER_LINES_CODE, "0"));
        userValues.add(mSharedPreferences.getString(ConstantManager.USER_PROJECTS, "0"));
        return userValues;
    }

    public void saveAuthToken(String authToken) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.AUTH_TOKEN, authToken);
        editor.apply();
    }

    public String getAuthToken() {
        return mSharedPreferences.getString(ConstantManager.AUTH_TOKEN, "null");
    }

    public void saveUserId(String userId) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_ID_KEY, userId);
        editor.apply();
    }

    public String getUserId() {
        return mSharedPreferences.getString(ConstantManager.USER_ID_KEY, "null");
    }

    public void saveAuthInfo(String[] info) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_MAIL_CODE, info[0]);
        editor.putString(ConstantManager.USER_PASS_CODE, md5_encode(info[1]));
        editor.apply();
    }

    public String[] getAuthInfo() {
        String[] info = new String[2];
        info[0] = mSharedPreferences.getString(ConstantManager.USER_MAIL_CODE, "null");
        info[1] = md5_decode(mSharedPreferences.getString(ConstantManager.USER_PASS_CODE, "null"));
        return info;
    }

    public void deleteAuth() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_MAIL_CODE, "null");
        editor.putString(ConstantManager.USER_PASS_CODE, "null");
        editor.putString(ConstantManager.USER_ID_KEY, "null");
        editor.putString(ConstantManager.AUTH_TOKEN, "null");
        editor.apply();
    }

    private String md5_encode(String p) {
        //some operations
        return p;
    }

    private String md5_decode(String p) {
        if (p.equals("null")) return p;
        else {

        }
        return p;
    }
}
