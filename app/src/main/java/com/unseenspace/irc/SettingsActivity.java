package com.unseenspace.irc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * activity for settings
 * needed for settings fragment
 * Created by madsk_000 on 6/18/2015.
 */
public class SettingsActivity extends BaseActivity {
    private static final String EXTRA_IMAGE = "SettingsActivity:image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null)
            setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if(ab != null)
            ab.setDisplayHomeAsUpEnabled(true);
    }

    public static void launch(AppCompatActivity activity)
    {
        Intent intent = new Intent(activity, SettingsActivity.class);

        ActivityCompat.startActivity(activity, intent, null);
    }
}
