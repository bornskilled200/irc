package com.unseenspace.android.test;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.test.espresso.IdlingResource;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by madsk_000 on 11/24/2015.
 */
public class IntentIdlingResource implements IdlingResource {
    private boolean called = false;
    private final Context context;
    private final String intent;

    public IntentIdlingResource(Context context, String intent) {
        this.context = context;
        this.intent = intent;
    }

    @Override
    public String getName() {
        return IntentIdlingResource.class.getName();
    }

    @Override
    public void registerIdleTransitionCallback(final ResourceCallback resourceCallback) {
        LocalBroadcastManager.getInstance(context).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                called = true;
                resourceCallback.onTransitionToIdle();
            }
        }, new IntentFilter(intent));
    }

    @Override
    public boolean isIdleNow() {
        return called;
    }
}
