package com.unseenspace.irc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hugo.weaving.DebugLog;

/**
 * A Service that will offload creating a PircBotX or connecting to an Irc
 * Created by madsk_000 on 11/28/2015.
 */
public class IrcService extends Service {
    /**
     * A tag for logging.
     */
    private static final String TAG = "IrcService";

    /**
     * Threads for Irc Connections.
     */
    private ExecutorService ircThreads;


    /**
     * @{inheritDoc}
     */
    @Override
    @DebugLog
    public void onCreate() {
        super.onCreate();
        ircThreads = Executors.newCachedThreadPool();
    }

    /**
     * @{inheritDoc}
     * @param intent @{inheritDoc}
     * @param flags @{inheritDoc}
     * @param startId @{inheritDoc}
     * @return @{inheritDoc}
     */
    @Override
    @DebugLog
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();


        int i = super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    @DebugLog
    public void onDestroy() {
        ircThreads.shutdown();
        super.onDestroy();
    }

    /**
     * @{inheritDoc}
     */
    @Override
    @DebugLog
    public void onLowMemory() {
        Log.i(TAG, "Low memory");
        super.onLowMemory();
    }

    /**
     * @{inheritDoc}
     * @param intent @{inheritDoc}
     * @return @{inheritDoc}
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
