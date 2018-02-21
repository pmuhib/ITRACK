package com.client.itrack.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Muhib.
 * Contact Number : +91 9796173066
 */
public class PageAdapter extends FragmentPagerAdapter {
    int mNumTabs;
    Fragment tab1,tab2;
    public PageAdapter(FragmentManager fm, int mNumTabs, Fragment tabA, Fragment tabB) {
        super(fm);
        this.mNumTabs=mNumTabs;
        tab1=tabA;
        tab2=tabB;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return tab1;
            case 1:
                return tab2;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return mNumTabs;
    }
}
