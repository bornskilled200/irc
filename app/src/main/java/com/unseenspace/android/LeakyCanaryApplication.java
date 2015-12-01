package com.unseenspace.android;

import android.app.Application;
import android.os.Build;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by madsk_000 on 11/27/2015.
 */
public class LeakyCanaryApplication extends Application {
    @Override public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            LeakCanary.install(this);
    }
}
