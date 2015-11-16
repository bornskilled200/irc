package com.unseenspace.irc;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

public class MainActivity extends BaseActivity implements IrcListFragment.IrcListener {

    private final static String TAG = "MainActivity";
    public static final String TAG_IRC_LIST = "TAG_IRC_LIST";
    public static final String TAG_IRC = "TAG_IRC";
    private DrawerLayout drawerLayout;

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
        Log.v(TAG, fragmentManager.getBackStackEntryCount() + " " + fragmentManager.getFragments());

        IrcFragment ircFragment = null;
        IrcListFragment listFragment;
        if (savedInstanceState == null) {
            FragmentManager.enableDebugLogging(true);
            Log.v(TAG, "Creating IrcListFragment");
            listFragment = new IrcListFragment();
        }
        else {
            listFragment = (IrcListFragment) fragmentManager.findFragmentByTag(TAG_IRC_LIST);
            ircFragment = (IrcFragment) fragmentManager.findFragmentByTag(TAG_IRC);

            Log.v(TAG, "IrcListFragment " + (listFragment == null ? "not" : "") + "found");
            Log.v(TAG, "IrcFragment " + (ircFragment == null ? "not" : "") + "found");
            Log.v(TAG, "Removing IrcListFragment || IrcFragment");
            if (ircFragment != null)
                fragmentManager.popBackStackImmediate();
            fragmentManager.beginTransaction().remove(listFragment).commit();
            Log.v(TAG, "Pending actions executed: " + fragmentManager.executePendingTransactions());
        }

        int panes = getResources().getInteger(R.integer.panes);
        Log.v(TAG, "Panes: " + panes);

        Log.v(TAG, "Adding IrcListFragment");
        fragmentManager.beginTransaction()
                .replace(panes == 1 ? R.id.mainFragment : R.id.sideFragment, listFragment, TAG_IRC_LIST)
                .commit();

        if (ircFragment != null)
            addIrcFragment(ircFragment);
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
        addIrcFragment(IrcFragment.create("unseenspace", "", IrcFragment.Template.TWITCH));
        return true;
    }

    private void addIrcFragment(IrcFragment fragment) {
        addIrcFragment(fragment, getResources().getInteger(R.integer.panes));
    }

    private void addIrcFragment(IrcFragment fragment, int panes) {
        Log.v(TAG, "Adding IrcFragment");
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (panes == 1)
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right_full, R.anim.slide_out_left_full, R.anim.slide_in_left_full, R.anim.slide_out_right_full);
        else
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_child_bottom, 0);

        fragmentTransaction.replace(R.id.mainFragment, fragment, TAG_IRC)
                .addToBackStack("IRC")
                .commit();
    }
}
