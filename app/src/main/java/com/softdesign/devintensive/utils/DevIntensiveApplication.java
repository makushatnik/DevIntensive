package com.softdesign.devintensive.utils;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Ageev Evgeny on 26.06.2016.
 */
public class DevIntensiveApplication extends Application {
    private static final DevIntensiveApplication INSTANCE = new DevIntensiveApplication();
    private static SharedPreferences sSharedPreferences;

    public DevIntensiveApplication() {}

    public static SharedPreferences getSharedPreferences() {
        return sSharedPreferences;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public static DevIntensiveApplication getContext() {
        return INSTANCE;
    }
}
