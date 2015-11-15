package com.unseenspace.irc;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.squareup.spoon.Spoon;
import com.unseenspace.android.EspressoActivityTestRule;
import com.unseenspace.android.Themes;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
public class ApplicationTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new EspressoActivityTestRule<>(MainActivity.class);

    /**
     * fix for marsmallow's permission model that only allows external write when asked for
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

        activity = rotate(activity, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        assertThat("This test will fail if the device's screen is not on", activity, notNullValue());
        Bitmap before = BitmapFactory.decodeFile(screenshot(activity, "before").getCanonicalPath());
        assertThat(before.getWidth(), lessThan(before.getHeight()));

        activity = rotate(activity, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Bitmap after = BitmapFactory.decodeFile(screenshot(activity, "after").getCanonicalPath());
        assertThat(after.getWidth(), greaterThan(after.getHeight()));
    }

    @Test
    public void testLandscapeToPortrait() throws IOException {
        MainActivity activity = activityRule.getActivity();

        activity = rotate(activity, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        assertThat("This test will fail if the device's screen is not on", activity, notNullValue());
        Bitmap before = BitmapFactory.decodeFile(screenshot(activity, "before").getCanonicalPath());
        assertThat(before.getWidth(), greaterThan(before.getHeight()));

        activity = rotate(activity, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Bitmap after = BitmapFactory.decodeFile(screenshot(activity, "after").getCanonicalPath());
        assertThat(after.getWidth(), lessThan(after.getHeight()));
    }

    @Test
    public void testToolbarColor() throws IOException {
        MainActivity activity = activityRule.getActivity();

        Bitmap screenshot = BitmapFactory.decodeFile(screenshot(activity, "screenshot").getCanonicalPath());

        assertThat(screenshot.getPixel(0, 0), is(Themes.getColor(activity, R.attr.colorPrimaryDark)));
        assertThat(screenshot.getPixel(0, getStatusBarHeight(activity) + 1), is(Themes.getColor(activity, R.attr.colorPrimary)));
    }



    /* Utility Functions */

    /**
     * Rotate the device given it's activity and requestion Orientation (@see ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
     * @param activity the current activity
     * @param requestedOrientation an orientation from ActivityInfo.SCREEN*
     * @return the activity that gets created/reset because of the orientation change
     */
    @SuppressWarnings("unchecked")
    private static <T extends Activity> T rotate(T activity, int requestedOrientation)  {
        activity.setRequestedOrientation(requestedOrientation);
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        activity = (T) getCurrentActivity();
        return activity;
    }

    /**
     * a different approach than (@see Spoon.screenshot)
     * reflectively checks for a method in the stacktrace that has a @Test annotation
     * @param activity the currente activity that wants to be screenshotted
     * @param tag a tag for later inspection, must only contain [A-Za-z0-9._-]
     * @return the file pointing to the screenshot
     */
    private static File screenshot(Activity activity, String tag) {
        StackTraceElement testClass = findTestClassTraceElement(Thread.currentThread().getStackTrace());
        String className = testClass.getClassName().replaceAll("[^A-Za-z0-9._-]", "_");
        String methodName = testClass.getMethodName();
        return Spoon.screenshot(activity, tag, className, methodName);
    }

    /**
     * helper method for screenshot
     * @param trace
     * @return
     */
    private static StackTraceElement findTestClassTraceElement(StackTraceElement[] trace) {
        for (int i = trace.length - 1; i >= 0; i--) {
            StackTraceElement element = trace[i];

            try {
                for (Annotation annotation : Class.forName(element.getClassName()).getDeclaredMethod(element.getMethodName()).getDeclaredAnnotations()) {
                    if (annotation.annotationType().equals(Test.class))
                        return element;
                }
            } catch (NoSuchMethodException e) {
                //e.printStackTrace();
            } catch (ClassNotFoundException e) {
                //e.printStackTrace();
            }
        }

        throw new IllegalArgumentException("Could not find test class!");
    }

    /**
     * http://qathread.blogspot.com/2014/09/discovering-espresso-for-android-how-to.html
     * returns the current activity
     * will not work if the screen device is off
     * mainly used as a work around when you change orientation until it is fixed in ActivityTestRule
     * @return the current foreground activity
     */
    public static Activity getCurrentActivity(){
        final Activity[] currentActivity = new Activity[1];
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection<Activity> resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                for (Activity act : resumedActivities) {
                    Log.d("Your current activity: ", act.getClass().getName());
                    currentActivity[0] = act;
                    break;
                }
            }
        });

        return currentActivity[0];
    }

    /**
     * http://stackoverflow.com/a/17880012/1036748
     * @param dp given dp that will be converted to pixels
     * @return the value of dp in pixels
     */
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * http://stackoverflow.com/a/17880012/1036748
     * @param px given px that will be converted to dp
     * @return the value of px in dp
     */
    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * http://stackoverflow.com/a/3410200/1036748
     * @param activity the main activity
     * @return the height of the status bar in pixels
     */
    public int getStatusBarHeight(Activity activity) {
        int result = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}