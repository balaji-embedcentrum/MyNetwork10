package com.networkteacher;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.networkteacher.widget.CustomPagerOrderAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OrderedProductListActivity extends BaseActivity {

    @Bind(R.id.tabLayout)
    TabLayout tabLayout;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordered_product_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final ArrayList<String> orderStatus = new ArrayList<>();

        ParseQuery<ParseObject> queryOrder = ParseQuery.getQuery("OrderStatus");
        queryOrder.findInBackground(
                new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> orderList, ParseException e) {
                        if (e == null) {
                            if (orderList.size() > 0) {
                                for (ParseObject singleOrder : orderList) {
                                    orderStatus.add(singleOrder.getString("OrderStatusCategory"));
                                }
                                CustomPagerOrderAdapter customPagerOrderAdapter = new CustomPagerOrderAdapter(getSupportFragmentManager(), OrderedProductListActivity.this, orderStatus);
                                viewPager.setAdapter(customPagerOrderAdapter);
                                tabLayout.setupWithViewPager(viewPager);
                                tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                            } else {
                                Log.d("TAG", "done: No Value");
                            }
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }
                    }
                }
        );
    }
}
