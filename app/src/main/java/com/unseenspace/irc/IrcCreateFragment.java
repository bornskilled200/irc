package com.unseenspace.irc;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

/**
 * a fragment showing the given irc.
 * <p/>
 * TextView for the chat
 * EditText for messages to send
 * Created by madsk_000 on 10/23/2015.
 */
@SuppressWarnings("SameParameterValue")
public class IrcCreateFragment extends Fragment {
    /**
     * A tag for logging.
     */
    private static final String TAG = "IrcCreateFragment";
    /**
     * An extra image for transitioning 6.0+.
     */
    private static final String EXTRA_IMAGE = "ShootActivity:image";
    /**
     * Parameter name for username in bundle.
     */
    private static final String USERNAME = "USERNAME_PARAMETER";
    /**
     * Parameter name for password in bundle.
     */
    private static final String PASSWORD = "PASSWORD_PARAMETER";
    /**
     * Parameter name for template in bundle.
     */
    private static final String TEMPLATE = "TEMPLATE_PARAMETER";

    /**
     * Animation for this fragment entering landscape.
     */
    private Animation enterLandscapeAnimation;
    /**
     * Animation for this fragment exiting landscape.
     */
    private Animation exitLandscapeAnimation;

    /**
     * Animation for this fragment exiting portrait.
     */
    private Animation enterPortraitAnimation;
    /**
     * Animation for this fragment exiting portrait.
     */
    private Animation exitPortraitAnimation;

    /**
     * Database Open helper for irc table.
     */
    private IrcOpenHelper openHelper;

    /**
     * Convenience Method to create this fragment.
     *
     * @param template the template that this fragment will use
     * @return a new instance of IrcCreateFragment
     */
    @SuppressWarnings("SameParameterValue")
    public static IrcCreateFragment create(IrcEntry.Template template) {
        Log.v(TAG, "create(" + template + ")");

        IrcCreateFragment ircFragment = new IrcCreateFragment();

        // Get arguments passed in, if any
        Bundle args = ircFragment.getArguments();
        if (args == null)
            args = new Bundle();

        args.putString(TEMPLATE, template.name());
        ircFragment.setArguments(args);

        return ircFragment;
    }

    /**
     * convenience method to check for null and empty string.
     * if null , will return empty string
     *
     * @return will always return a String, never null
     */
    private String getPassword() {
        Bundle arguments = getArguments();
        if (arguments == null)
            return "";
        String string = arguments.getString(PASSWORD);
        return string == null ? "" : string;
    }

    /**
     * convenience method to check for null and empty string.
     * if null or empty string, will return Template.IRC
     * however it will throw an exception if Enum.valueOf is invalid
     *
     * @return will always the given template in the arguments
     * @see com.unseenspace.irc.IrcEntry.Template#IRC
     */
    private IrcEntry.Template getTemplate() {
        Bundle arguments = getArguments();
        if (arguments == null)
            return IrcEntry.Template.IRC;
        String string = arguments.getString(TEMPLATE);
        return string == null || string.length() == 0 ? IrcEntry.Template.IRC : IrcEntry.Template.valueOf(string);
    }

    /**
     * convenience method to add the irc.
     *
     * @param template the template to use
     * @param name     the name of this irc (for user use)
     * @param ip       any kind of url
     * @param channels channels split by a space and prepended with #
     * @param username username/nick
     * @param password password
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    private long addIrc(IrcEntry.Template template, String name, String ip, String channels,
                        String username, String password) {
        SQLiteDatabase db = openHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(IrcEntry.COLUMN_TEMPLATE, template.name());
        values.put(IrcEntry.COLUMN_NAME, name);
        values.put(IrcEntry.COLUMN_IP, ip);
        values.put(IrcEntry.COLUMN_CHANNEL, channels);
        values.put(IrcEntry.COLUMN_USERNAME, username);
        values.put(IrcEntry.COLUMN_PASSWORD, password);

        // Insert the new row, returning the primary key value of the new row
        return db.insert(IrcEntry.TABLE_NAME, null, values);
    }

    /**
     * @{inheritDoc}
     * @param inflater @{inheritDoc}
     * @param container @{inheritDoc}
     * @param savedInstanceState @{inheritDoc}
     * @return @{inheritDoc}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_irc, container, false);

        if (savedInstanceState == null) {
            openHelper = new IrcOpenHelper(getContext());

            Interpolator interpolator;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                interpolator = AnimationUtils.loadInterpolator(getContext(), android.R.interpolator.fast_out_slow_in);
            else
                interpolator = new FastOutSlowInInterpolator();

            enterPortraitAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right_full);
            enterPortraitAnimation.setInterpolator(interpolator);

            exitPortraitAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right_full);
            exitPortraitAnimation.setInterpolator(interpolator);

            enterLandscapeAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_child_bottom);
            enterLandscapeAnimation.setInterpolator(interpolator);

            exitLandscapeAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_child_bottom);
            exitLandscapeAnimation.setInterpolator(interpolator);
        }

        return view;
    }

    /**
     * @{inheritDoc}
     * @param transit @{inheritDoc}
     * @param enter @{inheritDoc}
     * @param nextAnim @{inheritDoc}
     * @return @{inheritDoc}
     */
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        int panes = getResources().getInteger(R.integer.panes);
        if (panes == 1)
            return enter ? enterPortraitAnimation : exitPortraitAnimation;
        else if (panes == 2)
            return enter ? enterLandscapeAnimation : exitLandscapeAnimation;
        return super.onCreateAnimation(transit, enter, nextAnim);
    }
}
