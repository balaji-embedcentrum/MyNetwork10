package com.networkstudent.widget;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.networkstudent.CategoryListFragment;
import com.networkstudent.model.ProfileData;

import java.util.ArrayList;

/**
 * Created by Dream on 13-Dec-15.
 */
public class CustomPagerAdapter extends FragmentStatePagerAdapter {
    Context context;
    private int slideCount;
    private ArrayList<String> categoryList = new ArrayList<>();
    ArrayList<ProfileData> profileDataArrayList = new ArrayList<>();

    public CustomPagerAdapter(FragmentManager fm, int slideCount, Context context, ArrayList<String> categoryList, ArrayList<ProfileData> profileDataArrayList) {
        super(fm);
        this.slideCount = slideCount;
        this.context = context;
        this.categoryList.addAll(categoryList);
        this.profileDataArrayList = profileDataArrayList;
    }

    @Override
    public Fragment getItem(int position) {
        Log.i("POS", "position Adapter: " + position);
        return new CategoryListFragment(categoryList.get(position), context, profileDataArrayList);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return new String(categoryList.get(position));
    }

    @Override
    public int getCount() {
        return slideCount;
    }
}
