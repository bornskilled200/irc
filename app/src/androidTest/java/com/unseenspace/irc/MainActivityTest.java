package com.unseenspace.irc;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.unseenspace.android.Tests;
import com.unseenspace.android.Themes;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Future;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

/**
 *
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    public static final Intent IRC_CONNECTED = new Intent(IrcFragment.IRC_CONNECTED);
    public static final Intent TTS_INITIALIZED = new Intent(IrcFragment.TTS_INITIALIZED);
    /**
     *
     */
    @Rule
    public final ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    /**
     * fix for marshmallow's permission model that only allows external write when asked for.
     * compared to previous android versions
     */
    @Before
    public void before() {
        MainActivity activity = activityRule.getActivity();
        int externalWrite = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (externalWrite == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    /**
     * Make sure we are using the debuggable release type, giving additional permissions in AndroidManifest.xml.
     */
    @Test
    public void testDebuggable() {
        ApplicationInfo appInfo = activityRule.getActivity().getApplicationInfo();

        assertTrue((appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
    }

    /**
     *
     */
    @Test
    public void testExternalWrite() {
        MainActivity activity = activityRule.getActivity();

        int writeExternal = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        assertEquals(PackageManager.PERMISSION_GRANTED, writeExternal);
    }

    /**
     *
     * @throws IOException
     */
    @Test
    public void testPortraitToLandscape() throws IOException {
        MainActivity activity = activityRule.getActivity();

        activity = Tests.rotate(activity, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Bitmap before = BitmapFactory.decodeFile(Tests.screenshot(activity, "before").getCanonicalPath());
        assertThat(before.getWidth(), lessThan(before.getHeight()));

        activity = Tests.rotate(activity, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Bitmap after = BitmapFactory.decodeFile(Tests.screenshot(activity, "after").getCanonicalPath());
        assertThat(after.getWidth(), greaterThan(after.getHeight()));
    }

    /**
     *
     * @throws IOException
     */
    @Test
    public void testLandscapeToPortrait() throws IOException {
        MainActivity activity = activityRule.getActivity();

        activity = Tests.rotate(activity, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Bitmap before = BitmapFactory.decodeFile(Tests.screenshot(activity, "before").getCanonicalPath());
        assertThat(before.getWidth(), greaterThan(before.getHeight()));

        activity = Tests.rotate(activity, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Bitmap after = BitmapFactory.decodeFile(Tests.screenshot(activity, "after").getCanonicalPath());
        assertThat(after.getWidth(), lessThan(after.getHeight()));
    }

    /**
     *
     * @throws IOException
     */
    @Test
    public void testToolbarColor() throws IOException {
        MainActivity activity = activityRule.getActivity();

        Bitmap screenshot = BitmapFactory.decodeFile(Tests.screenshot(activity, "screenshot").getCanonicalPath());

        assertThat(screenshot.getPixel(0, 0), is(Themes.getColor(activity, R.attr.colorPrimaryDark)));

        int toolbarY = Tests.getStatusBarHeight(activity) + 1;
        assertThat(screenshot.getPixel(0, toolbarY), is(Themes.getColor(activity, R.attr.colorPrimary)));
    }

    /**
     *
     * @throws Throwable
     */
    @Test
    public void testConnectLocal() throws Throwable {
        MainActivity activity = activityRule.getActivity();
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(activity);
        BroadcastReceiver broadcastReceiver = mock(BroadcastReceiver.class);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IrcFragment.TTS_INITIALIZED);
        intentFilter.addAction(IrcFragment.IRC_CONNECTED);
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);

        Future<Socket> server = Server.one("localhost");
        try {
            onView(withId(R.id.fab)).perform(click());

            onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
            verify(broadcastReceiver, timeout(100).atLeastOnce())
                    .onReceive(any(Context.class), argThat(intent(IrcFragment.TTS_INITIALIZED)));
            Tests.screenshot(activity, "tts-initialized");
            onView(withId(R.id.textBox)).check(matches(withText(containsString("Initialized"))));

            verify(broadcastReceiver, timeout(500).atLeastOnce())
                    .onReceive(any(Context.class), argThat(intent(IrcFragment.IRC_CONNECTED)));
            Tests.screenshot(activity, "irc-connected");
            assertThat(server.get(), notNullValue());
        } finally {
            broadcastManager.unregisterReceiver(broadcastReceiver);
            server.cancel(true);
        }
    }

    private Matcher<Intent> intent(final String action) {
        return new BaseMatcher<Intent>() {

            @Override
            public void describeTo(Description description) {
                description.appendText(action);
            }

            @Override
            public boolean matches(Object item) {
                return ((Intent) item).getAction().equals(action);
            }
        };
    }
}