package com.unseenspace.irc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import hugo.weaving.DebugLog;

/**
 * Created by madsk_000 on 11/28/2015.
 */
public class IrcService extends Service {
    private static final String TAG = "IrcService";

    private ExecutorService ircThreads;


    @Override
    @DebugLog
    public void onCreate() {
        super.onCreate();
        ircThreads = Executors.newCachedThreadPool();
    }

    @Override
    @DebugLog
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();


        int i = super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;
    }

    @Override
    @DebugLog
    public void onDestroy() {
        ircThreads.shutdown();
        super.onDestroy();
    }

    @Override
    @DebugLog
    public void onLowMemory() {
        Log.i(TAG, "Low memory");
        super.onLowMemory();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
