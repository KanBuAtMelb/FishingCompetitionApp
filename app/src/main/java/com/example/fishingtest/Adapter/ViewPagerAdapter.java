package com.example.fishingtest.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Completed by Kan Bu on 8/06/2019.
 *
 * Fragment Pager Adapter for the Home Page to host two fragments
 * of "DiscoveryActivity" and "MyCompetitionActivity".
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    // Local variables
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentListTittle = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentListTittle.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentListTittle.get(position);
    }

    // Add fragment to the fragment list
    public void AddFragment(Fragment fragment, String title){
        fragmentList.add(fragment);
        fragmentListTittle.add(title);
    }
}

