package com.softdesign.devintensive.ui.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.softdesign.devintensive.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
    }

}