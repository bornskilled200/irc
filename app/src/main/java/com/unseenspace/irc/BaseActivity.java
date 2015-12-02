package com.unseenspace.irc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * common base activity so that themes can be set automatically to all activities i create.
 * Also takes care of theme set in the preference
 * <p/>
 * Created by madsk_000 on 6/19/2015.
 */
public abstract class BaseActivity extends AppCompatActivity {
    /**
     * The preference for this app.
     */
    private SharedPreferences preferences;

    /**
     * whether or not we need to refresh the theme when we resume this activity.
     */
    private boolean refreshTheme;

    /**
     * Get the preferences for this app.
     *
     * @return preferences for this app
     */
    protected SharedPreferences getPreferences() {
        return preferences;
    }

    /**
     * When an item from the left drawer gets picked.
     *
     * @param navigationView the navigation view
     * @param drawerLayout   the DrawerLayout
     */
    public void setupDrawerContent(NavigationView navigationView, final DrawerLayout drawerLayout) {
        navigationView.inflateHeaderView(R.layout.nav_header);
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();

                if (menuItem.getItemId() == R.id.nav_settings)
                    SettingsActivity.launch(BaseActivity.this);

                return true;
            }
        });
    }

    /**
     * Apply the theme set in the preferences.
     *
     * @param savedInstanceState @{inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = getSharedPreferences(SettingsFragment.SETTINGS_SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);

        setTheme(SettingsFragment.getTheme(preferences));

        super.onCreate(savedInstanceState);
    }

    /**
     * If refreshTheme is set to true, then restart this activity.
     */
    @Override
    protected void onResume() {
        if (refreshTheme) {
            finish();
            startActivity(getIntent());
        } else
            super.onResume();
    }

    /**
     * Call to set a flag to refresh the theme on resume.
     */
    public void refreshTheme() {
        refreshTheme = true;
    }
}
