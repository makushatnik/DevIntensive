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

    public PreferencesManager() {
        mSharedPreferences = DevIntensiveApplication.getSharedPreferences();
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

    public Uri loadUserPhoto() {
        return Uri.parse(mSharedPreferences.getString(ConstantManager.USER_PHOTO_KEY,
                "android.resource://com.softdesign.devintensive/drawable/userphoto_3"));
    }
}
