package com.unseenspace.archery.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.unseenspace.archery.PageFragment;

/**
 * Created by chris.black on 6/11/15.
 */
public class TabPagerAdapter extends FragmentPagerAdapter {
    private static final String[] TITLES = new String[] {
            "Target", //ADJUST
            "Detail", //INFO
            "Score"   //GRID
    };

    public static final int NUM_TITLES = TITLES.length;

    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return NUM_TITLES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragment.create(position + 1);
    }
}
