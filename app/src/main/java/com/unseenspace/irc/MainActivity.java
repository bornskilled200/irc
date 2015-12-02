package com.unseenspace.irc;

import android.app.NotificationManager;
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
import android.view.Menu;
import android.view.MenuItem;

import static android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import static com.unseenspace.irc.IrcListFragment.IrcListener;

/**
 * Main activity for the Irc Program.
 */
public class MainActivity extends BaseActivity implements IrcListener, OnBackStackChangedListener {

    /**
     * A tag for logging.
     */
    private static final String TAG = "MainActivity";

    /**
     * A tag IrcListFragment in FragmentManager.
     */
    public static final String TAG_IRC_LIST = "TAG_IRC_LIST";

    /**
     * A tag IrcFragment in FragmentManager.
     */
    public static final String TAG_IRC = "TAG_IRC";

    /**
     * A tag IrcCreateFragment in FragmentManager.
     */
    private static final String TAG_IRC_CREATE = "TAG_IRC_CREATE";

    /**
     * The ID of the notification that will always stay up and will not go away to make sure this activity does
     * not get destroyed.
     */
    public static final int NOTIFICATION_ONGOING = 1;
    /**
     * The DrawerLayout for the main activity.
     */
    private DrawerLayout drawerLayout;

    /**
     * What layout this is, 0 for none or taken literally, no panes.
     */
    private int currentPaneSetup = 0;

    /**
     * this gets called after onCreate and given menu is the same as toolbar.getMenu().
     * @param menu @{inheritDoc}
     * @return @{inheritDoc}
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_irc_create, menu);

        FragmentManager supportFragmentManager = getSupportFragmentManager();
        menu.findItem(R.id.action_save).setVisible(supportFragmentManager.findFragmentByTag(TAG_IRC_CREATE) != null);

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * @{inheritDoc}
     * @param savedInstanceState @{inheritDoc}
     */
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
            setupDrawerContent(navigationView, drawerLayout);


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
                } else {
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
     * shows the fragment if it is not null.
     * @param transaction the transaction that is happening now
     * @param fragment the fragment to be shown
     * @return the transaction that was given, for "chaining"
     */
    private FragmentTransaction show(FragmentTransaction transaction, Fragment fragment) {
        if (fragment != null)
            transaction.show(fragment);
        return transaction;
    }

    /**
     * handles toolbar items.
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * gets called when an ircEntry gets clicked, usually from IrcListFragment.
     * @param entry the ircEntry that got clicked
     * @return if this method consumes this action
     */
    @Override
    public boolean onClick(IrcEntry entry) {
        addIrcFragment(IrcFragment.create(IrcEntry.Template.IRC, "localhost", "channel", "unseenspace", ""));
        return true;
    }

    /**
     * convenience method that request how many panes there are in the current layout.
     * @param fragment the fragment to be added to the layout
     * @see MainActivity#addIrcFragment(IrcFragment, int)
     */
    private void addIrcFragment(IrcFragment fragment) {
        addIrcFragment(fragment, getResources().getInteger(R.integer.panes));
    }

    /**
     * Add an IrcFragment to the layout.
     * this takes care of all the intricacies of all layouts, such as
     * starting up the notification so this activity does not die
     * hiding the sideFragment if panes == 1
     * @param ircFragment the IrcFragment to be added
     * @param panes how many panes there are currently
     */
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
            fragmentManager.beginTransaction()
                    .hide(fragmentManager.findFragmentByTag(TAG_IRC_LIST))
                    .show(ircFragment)
                    .commit();
    }

    /**
     * make sure we cancel all notification for now as we are still implementing it.
     */
    @Override
    protected void onDestroy() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        super.onDestroy();
    }

    /**
     * make sure that when we back into the IrcListFragment, we show it.
     * or * if we back into a IrcCreateFragment we show the save action in the actionbar/toolbar
     */
    @Override
    public void onBackStackChanged() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (getResources().getInteger(R.integer.panes) == 1 && fragmentManager.getBackStackEntryCount() == 0)
            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(TAG_IRC_LIST)).commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Menu menu = toolbar.getMenu();
        menu.findItem(R.id.action_save).setVisible(fragmentManager.findFragmentByTag(TAG_IRC_CREATE) != null);
    }
}
