package com.unseenspace.irc;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

public class MainActivity extends BaseActivity implements IrcListFragment.IrcListener, FragmentManager.OnBackStackChangedListener {

    private final static String TAG = "MainActivity";
    public static final String TAG_IRC_LIST = "TAG_IRC_LIST";
    public static final String TAG_IRC = "TAG_IRC";

    private DrawerLayout drawerLayout;
    private int currentPaneSetup = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null)
            setSupportActionBar(toolbar);


        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null)
            setupDrawerContent(navigationView, drawerLayout, this);


        /* FRAGMENT STUFF */
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);

        int panes = getResources().getInteger(R.integer.panes);
        if (currentPaneSetup != panes) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            IrcListFragment listFragment = new IrcListFragment();
            if (savedInstanceState == null) {
                Log.v(TAG, "Creating IrcListFragment");
                fragmentTransaction.replace(R.id.sideFragment, listFragment, TAG_IRC_LIST);
            } else
                listFragment = (IrcListFragment) fragmentManager.findFragmentByTag(TAG_IRC_LIST);

            Fragment ircFragment = fragmentManager.findFragmentByTag(TAG_IRC);
            if (panes == 1)
                if (ircFragment != null)
                    show(fragmentTransaction.hide(listFragment), ircFragment).commit();
                else
                    fragmentTransaction.show(listFragment).commit();
            else if (panes == 2)
                show(fragmentTransaction.show(listFragment), ircFragment).commit();

        }
    }

    /**
     * shows the fragment if not null
     * @param transaction the transaction that is happening now
     * @param fragment the fragment to be shown
     * @return the @param transaction that was given, for "chaining"
     */
    private FragmentTransaction show(FragmentTransaction transaction, Fragment fragment) {
        if (fragment != null)
            transaction.show(fragment);
        return transaction;
    }

    /**
     * handling side drawer items
     * android.R.id.home (Hamburger/Settings button)
     *
     * @param item the item that got selected
     * @return Return false to allow normal menu processing to
     *         proceed, true to consume it here.
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
    public boolean onClick(IrcEntry entry) {
        Log.v(TAG, "Creating IrcFragment");
        addIrcFragment(IrcFragment.create("unseenspace", "", IrcEntry.Template.TWITCH));
        return true;
    }

    private void addIrcFragment(IrcFragment fragment) {
        addIrcFragment(fragment, getResources().getInteger(R.integer.panes));
    }

    private void addIrcFragment(IrcFragment ircFragment, int panes) {
        Log.v(TAG, "Adding IrcFragment");
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mainFragment, ircFragment, TAG_IRC)
                .addToBackStack(null)
                .commit();

        if (panes == 1)
            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(TAG_IRC_LIST)).show(ircFragment).commit();
    }

    @Override
    public void onBackStackChanged() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (getResources().getInteger(R.integer.panes) == 1 && fragmentManager.getBackStackEntryCount() == 0)
            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(TAG_IRC_LIST)).commit();
    }
}
