package com.unseenspace.irc;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.util.Log;

import android.support.v7.preference.PreferenceFragmentCompatFix;

/**
 * fragment for preferences/settings.
 * main logic and where you can get values from preferences
 * Created by madsk_000 on 6/18/2015.
 */
public class SettingsFragment extends PreferenceFragmentCompatFix implements Preference.OnPreferenceChangeListener {
    /**
     * A tag for logging.
     */
    private static final String TAG = "SettingsFragment";

    /**
     * The file name for SharedPreferences.
     */
    public static final String SETTINGS_SHARED_PREFERENCES_FILE_NAME = TAG + ".SETTINGS_SHARED_PREFERENCES_FILE_NAME";

    /**
     * @{inheritDoc}
     * @param bundle @{inheritDoc}
     * @param rootKey @{inheritDoc}
     */
    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        getPreferenceManager().setSharedPreferencesName(SETTINGS_SHARED_PREFERENCES_FILE_NAME);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        findPreference(getResources().getString(R.string.pref_theme)).setOnPreferenceChangeListener(this);
    }

    /**
     * Get the current theme specified by preference in resID form.
     * @param preferences the shared preference we are using now
     * @return resId of the theme
     */
    public static int getTheme(SharedPreferences preferences) {
        String string = preferences.getString("pref_theme", "Default");
        switch (string) {
            case "Default":
            case "Light+Purple":
                return R.style.AppTheme_Light_Purple;
            case "Dark+Purple":
                return R.style.AppTheme_Dark_Purple;
            default:
                        Log.e(TAG, "Unknown theme type, " + string);
            return R.style.AppTheme_Light_Purple;
        }
    }

    /**
     * When changing the theme, reset the activity and activities before to apply the theme.
     * @param preference @{inheritDoc}
     * @param newValue @{inheritDoc}
     * @return @{inheritDoc}
     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(getResources().getString(R.string.pref_theme))) {
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
