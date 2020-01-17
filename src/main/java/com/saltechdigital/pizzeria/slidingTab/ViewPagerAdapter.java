package com.saltechdigital.pizzeria.slidingTab;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.saltechdigital.pizzeria.CompletedOrder;
import com.saltechdigital.pizzeria.PendingOrder;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Jeanpaul Tossou on 02/11/2016.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private CharSequence[] Titles; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    private int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm, CharSequence[] mTitles, int mNumbOfTabsumb) {
        super(fm);
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
    }

    //This method return the fragment for the every position in the View Pager
    @NotNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new PendingOrder();
            case 1:
                return new CompletedOrder();
        }
        return new Fragment();
    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}