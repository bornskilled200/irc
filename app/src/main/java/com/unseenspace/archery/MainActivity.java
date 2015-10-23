package com.unseenspace.archery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends BaseActivity {

    private final static String TAG = MainActivity.class.getName();
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null)
            setSupportActionBar(toolbar);



        final ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            ab.setDisplayHomeAsUpEnabled(true);
        }


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null)
            setupDrawerContent(navigationView);
    }


    /**
     * android.R.id.home (Hamburger/Settings button)
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * When an item from the settings drawer gets picked
     * @param navigationView
     */
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();

                        //setTheme(menuItem);

                        if (menuItem.getItemId() == R.id.nav_settings)
                        {
                            SettingsActivity.launch(MainActivity.this);

                        }
                        return true;
                    }
                });
    }

    //private void setTheme(MenuItem menuItem)
    //{
    //    switch (menuItem.getItemId())
    //    {
    //        case R.id.nav_home:
    //            themeId = R.style.AppTheme_Light_Purple;
    //            break;
    //        case R.id.nav_dark:
    //            themeId = R.style.AppTheme_Dark_Purple;
    //            break;
    //        default:
    //            return;
    //    }
//
    //    finish();
    //    startActivity(new Intent(this, getClass()));
    //}

    //No need to have a settings in the action bar. The menu/triple dots button
    //@Override
    //public boolean onCreateOptionsMenu(Menu menu) {
    //    // Inflate the menu; this adds items to the action bar if it is present.
    //    getMenuInflater().inflate(R.menu.menu_main, menu);
    //    return true;
    //}

    /**
     * What happens when you click one of the cards/items
     * @param view
     */
    //@Override
    //public void onItemClick(View view) {
    //    ShootActivity.launch(MainActivity.this, view.findViewById(R.id.image));
    //}
}
