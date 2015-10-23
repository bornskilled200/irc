package com.unseenspace.irc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.github.machinarius.preferencefragment.PreferenceFragment;

/**
 * Created by madsk_000 on 6/18/2015.
 */
public class SettingsFragment extends PreferenceFragment {
    private final static String TAG = SettingsFragment.class.getName();
    public final static String SETTINGS_SHARED_PREFERENCES_FILE_NAME = TAG + ".SETTINGS_SHARED_PREFERENCES_FILE_NAME";

    public static SettingsFragment create() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName(SETTINGS_SHARED_PREFERENCES_FILE_NAME);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    public static int getTheme(SharedPreferences preferences) {
        if (preferences.getBoolean("pref_theme", false) == false)
            return R.style.AppTheme_Light;

        String string = preferences.getString("pref_themeType", "Light");
        switch (string)
        {
            case "Light+Purple":
                return R.style.AppTheme_Light_Purple;
            case "Dark+Purple":
                return R.style.AppTheme_Dark_Purple;
        }
        Log.e(TAG, "Unknown theme type, " + string);
        return R.style.AppTheme_Light;
    }
}
