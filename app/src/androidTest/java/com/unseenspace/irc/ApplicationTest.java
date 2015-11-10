package com.unseenspace.irc;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.squareup.spoon.Spoon;
import com.unseenspace.android.EspressoActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
@RunWith(AndroidJUnit4.class)
public class ApplicationTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new EspressoActivityTestRule<>(MainActivity.class);

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
    public void testPortraitToLandscape() {
        MainActivity activity = activityRule.getActivity();

        activity = rotate(activity, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        screenshot(activity, "before");

        activity = rotate(activity, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        screenshot(activity, "after");
    }

    @Test
    public void testLandscapeToPortrait()
    {
        MainActivity activity = activityRule.getActivity();

        activity = rotate(activity, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        screenshot(activity, "before");

        activity = rotate(activity, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        screenshot(activity, "after");
    }



    /* Utility Functions */
    @SuppressWarnings("unchecked")
    private static <T extends Activity> T rotate(T activity, int requestedOrientation)
    {
        activity.setRequestedOrientation(requestedOrientation);
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        activity = (T) getCurrentActivity();
        return activity;
    }

    private static File screenshot(Activity activity, String tag) {
        StackTraceElement testClass = findTestClassTraceElement(Thread.currentThread().getStackTrace());
        String className = testClass.getClassName().replaceAll("[^A-Za-z0-9._-]", "_");
        String methodName = testClass.getMethodName();
        return Spoon.screenshot(activity, tag, className, methodName);
    }

    private static StackTraceElement findTestClassTraceElement(StackTraceElement[] trace) {
        for (int i = trace.length - 1; i >= 0; i--) {
            StackTraceElement element = trace[i];

            try {
                for (Annotation annotation : Class.forName(element.getClassName()).getDeclaredMethod(element.getMethodName()).getDeclaredAnnotations()) {
                    if (annotation.annotationType().equals(Test.class))
                        return element;
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        throw new IllegalArgumentException("Could not find test class!");
    }

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
}