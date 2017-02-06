package com.tarunisrani.dailykharcha.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class CustomPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<String> tabtitles = new ArrayList<>();
    private ArrayList<Fragment> mFragmentList = new ArrayList<>();

    public void addItem(Fragment fragment, String title){
        mFragmentList.add(fragment);
        tabtitles.add(title);
    }

    public CustomPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles.get(position);
    }

}