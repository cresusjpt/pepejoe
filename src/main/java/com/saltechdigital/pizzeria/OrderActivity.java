package com.saltechdigital.pizzeria;

import android.content.Context;
import android.os.Bundle;

import com.marcinorlowski.fonty.Fonty;
import com.saltechdigital.pizzeria.slidingTab.SlidingTabLayout;
import com.saltechdigital.pizzeria.slidingTab.ViewPagerAdapter;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

public class OrderActivity extends AppCompatActivity {

    private ViewPager pager;
    private Context context;
    private ViewPagerAdapter adapter;
    private SlidingTabLayout tabs;

    private CharSequence[] mTitle;
    private int numOfTabs = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        context = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.menu_orders);

        CharSequence[] title = {getString(R.string.pending_order_title), getString(R.string.completed_order_title)};

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), title, numOfTabs);

        // Assigning ViewPager View and setting the adapter
        pager = findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = findViewById(R.id.tabs);

        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(position -> ContextCompat.getColor(context, R.color.colorWhite));

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

        Fonty.setFonts(this);
    }

    private void inflateViews() {

    }
}
