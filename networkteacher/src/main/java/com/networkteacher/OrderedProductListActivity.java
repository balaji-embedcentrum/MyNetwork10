package com.networkteacher;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.networkteacher.widget.CustomPagerOrderAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OrderedProductListActivity extends BaseActivity {

    @Bind(R.id.tabLayout)
    TabLayout tabLayout;
    @Bind(R.id.viewPager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordered_product_list);
        ButterKnife.bind(this);

        ArrayList<String> orderStatus = new ArrayList<>();
        orderStatus.add("Ordered");
        orderStatus.add("Picked Up");

        CustomPagerOrderAdapter customPagerOrderAdapter = new CustomPagerOrderAdapter(getSupportFragmentManager(), OrderedProductListActivity.this, orderStatus);
        viewPager.setAdapter(customPagerOrderAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }
}
