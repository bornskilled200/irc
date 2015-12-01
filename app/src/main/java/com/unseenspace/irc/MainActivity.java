package com.unseenspace.irc;

import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends BaseActivity implements IrcListFragment.IrcListener, FragmentManager.OnBackStackChangedListener {

    private final static String TAG = "MainActivity";
    public static final String TAG_IRC_LIST = "TAG_IRC_LIST";
    public static final String TAG_IRC = "TAG_IRC";
    private static final String TAG_IRC_CREATE = "TAG_IRC_CREATE";

    private DrawerLayout drawerLayout;

    /**
     * What layout this is, 0 for none or taken literally, no panes.
     */
    private int currentPaneSetup = 0;
    public static final int NOTIFICATION_ONGOING = 1;

    /**
     * this gets called after onCreate and given menu is the same as toolbar.getMenu()
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_irc_create, menu);

        menu.findItem(R.id.action_save).setVisible(getSupportFragmentManager().findFragmentByTag(TAG_IRC_CREATE) != null);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }


        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowCustomEnabled(true);
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
            Log.v(TAG, "Changing layout to " + panes + " panes");
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            IrcListFragment listFragment;
            if (savedInstanceState == null) {
                Log.v(TAG, "Creating IrcListFragment");
                listFragment = new IrcListFragment();
                listFragment.setRetainInstance(true);

                fragmentTransaction.replace(R.id.sideFragment, listFragment, TAG_IRC_LIST);
            } else {
                Log.v(TAG, "Retrieving IrcListFragment");
                listFragment = (IrcListFragment) fragmentManager.findFragmentByTag(TAG_IRC_LIST);
            }
            Fragment ircFragment = fragmentManager.findFragmentByTag(TAG_IRC);

            /*
             * SHOW THINGS CORRECTLY BECAUSE THE PANE SETUP CHANGED
             * One advantage of doing it like this is that I don't have to
             * rearrange the BackStack of the FragmentManager
             */
            if (panes == 1)
                if (ircFragment != null) {
                    Log.v(TAG, "hiding IrcListFragment and showing IrcFragments");
                    show(fragmentTransaction.hide(listFragment), ircFragment).commit();
                }
                else {
                    Log.v(TAG, "showing IrcListFragments");
                    fragmentTransaction.show(listFragment).commit();
                }
            else if (panes == 2) {
                Log.v(TAG, "showing IrcListFragments and showing IrcFragments");
                show(fragmentTransaction.show(listFragment), ircFragment).commit();
            }

            currentPaneSetup = panes;
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
     * handling side drawer items and toolbar items
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
            case R.id.action_save:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onClick(IrcEntry entry) {
        addIrcFragment(IrcFragment.create("localhost", "channel", "unseenspace", "", IrcEntry.Template.IRC));
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

        Log.v(TAG, "Starting Notification");
        // Gets an instance of the NotificationManager service
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ONGOING, new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.irc_notification)
                .setContentTitle("Unseen IRC")
                .setContentText("Not Connected")
                .setOngoing(true)
                .build());

        if (panes == 1)
            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(TAG_IRC_LIST)).show(ircFragment).commit();
    }

    @Override
    protected void onDestroy() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        super.onDestroy();
    }

    @Override
    public void onBackStackChanged() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (getResources().getInteger(R.integer.panes) == 1 && fragmentManager.getBackStackEntryCount() == 0)
            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(TAG_IRC_LIST)).commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.getMenu().findItem(R.id.action_save).setVisible(fragmentManager.findFragmentByTag(TAG_IRC_CREATE) != null);
    }
}
