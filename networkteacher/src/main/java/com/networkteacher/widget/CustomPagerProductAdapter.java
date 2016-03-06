package com.networkteacher.widget;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.networkteacher.ArchivedProductsFragment;
import com.networkteacher.MyProductsFragment;

import java.util.ArrayList;

/**
 * Created by Dream on 13-Dec-15.
 */
public class CustomPagerProductAdapter extends FragmentStatePagerAdapter {
    Context context;
    ArrayList<String> orderStatus = new ArrayList<>();

    public CustomPagerProductAdapter(FragmentManager fm, Context context, ArrayList<String> orderStatus) {
        super(fm);
        this.context = context;
        this.orderStatus = orderStatus;
    }

    @Override
    public Fragment getItem(int position) {
        Log.i("POS", "position Adapter: " + position);
        if (position == 0)
            return new MyProductsFragment();
        else
            return new ArchivedProductsFragment();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return new String(orderStatus.get(position));
    }

    @Override
    public int getCount() {
        return orderStatus.size();
    }
}
