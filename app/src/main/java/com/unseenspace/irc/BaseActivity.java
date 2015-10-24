package com.unseenspace.irc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * common base activity so that themes can be set automagically to all activities i create
 * Created by madsk_000 on 6/19/2015.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected SharedPreferences preferences;
    private boolean refreshTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = getSharedPreferences(SettingsFragment.SETTINGS_SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);

        setTheme(SettingsFragment.getTheme(preferences));

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        if (refreshTheme)
        {
            finish();
            startActivity(getIntent());
        }
        else
            super.onResume();
    }

    public void refreshTheme() {
        refreshTheme = true;
    }
}
