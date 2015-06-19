package com.unseenspace.archery;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.unseenspace.archery.adapters.TabPagerAdapter;

/**
 * Created by chris.black on 6/11/15.
 */
public class ShootActivity extends BaseActivity {

    private static final String EXTRA_IMAGE = "ShootActivity:image";
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoot);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        final ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        final FragmentPagerAdapter adapter = new TabPagerAdapter(this.getSupportFragmentManager());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        //mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        //tabLayout.setTabsFromPagerAdapter(adapter);
        //tabLayout.getTabAt(0).setIcon(R.drawable.ic_adjust_white_48dp);
        //tabLayout.getTabAt(1).setIcon(R.drawable.ic_info_outline_white_48dp);
        //tabLayout.getTabAt(2).setIcon(R.drawable.ic_grid_on_white_24dp);
        tabLayout.setupWithViewPager(viewPager);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        ImageView image = (ImageView) findViewById(R.id.image);
        ViewCompat.setTransitionName(image, EXTRA_IMAGE);
        image.setImageResource(getIntent().getIntExtra(EXTRA_IMAGE, R.drawable.ic_android_white_48dp));
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                // Reverse the animation back to the previous view.
                if(Build.VERSION.SDK_INT >= 21)
                    finishAfterTransition();
                else
                    finish();

                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void launch(AppCompatActivity activity, View transitionView) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionView, EXTRA_IMAGE);

        Intent intent = new Intent(activity, ShootActivity.class);

        int drawable = R.drawable.ic_adjust_white_48dp;
        if (transitionView.getTag() instanceof Integer)
            drawable = (Integer) transitionView.getTag();
        intent.putExtra(EXTRA_IMAGE, drawable);

        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }
}
