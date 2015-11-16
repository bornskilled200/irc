package com.unseenspace.android;

import android.app.Activity;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;

import com.squareup.spoon.Spoon;

import org.junit.Test;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Iterator;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * utility functions for android testing
 * Created by madsk_000 on 11/16/2015.
 */
public class Tests {
    /**
     * Rotate the device given it's activity and requested Orientation (@see ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
     * @param activity the current activity
     * @param requestedOrientation an orientation from ActivityInfo.SCREEN*
     * @return the activity that gets created/reset because of the orientation change
     */
    @SuppressWarnings("unchecked")
    public static <T extends Activity> T rotate(T activity, int requestedOrientation)  {
        activity.setRequestedOrientation(requestedOrientation);
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        T currentActivity = (T) getCurrentActivity();
        if (currentActivity == null)
            screenshot(activity, "fail");
        assertThat("This test will fail if the device's screen is not on", currentActivity, notNullValue());
        return currentActivity;
    }

    /**
     * a different approach than (@see Spoon.screenshot)
     * reflectively checks for a method in the stacktrace that has a @Test annotation
     * @param activity the current activity that wants to be screenshot
     * @param tag a tag for later inspection, must only contain [A-Za-z0-9._-]
     * @return the file pointing to the screenshot
     */
    public static File screenshot(Activity activity, String tag) {
        StackTraceElement testClass = findTestClassTraceElement(Thread.currentThread().getStackTrace());
        String className = testClass.getClassName().replaceAll("[^A-Za-z0-9._-]", "_");
        String methodName = testClass.getMethodName();
        return Spoon.screenshot(activity, tag, className, methodName);
    }

    /**
     * a different approach than (@see Spoon.screenshot)
     * reflectively checks for a method in the stacktrace that has a @Test annotation
     * @param activity the current activity that wants to be screenshot
     * @param tag a tag for later inspection, must only contain [A-Za-z0-9._-]
     * @return the file pointing to the screenshot
     */
    public static File screenshot(Activity activity, String tag, Throwable e) {
        StackTraceElement testClass = findTestClassTraceElement(e);
        String className = testClass.getClassName().replaceAll("[^A-Za-z0-9._-]", "_");
        String methodName = testClass.getMethodName();
        return Spoon.screenshot(activity, tag, className, methodName);
    }

    /**
     * helper method for screenshot
     * returns the first element of the stack trace starting from top
     * that points to a method that has @Test annotation
     *
     * @param trace stacktrace that will be examined
     * @return element of the stack trace that points to a method that has @Test annotation
     */
    public static StackTraceElement findTestClassTraceElement(StackTraceElement[] trace) {
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
     * helper method for screenshot
     * returns the first element of the stack trace starting from top
     * that points to a method that has @Test annotation
     *
     * @param e throwable/exception that will be examied
     * @return element of the stack trace that points to a method that has @Test annotation
     */
    public static StackTraceElement findTestClassTraceElement(Throwable e) {
        StackTraceElement[] trace = e.getStackTrace();
        for (int i = trace.length - 1; i >= 0; i--) {
            StackTraceElement element = trace[i];

            try {
                for (Annotation annotation : Class.forName(element.getClassName()).getDeclaredMethod(element.getMethodName()).getDeclaredAnnotations()) {
                    if (annotation.annotationType().equals(Test.class))
                        return element;
                }
            } catch (NoSuchMethodException ex) {
                //e.printStackTrace();
            } catch (ClassNotFoundException ex) {
                //e.printStackTrace();
            }
        }

        throw new IllegalArgumentException("Could not find test class!", e);
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
                Iterator<Activity> resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED).iterator();
                if (resumedActivities.hasNext())
                    currentActivity[0] = resumedActivities.next();
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
    public static int getStatusBarHeight(Activity activity) {
        int result = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
