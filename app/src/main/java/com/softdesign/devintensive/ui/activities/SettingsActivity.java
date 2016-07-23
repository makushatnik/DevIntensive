package com.softdesign.devintensive.ui.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.softdesign.devintensive.R;

/**
 * Created by Ageev Evgeny on 23.07.2016.
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
