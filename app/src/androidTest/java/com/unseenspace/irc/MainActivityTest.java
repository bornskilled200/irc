package com.unseenspace.irc;

import android.Manifest;
import android.app.Instrumentation;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.unseenspace.android.Tests;
import com.unseenspace.android.Themes;
import com.unseenspace.android.test.IntentIdlingResource;

import org.junit.After;
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

/**
 *
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public final ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    /**
     * fix for marshmallow's permission model that only allows external write when asked for
     * compared to
     */
    @Before
    public void before() {
        if (ContextCompat.checkSelfPermission(activityRule.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(activityRule.getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    @Test
    public void testDebuggable() {
        ApplicationInfo appInfo = activityRule.getActivity().getApplicationInfo();

        assertTrue((appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
    }

    @Test
    public void testExternalWrite() {
        assertEquals(PackageManager.PERMISSION_GRANTED, ContextCompat.checkSelfPermission(activityRule.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

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

    @Test
    public void testToolbarColor() throws IOException {
        MainActivity activity = activityRule.getActivity();

        Bitmap screenshot = BitmapFactory.decodeFile(Tests.screenshot(activity, "screenshot").getCanonicalPath());

        assertThat(screenshot.getPixel(0, 0), is(Themes.getColor(activity, R.attr.colorPrimaryDark)));
        assertThat(screenshot.getPixel(0, Tests.getStatusBarHeight(activity) + 1), is(Themes.getColor(activity, R.attr.colorPrimary)));
    }

    @Test(timeout = 10000)
    public void testConnectLocal() throws Throwable {
        Future<Socket> server = Server.one("localhost");
        IntentIdlingResource intentIdlingResource = null;
        try {
            MainActivity activity = activityRule.getActivity();
            Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();


            onView(withId(R.id.fab)).perform(click());

            onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
            Espresso.registerIdlingResources(intentIdlingResource = new IntentIdlingResource(instrumentation.getTargetContext(), IrcFragment.TTS_INITIALIZED));
            Tests.screenshot(activity, "tts-initialized");
            onView(withId(R.id.textBox)).check(matches(withText(containsString("Initialized"))));
            Espresso.unregisterIdlingResources(intentIdlingResource);

            Espresso.registerIdlingResources(intentIdlingResource = new IntentIdlingResource(instrumentation.getTargetContext(), IrcFragment.IRC_CONNECTED));
            Tests.screenshot(activity, "irc-connected");
            assertThat(server.get(), notNullValue());
            Espresso.unregisterIdlingResources(intentIdlingResource);

        } finally {
            Espresso.unregisterIdlingResources(intentIdlingResource);
            server.cancel(true);
        }
    }
}