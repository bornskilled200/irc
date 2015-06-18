package com.unseenspace.archery;

import android.os.Bundle;

import com.github.machinarius.preferencefragment.PreferenceFragment;

/**
 * Created by madsk_000 on 6/18/2015.
 */
public class SettingsFragment extends PreferenceFragment {
    public static SettingsFragment create() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
