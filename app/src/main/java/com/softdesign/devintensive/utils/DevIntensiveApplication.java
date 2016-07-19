package com.softdesign.devintensive.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Ageev Evgeny on 26.06.2016.
 */
public class DevIntensiveApplication extends Application {
    private static DevIntensiveApplication sContext;
    private static SharedPreferences sSharedPreferences;

    public static SharedPreferences getSharedPreferences() {
        return sSharedPreferences;
    }

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = this;
        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }
}
