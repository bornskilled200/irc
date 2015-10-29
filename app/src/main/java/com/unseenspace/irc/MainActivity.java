package com.unseenspace.irc;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = "MainActivity";
    private DrawerLayout drawerLayout;

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


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null)
            setupDrawerContent(navigationView, drawerLayout, this);

        if (getResources().getInteger(R.integer.panes) == 1)
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.mainFragment, new IrcListFragment())
                    .commit();
        else {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.sideFragment, new IrcListFragment())
                    .commit();
        }
    }


    /**
     * handling side drawer items
     * android.R.id.home (Hamburger/Settings button)
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFragment, IrcFragment.create("unseenspace", preferences.getString("password", "")))
                .addToBackStack(null)
                .commit();
    }
}
