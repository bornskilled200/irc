package com.unseenspace.irc;

import android.Manifest;
import android.app.Instrumentation;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.PreferenceMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;

import com.unseenspace.android.Tests;
import com.unseenspace.android.Themes;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.PreferenceMatchers.withKey;
import static android.support.test.espresso.matcher.PreferenceMatchers.withTitleText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 *
 */
@RunWith(AndroidJUnit4.class)
public class SettingsActivityTest {
    @Rule
    public final ActivityTestRule<SettingsActivity> activityRule = new ActivityTestRule<>(SettingsActivity.class);

    /**
     * fix for marshmallow's permission model that only allows external write when asked for
     * compared to
     */
    @Before
    public void before() {
        if (ContextCompat.checkSelfPermission(activityRule.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(activityRule.getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getInstrumentation().getTargetContext()).edit().clear().commit();
    }

    @Test
    public void testPortraitToLandscape() throws IOException {
        SettingsActivity activity = activityRule.getActivity();

        activity = Tests.rotate(activity, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Bitmap before = BitmapFactory.decodeFile(Tests.screenshot(activity, "before").getCanonicalPath());
        assertThat(before.getWidth(), lessThan(before.getHeight()));

        activity = Tests.rotate(activity, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Bitmap after = BitmapFactory.decodeFile(Tests.screenshot(activity, "after").getCanonicalPath());
        assertThat(after.getWidth(), greaterThan(after.getHeight()));
    }

    @Test
    public void testLandscapeToPortrait() throws IOException {
        SettingsActivity activity = activityRule.getActivity();

        activity = Tests.rotate(activity, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Bitmap before = BitmapFactory.decodeFile(Tests.screenshot(activity, "before").getCanonicalPath());
        assertThat(before.getWidth(), greaterThan(before.getHeight()));

        activity = Tests.rotate(activity, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Bitmap after = BitmapFactory.decodeFile(Tests.screenshot(activity, "after").getCanonicalPath());
        assertThat(after.getWidth(), lessThan(after.getHeight()));
    }

    @Test
    public void testToolbarColor() throws IOException {
        SettingsActivity activity = activityRule.getActivity();

        Bitmap screenshot = BitmapFactory.decodeFile(Tests.screenshot(activity, "screenshot").getCanonicalPath());

        assertThat(screenshot.getPixel(0, 0), is(Themes.getColor(activity, R.attr.colorPrimaryDark)));
        assertThat(screenshot.getPixel(0, Tests.getStatusBarHeight(activity) + 1), is(Themes.getColor(activity, R.attr.colorPrimary)));
    }

    @Test
    public void testLightTheme() throws IOException, InterruptedException {
        changeTheme("Light", 0, .5, Color.parseColor("#fffafafa")); //<color name="material_grey_50">#fffafafa</color>
    }

    @Test
    public void testDarkTheme() throws IOException, InterruptedException {
        changeTheme("Dark", 0, .5, Color.parseColor("#ff303030")); //<color name="material_grey_850">#ff303030</color>
    }

    /* UTILITY FUNCTIONS */
    public Bitmap changeTheme(final String filter, double x, double y, int color) throws IOException, InterruptedException {
        SettingsActivity activity = activityRule.getActivity();

        onView(withText(R.string.pref_theme_title)).perform(click());
        onView(withText(containsString(filter))).perform(click());

        Bitmap screenshot = BitmapFactory.decodeFile(Tests.screenshot(Tests.getCurrentActivity(), filter).getCanonicalPath());
        assertThat(screenshot.getPixel((int)(screenshot.getWidth()*x), (int)(screenshot.getHeight()*y)), is(color));

        return screenshot;
    }

    private String text(int resourceId)
    {
        return Tests.getCurrentActivity().getResources().getString(resourceId);
    }

    public Iterable<CharSequence> filter(CharSequence[] chars, CharSequence filter)
    {
        ArrayList<CharSequence> filtered = new ArrayList<>();
        for (CharSequence string : chars) {
            if (TextUtils.indexOf(string, filter) != -1)
                filtered.add(string);
        }
        return filtered;
    }
}