package com.networkteacher;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.networkteacher.widget.CustomPagerProductAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @Bind(R.id.tabLayout)
    TabLayout tabLayout;
    @Bind(R.id.viewPager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ArrayList<String> tabs = new ArrayList<>();
        tabs.add("Active Products");
        tabs.add("Archived Products");

        CustomPagerProductAdapter customPagerProductAdapter = new CustomPagerProductAdapter(getSupportFragmentManager(), MainActivity.this, tabs);
        viewPager.setAdapter(customPagerProductAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }
}
