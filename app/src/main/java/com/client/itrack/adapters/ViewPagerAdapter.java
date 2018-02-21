package com.client.itrack.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by sony on 24-06-2016.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    String [] tags ;
    public ViewPagerAdapter(FragmentManager fm,String[] tags) {
        super(fm);
        this.tags= tags;
    }

    @Override
    public Fragment getItem(int position) {
        return this.getItem(position);
    }

    @Override
    public int getCount() {
        return tags.length;
    }
}
