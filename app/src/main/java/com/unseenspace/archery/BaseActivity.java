package com.unseenspace.archery;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by madsk_000 on 6/19/2015.
 */
public class BaseActivity extends AppCompatActivity {

    protected SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = getSharedPreferences(SettingsFragment.SETTINGS_SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);

        setTheme(SettingsFragment.getTheme(preferences));

        super.onCreate(savedInstanceState);
    }
}
