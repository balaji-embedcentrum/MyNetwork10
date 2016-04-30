package com.networkstudent;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.networkstudent.model.Product;
import com.networkstudent.utils.ReusableClass;
import com.networkstudent.widget.ArchivedProductListRecyclerAdapter;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class ProductShowcaseActivity extends BaseActivity {

    private static final String TAG = "MyTAG";
    @Bind(R.id.my_recycler_view)
    RecyclerView myRecyclerView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.textViewNoData)
    TextView textViewNoData;
    @Bind(R.id.progress_container)
    LinearLayout progressContainer;
    @Bind(R.id.imageViewBackProduct)
    ImageView imageViewBackProduct;
    @Bind(R.id.ImageViewCall)
    ImageView ImageViewCall;
    @Bind(R.id.textViewAddress)
    TextView textViewAddress;
    @Bind(R.id.textViewDescription)
    TextView textViewDescription;
    @Bind(R.id.textViewCost)
    TextView textViewCost;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.imageViewFav)
    ImageView imageViewFav;
    @Bind(R.id.textViewDistance)
    TextView textViewDistance;


    private RecyclerView.LayoutManager mLayoutManager;
    private ArchivedProductListRecyclerAdapter mAdapter;
    private String profileCode;
    private String userId;
    private Location latLngTo;
    private Location latLngFrom;
    private String profileObjId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_showcase);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        profileCode = getIntent().getStringExtra("profileCode");
        userId = getIntent().getStringExtra("userId");
        profileObjId = getIntent().getStringExtra("profileObjId");

        ReusableClass.saveInPreference("profileCode", profileCode, this);
        ReusableClass.saveInPreference("userID", userId, this);
        ReusableClass.saveInPreference("profileObjId", profileObjId, this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (latLngFrom != null && latLngTo != null)
                    openingNavigation(latLngFrom.getLatitude(), latLngFrom.getLongitude(), latLngTo.getLatitude(), latLngTo.getLongitude());
            }
        });

        mAdapter = new ArchivedProductListRecyclerAdapter(this);
        myRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this, 2);
        myRecyclerView.setLayoutManager(mLayoutManager);
        myRecyclerView.setAdapter(mAdapter);
    }

    private void updateProfileView() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ProfileData");
        query.getInBackground(ReusableClass.getFromPreference("profileObjId", this), new GetCallback<ParseObject>() {
            public void done(ParseObject profileViews, ParseException e) {
                if (e == null) {
                    profileViews.increment("profileViews");
                    latLngTo = new Location("newlocation");
                    latLngTo.setLatitude(profileViews.getParseGeoPoint("profileGeopoint").getLatitude());
                    latLngTo.setLongitude(profileViews.getParseGeoPoint("profileGeopoint").getLongitude());

                    if (latLngFrom != null) {
                        textViewDistance.setText(new DecimalFormat("##.##").format((latLngFrom.distanceTo(latLngTo) / 1000)) + " KM");
                        textViewDistance.setVisibility(View.VISIBLE);
                    } else {
                        textViewDistance.setVisibility(View.GONE);
                    }

                    profileViews.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Log.d("TAG", " UPDATED");
                            if (e == null) {
                                addingHeaderData();
                            } else {

                            }
                        }
                    });
                }
            }
        });
    }

    private void selectFevIcon() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserFavorites");
        query.whereEqualTo("profileCode", Integer.parseInt(ReusableClass.getFromPreference("profileCode", ProductShowcaseActivity.this)));
        query.whereEqualTo("userID", Integer.parseInt(ReusableClass.getFromPreference("userID", ProductShowcaseActivity.this)));
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> fevList, ParseException e) {
                if (e == null) {
                    Log.d("score", "Retrieved " + fevList.size() + " scores");
                    if (fevList.size() > 0)
                        imageViewFav.setImageResource(R.drawable.fav_btn_active);
                    else
                        imageViewFav.setImageResource(R.drawable.fav_btn_inactive);
                } else
                    Log.d("score", "Error: " + e.getMessage());

            }
        });
    }

    public void openingNavigation(double latitude_currrent, double longitude_current, double latitude_destination, double longitude_destination) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=" + latitude_destination + "," + longitude_destination + ""));
        startActivity(intent);
    }

    private void addingHeaderData() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ProfileData");
        query.whereEqualTo("profileCode", Integer.parseInt(ReusableClass.getFromPreference("profileCode", ProductShowcaseActivity.this)));

        query.findInBackground(
                new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> profileList, ParseException e) {
                        if (e == null) {
                            if (profileList.size() > 0) {
                                for (int i = 0; i < profileList.size(); i++) {
                                    ParseObject p = profileList.get(i);

                                    String address = "";
                                    if (p.getString("profileAddr1") != null)
                                        address = p.getString("profileAddr1");
                                    if (p.getString("profileAddr2") != null)
                                        address = address + ", " + p.getString("profileAddr2");
                                    if (p.getString("profileCity") != null)
                                        address = address + ", " + p.getString("profileCity");
                                    if (p.getString("profileZip") != null)
                                        address = address + ", " + p.getString("profileZip");
                                    if (p.getString("profileState") != null)
                                        address = address + ", " + p.getString("profileState");
                                    if (p.getString("profileCountry") != null)
                                        address = address + ", " + p.getString("profileCountry");

                                    latLngTo = new Location("newlocation");
                                    latLngTo.setLatitude(p.getParseGeoPoint("profileGeopoint").getLatitude());
                                    latLngTo.setLongitude(p.getParseGeoPoint("profileGeopoint").getLongitude());

                                    textViewAddress.setText(address);
                                    ImageViewCall.setTag(p.getString("profilePhone"));

                                    if (p.getParseFile("profileImage") != null) {
                                        if (p.getParseFile("profileImage").getUrl() != null)
                                            Glide.with(ProductShowcaseActivity.this).load(p.getParseFile("profileImage").getUrl())
                                                    .centerCrop()
                                                    .placeholder(R.drawable.placeholder)
                                                    .crossFade()
                                                    .into(imageViewBackProduct);
                                    }
                                    if (p.getNumber("profileViews") != null)
                                        textViewCost.setText("View - " + p.getNumber("profileViews"));
                                    else
                                        textViewCost.setText("View - 0");

                                }
                            } else {
                                Log.d(TAG, "done: No Value");
                            }
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }
                    }
                }
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        textViewNoData.setVisibility(View.INVISIBLE);
        progressContainer.setVisibility(View.VISIBLE);
        loadData();
        selectFevIcon();
    }

    private void loadData() {
        ImageViewCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ImageViewCall.getTag() != null) {
                    Intent dial = new Intent();
                    dial.setAction("android.intent.action.DIAL");
                    dial.setData(Uri.parse("tel:" + ImageViewCall.getTag()));
                    startActivity(dial);
                } else
                    Toast.makeText(ProductShowcaseActivity.this, "No no available.", Toast.LENGTH_LONG).show();
            }
        });
        updateProfileView();
        SmartLocation.with(this).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        latLngFrom = location;
                        if (latLngTo != null) {
                            textViewDistance.setText(new DecimalFormat("##.##").format((latLngFrom.distanceTo(latLngTo) / 1000)) + " KM");
                            textViewDistance.setVisibility(View.VISIBLE);
                        } else {
                            textViewDistance.setVisibility(View.GONE);
                        }
                    }
                });


        final ArrayList<Product> productArrayList = new ArrayList<>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ProductData");
        query.whereEqualTo("ProfileCode", Integer.parseInt(ReusableClass.getFromPreference("profileCode", ProductShowcaseActivity.this)));
        query.whereEqualTo("ProductStatus", "Active");
        query.findInBackground(
                new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> profileList, ParseException e) {
                        if (e == null) {
                            if (profileList.size() > 0) {
                                for (int i = 0; i < profileList.size(); i++) {
                                    ParseObject p = profileList.get(i);

                                    Log.d(TAG, "done: " + p.getString("ProductDescription"));
                                    Product product = new Product();
                                    product.setObjectId(p.getObjectId());
                                    product.setProductDescription(p.getString("ProductDescription"));
                                    product.setProductStatus(p.getString("ProductStatus"));
                                    product.setProductSummary(p.getString("ProductSummary"));
                                    product.setProductStatus(p.getString("ProductStatus"));
                                    product.setProductCost(p.getInt("ProductCost"));
                                    product.setProfileCode(p.getInt("ProductCode"));
                                    product.setObjectId(p.getObjectId());
                                    if (p.getParseFile("ProductFoto1") != null)
                                        product.setProductFoto1(p.getParseFile("ProductFoto1").getUrl().toString());
                                    if (p.getParseFile("ProductFoto2") != null)
                                        product.setProductFoto2(p.getParseFile("ProductFoto2").getUrl().toString());
                                    if (p.getParseFile("ProductFoto3") != null)
                                        product.setProductFoto3(p.getParseFile("ProductFoto3").getUrl().toString());

                                    productArrayList.add(product);
                                }
                                mAdapter.addAll(productArrayList);
                            } else {
                                Log.d(TAG, "done: No Value");
                                textViewNoData.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }
                        progressContainer.setVisibility(View.INVISIBLE);
                    }
                }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.imageViewFav)
    public void addingToFav(final View v) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserFavorites");
        query.whereEqualTo("profileCode", Integer.parseInt(ReusableClass.getFromPreference("profileCode", ProductShowcaseActivity.this)));
        query.whereEqualTo("userID", Integer.parseInt(ReusableClass.getFromPreference("userID", ProductShowcaseActivity.this)));

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> fevList, ParseException e) {
                if (e == null) {
                    Log.d("score", "Retrieved " + fevList.size() + " scores");
                    if (fevList.size() > 0) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserFavorites");
                        query.whereEqualTo("profileCode", Integer.parseInt(ReusableClass.getFromPreference("profileCode", ProductShowcaseActivity.this)));
                        query.whereEqualTo("userID", Integer.parseInt(ReusableClass.getFromPreference("userID", ProductShowcaseActivity.this)));
                        query.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> invites, ParseException e) {
                                if (e == null) {
                                    // iterate over all messages and delete them
                                    for (ParseObject invite : invites) {
                                        invite.deleteInBackground();
                                    }
                                    Toast.makeText(ProductShowcaseActivity.this, "Removed from favorites.", Toast.LENGTH_LONG).show();
                                    imageViewFav.setImageResource(R.drawable.fav_btn_inactive);
                                } else {
                                    Toast.makeText(ProductShowcaseActivity.this, "Sorry unable to remove please try again.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        ParseObject gameScore = new ParseObject("UserFavorites");
                        gameScore.put("userID", Integer.parseInt(ReusableClass.getFromPreference("userID", ProductShowcaseActivity.this)));
                        gameScore.put("profileCode", Integer.parseInt(ReusableClass.getFromPreference("profileCode", ProductShowcaseActivity.this)));
                        gameScore.put("StudentPhone", ReusableClass.getFromPreference("session", ProductShowcaseActivity.this));
                        gameScore.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Log.d("TAG", " UPDATED");
                                if (e == null) {
                                    imageViewFav.setImageResource(R.drawable.fav_btn_active);
                                    Snackbar snackbar = Snackbar
                                            .make(v, "Added to your Favorites.", Snackbar.LENGTH_LONG)
                                            .setAction("UNDO", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    imageViewFav.performClick();
                                                    imageViewFav.setImageResource(R.drawable.fav_btn_inactive);
                                                }
                                            });
                                    snackbar.show();
                                } else {
                                    Toast.makeText(ProductShowcaseActivity.this, "Sorry unable to undo please try again.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }
}
