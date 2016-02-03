package com.balaji.mynetwork10;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.balaji.mynetwork10.model.ProfileData;
import com.balaji.mynetwork10.widget.MyRecyclerFavoritesAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FavoritesActivity extends AppCompatActivity {

    @Bind(R.id.my_recycler_view)
    RecyclerView myRecyclerView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.textViewNoData)
    TextView textViewNoData;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private RecyclerView.LayoutManager mLayoutManager;
    private MyRecyclerFavoritesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fevorites);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mAdapter = new MyRecyclerFavoritesAdapter(this);
        myRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(mLayoutManager);
        myRecyclerView.setAdapter(mAdapter);

        gettingProfileList();
    }

    private void gettingProfileList() {
        final ArrayList<ProfileData> nearByProfileDataArrayList = new ArrayList<>();

        ParseQuery<ParseObject> queryProfileViews = ParseQuery.getQuery("UserFavorites");
        queryProfileViews.whereEqualTo("userID", 1234567890);

        ParseQuery<ParseObject> queryProfileData = ParseQuery.getQuery("ProfileData");
        queryProfileData.setLimit(1000);
        queryProfileData.addDescendingOrder("profileViews");

        queryProfileData.whereMatchesKeyInQuery("profileCode", "profileCode", queryProfileViews);
        queryProfileData.findInBackground(
                new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> profileList, ParseException e) {
                        if (e == null) {
                            if (profileList.size() > 0) {
                                for (int i = 0; i < profileList.size(); i++) {
                                    ParseObject p = profileList.get(i);

                                    ProfileData profileData = new ProfileData();
                                    profileData.setObjectId(p.getObjectId());
                                    profileData.setUserId(p.getString("userID"));
                                    profileData.setProfileCode(p.getInt("profileCode"));
                                    profileData.setProfileName(p.getString("profileName"));
                                    profileData.setProfileAddr1(p.getString("profileAddr1"));
                                    profileData.setProfileAddr2(p.getString("profileAddr2"));
                                    profileData.setProfileCity(p.getString("profileCity"));
                                    profileData.setProfileZip(p.getInt("profileZip"));
                                    profileData.setProfileState(p.getString("profileState"));
                                    profileData.setProfileCountry(p.getString("profileCountry"));
                                    profileData.setProfileGeopoint(p.getParseGeoPoint("profileGeopoint"));
                                    profileData.setProfileImage(p.getParseFile("profileImage"));
                                    profileData.setProfileViews(p.getInt("profileViews"));
                                    profileData.setProfileCategory(p.getString("profileCategory"));

                                    nearByProfileDataArrayList.add(profileData);
                                }
                                if (nearByProfileDataArrayList.size() > 0)
                                    populateList(nearByProfileDataArrayList);
                            }
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_favorites, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_favorites:
                // do stuff, like showing settings fragment
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void populateList(ArrayList<ProfileData> profileDataArrayList) {
        progressBar.setVisibility(View.VISIBLE);

        if (profileDataArrayList.size() > 0) {
            mAdapter.addAll(profileDataArrayList);
        } else {
            textViewNoData.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.INVISIBLE);
    }
}
