package com.softdesign.devintensive.data.managers;

/**
 * Created by Ageev Evgeny on 27.06.2016.
 * Singleton
 */
public class DataManager {
    private static DataManager INSTANCE = null;

    private final PreferencesManager mPreferencesManager;

    private DataManager() {
        mPreferencesManager = new PreferencesManager();
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
}
