package com.balaji.mynetwork10;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.balaji.mynetwork10.model.ProfileData;
import com.balaji.mynetwork10.widget.MyRecyclerAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dream on 13-Dec-15.
 */
public class CategoryListFragment extends Fragment {

    @Bind(R.id.my_recycler_view)
    RecyclerView myRecyclerView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.textViewNoData)
    TextView textViewNoData;
    private RecyclerView.LayoutManager mLayoutManager;
    private MyRecyclerAdapter mAdapter;

    String categoryName;
    Context context;
    ArrayList<ProfileData> profileDataArrayList = new ArrayList<>();

    public CategoryListFragment() {
    }

    public CategoryListFragment(String categoryName, Context context, ArrayList<ProfileData> profileDataArrayList) {
        this.categoryName = categoryName;
        this.context = context;

        this.profileDataArrayList = profileDataArrayList;
        mAdapter = new MyRecyclerAdapter(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_layout, container, false);
        ButterKnife.bind(this, rootView);

        populateList(profileDataArrayList);
        myRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        myRecyclerView.setLayoutManager(mLayoutManager);
        myRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    private void populateList(ArrayList<ProfileData> profileDataArrayList) {
        progressBar.setVisibility(View.VISIBLE);
        ArrayList<ProfileData> categoryWiseProfileList = new ArrayList<>();
        for (int i = 0; i < profileDataArrayList.size(); i++) {
            Log.d("TAG", "populateList: " + profileDataArrayList.get(i).getProfileCategory() + "\ncategoryName: " + categoryName);
            if (profileDataArrayList.get(i).getProfileCategory().equalsIgnoreCase(categoryName)) {
                categoryWiseProfileList.add(profileDataArrayList.get(i));
                Log.d("TAG", "CategoryListFragment: " + profileDataArrayList.size());
            }
        }
        if (categoryWiseProfileList.size() > 0) {
            mAdapter.addAll(categoryWiseProfileList);
        } else {
            textViewNoData.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
