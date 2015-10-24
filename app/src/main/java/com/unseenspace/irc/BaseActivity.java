package com.unseenspace.irc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * common base activity so that themes can be set automagically to all activities i create
 * Created by madsk_000 on 6/19/2015.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected SharedPreferences preferences;
    private boolean refreshTheme;

    /**
     * When an item from the settings drawer gets picked
     * @param navigationView
     * @param activity
     */
    public static void setupDrawerContent(NavigationView navigationView, final DrawerLayout drawerLayout, final AppCompatActivity activity) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();

                        if (menuItem.getItemId() == R.id.nav_settings)
                            SettingsActivity.launch(activity);

                        return true;
                    }
                });
    }

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
