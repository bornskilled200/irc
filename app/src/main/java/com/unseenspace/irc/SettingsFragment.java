package com.unseenspace.irc;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.util.Log;

import android.support.v7.preference.PreferenceFragmentCompatFix;
//import android.support.v7.preference.PreferenceFragmentCompat;
//import com.github.machinarius.preferencefragment.PreferenceFragment;

/**
 * fragment for preferences/settings
 * main logic and where you can get values from preferences
 * Created by madsk_000 on 6/18/2015.
 */
public class SettingsFragment extends PreferenceFragmentCompatFix implements Preference.OnPreferenceChangeListener {
    private final static String TAG = "SettingsFragment";
    public final static String SETTINGS_SHARED_PREFERENCES_FILE_NAME = TAG + ".SETTINGS_SHARED_PREFERENCES_FILE_NAME";

    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        getPreferenceManager().setSharedPreferencesName(SETTINGS_SHARED_PREFERENCES_FILE_NAME);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        findPreference("pref_theme").setOnPreferenceChangeListener(this);
    }

    public static int getTheme(SharedPreferences preferences) {
        String string = preferences.getString("pref_theme", "Default");
        switch (string)
        {
            case "Default":
            case "Light+Purple":
                return R.style.AppTheme_Light_Purple;
            case "Dark+Purple":
                return R.style.AppTheme_Dark_Purple;
        }
        Log.e(TAG, "Unknown theme type, " + string);
        return R.style.AppTheme_Light_Purple;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals("pref_theme"))
        {
            Activity activity = getActivity();
            activity.finish();
            startActivity(activity.getIntent());

            for (activity = activity.getParent(); activity != null; activity = activity.getParent())
                if (activity instanceof BaseActivity)
                    ((BaseActivity) activity).refreshTheme();
        }
        return true;
    }
}
