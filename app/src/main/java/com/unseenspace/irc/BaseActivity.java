package com.unseenspace.irc;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * common base activity so that themes can be set automatically to all activities i create
 * Created by madsk_000 on 6/19/2015.
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected SharedPreferences preferences;

    private boolean refreshTheme;

    private Drawable getIconForAccount(Account account, AccountManager manager) {
        AuthenticatorDescription[] descriptions =  manager.getAuthenticatorTypes();
        for (AuthenticatorDescription description: descriptions) {
            if (description.type.equals(account.type)) {
                PackageManager pm = getPackageManager();
                return pm.getDrawable(description.packageName, description.iconId, null);
            }
        }
        return null;
    }
    /**
     * When an item from the left drawer gets picked
     *
     * @param navigationView the navigation view
     * @param activity the main activity
     */
    public void setupDrawerContent(NavigationView navigationView, final DrawerLayout drawerLayout, final AppCompatActivity activity) {
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
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
