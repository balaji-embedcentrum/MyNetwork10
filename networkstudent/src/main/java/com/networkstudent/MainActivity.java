package com.networkstudent;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.networkstudent.model.ProfileData;
import com.networkstudent.widget.CustomPagerAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class MainActivity extends BaseActivity implements OnMapReadyCallback {

    @Bind(R.id.toolbar)
    Toolbar toolBar;
    @Bind(R.id.tabLayout)
    TabLayout tabLayout;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    ParseGeoPoint previousCenterPoint;
    ArrayList<String> categoryArrayList = new ArrayList<>();
    private GoogleMap mMap;
    private CustomPagerAdapter customPagerAdapter;
    private int noOfCategory = 0;
    private int radiusKm = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolBar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("AppSettings");
        query.findInBackground(
                new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> appSettings, ParseException e) {
                        if (e == null) {
                            if (appSettings.size() > 0) {
                                for (int i = 0; i < appSettings.size(); i++) {
                                    ParseObject p = appSettings.get(i);
                                    radiusKm = p.getNumber("radius").intValue();
                                    Log.d("TAG", "radiusKm: " + p.getNumber("radius"));
                                }
                                listenForAddressSearch();
                                prepareCategoryTabs();
                            }
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }
                    }
                }
        );
    }

    private void prepareCategoryTabs() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ProfileCategory");
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> categoryList, ParseException e) {
                if (e == null) {
                    if (categoryList.size() > 0) {
                        for (int i = 0; i < categoryList.size(); i++) {
                            ParseObject p = categoryList.get(i);
                            Log.d("ParseQuery", "category: " + p.getString("category"));
                            categoryArrayList.add(p.getString("category"));
                        }
                        noOfCategory = categoryList.size();
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void listenForAddressSearch() {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i("TAG", "Place: " + place.getLatLng());

                Log.d("TAG", "lat: " + place.getLatLng().latitude + "\n lng: " + place.getLatLng().longitude);
                final double latitude = place.getLatLng().latitude;
                final double longitude = place.getLatLng().longitude;
                final LatLng latLng = new LatLng(latitude, longitude);
                mMap.clear();

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12));
                new Thread() {
                    public void run() {
                        try {
                            getCircleOptions(latLng);
                            getNearByLocation(latitude, longitude);
                        } catch (Exception e) {
                            Log.d("TAG", "Exception: " + e);
                        }
                    }
                }.start();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (!SmartLocation.with(this).location().state().isGpsAvailable()
                && !SmartLocation.with(this).location().state().isNetworkAvailable()) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(this.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(this.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    MainActivity.this.startActivity(myIntent);
                }
            });
            dialog.setNegativeButton(MainActivity.this.getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Toast.makeText(MainActivity.this, "You need to give the location permission.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
            dialog.show();
        } else {
            mMap.setMyLocationEnabled(true);

            /*
            * USER CASE - (1)
            * (1) User opens the app, current location is detected, a circle is drawn,
            *     categoryArrayList is loaded in the list according to the categories and map markers are placed.
            * */
            gettingCurrentLocationAndLoadingData();

            mMap.setOnCameraChangeListener(getCameraChangeListener());

            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    /*
                    * USER CASE - (3)
                    *  3) By pressing the location button, user should come to UseCase1
                    * */
                    gettingCurrentLocationAndLoadingData();
                    return true;
                }
            });
        }
    }

    private void gettingCurrentLocationAndLoadingData() {
        SmartLocation.with(this).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        final double latitude = location.getLatitude();
                        final double longitude = location.getLongitude();
                        final LatLng latLng = new LatLng(latitude, longitude);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));

                        Log.d("TAG", "lat: " + latitude + "\n lng: " + longitude);
                        mMap.clear();

                        new Thread() {
                            public void run() {
                                try {
                                    getCircleOptions(latLng);
                                    getNearByLocation(latitude, longitude);
                                } catch (Exception e) {
                                    Log.d("TAG", "Exception: " + e);
                                }
                            }
                        }.start();
                    }
                });
    }


    public void getCircleOptions(final LatLng latLng) {
        // circle settings
        int radiusM = 2000;
        if (radiusKm != 0)
            radiusM = radiusKm * 1000;// your radius in meters

        // draw circle
        int d = 500; // diameter
        Bitmap bm = Bitmap.createBitmap(d, d, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint();
        p.setColor(ContextCompat.getColor(this, R.color.green));
        c.drawCircle(d / 2, d / 2, d / 2, p);

        // generate BitmapDescriptor from circle Bitmap
        final BitmapDescriptor bmD = BitmapDescriptorFactory.fromBitmap(bm);

        final int finalRadiusM = radiusM;
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                mMap.clear();
                mMap.addGroundOverlay(new GroundOverlayOptions().
                        image(bmD).
                        position(latLng, finalRadiusM * 2, finalRadiusM * 2).
                        transparency(0.6f));
            }
        });
    }

    public GoogleMap.OnCameraChangeListener getCameraChangeListener() {
        return new GoogleMap.OnCameraChangeListener() {
            float minZoom = 12.0f;

            @Override
            public void onCameraChange(CameraPosition position) {
                Log.d("tag", "Zoom: " + position.zoom);

                if (position.zoom <= minZoom) {

                    /*
                    * USER CASE - (2)
                    *  2) When the user the user drags it or Zoomout is more than 2km
                    *     radius from the last location, do the steps as in UseCase1.
                    * */

                    final double lat = position.target.latitude;
                    final double lng = position.target.longitude;
                    final LatLng latLng = new LatLng(lat, lng);

                    ParseGeoPoint currentCenterPoint = new ParseGeoPoint(lat, lng);

                    if (previousCenterPoint != null)
                        if (currentCenterPoint.distanceInKilometersTo(previousCenterPoint) > 2) {
                            Log.d("TAG", "onCameraChange: Distance between to center point > 2 KM");
                            mMap.clear();

                            new Thread() {
                                public void run() {
                                    try {
                                        getCircleOptions(latLng);
                                        getNearByLocation(lat, lng);
                                    } catch (Exception e) {
                                        Log.d("TAG", "Exception: " + e);
                                    }
                                }
                            }.start();
                        } else {
                            /*
                            * USER CASE - (1) 2nd Part
                            *  1) When Zoom-in no changes in the categoryArrayList or circle,
                            *     just zoom-in to see the exact streets and roads.
                            * */
                            Log.d("TAG", "onCameraChange: Distance between to center point < 2 KM");
                        }
                    else {
                        previousCenterPoint = new ParseGeoPoint(lat, lng);
                        Log.d("TAG", "onCameraChange: First Time Loaded");
                    }
                }
            }
        };
    }

    private void getNearByLocation(double lat, double lng) {
        final ArrayList<ProfileData> nearByProfileDataArrayList = new ArrayList<>();

        int withIn = 2;
        if (radiusKm != 0)
            withIn = radiusKm;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ProfileData");
        query.addDescendingOrder("profileViews");
        query.whereWithinKilometers("profileGeopoint", new ParseGeoPoint(lat, lng), withIn);
        query.setLimit(1000);
        query.findInBackground(
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
                                if (nearByProfileDataArrayList.size() > 0) {
                                    addingMarker(nearByProfileDataArrayList);

                                    Log.d("TAG", "done: " + nearByProfileDataArrayList.size());
                                } else
                                    Toast.makeText(MainActivity.this, "No nearby place found.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }
                        customPagerAdapter = new CustomPagerAdapter(getSupportFragmentManager(), noOfCategory, MainActivity.this, categoryArrayList, nearByProfileDataArrayList);
                        viewPager.setAdapter(customPagerAdapter);
                        tabLayout.setupWithViewPager(viewPager);
                        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                    }
                }
        );
    }

    public void addingMarker(ArrayList<ProfileData> profileDataArrayList) {
        if (mMap != null) {
            final LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (int i = 0; i < profileDataArrayList.size(); i++) {
                final ProfileData profileData = profileDataArrayList.get(i);
                if (profileData.getProfileGeopoint() != null) {
                    final LatLng latLng = new LatLng(profileData.getProfileGeopoint().getLatitude(),
                            profileData.getProfileGeopoint().getLongitude());

                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            mMap.addMarker(new MarkerOptions().position(latLng).title(profileData.getProfileName()
                                    + ", " + profileData.getProfileAddr1() + ", " + profileData.getProfileCity()));
                        }
                    });
                    builder.include(latLng);
                }
            }
        }
    }

//    @Override
//    protected void onStart() {
//        if (!EventBus.getDefault().isRegistered(this))
//            EventBus.getDefault().register(this);
//        super.onStart();
//    }
//
//    @Override
//    protected void onDestroy() {
//        EventBus.getDefault().unregister(this);
//        super.onDestroy();
//    }
//
//    public void onEvent(GetSelectedLocationEvent event) {
//        if (event.getLocationData().size() > 0) {
//            if (mMap != null) {
//                final LatLngBounds.Builder builder = new LatLngBounds.Builder();
//
//                for (int i = 0; i < event.getLocationData().size(); i++) {
//                    final ProfileData profileData = event.getLocationData().get(i);
//                    if (profileData.getProfileGeopoint() != null) {
//                        final LatLng latLng = new LatLng(profileData.getProfileGeopoint().getLatitude(),
//                                profileData.getProfileGeopoint().getLongitude());
//
//                        MainActivity.this.runOnUiThread(new Runnable() {
//                            public void run() {
//                                mMap.addMarker(new MarkerOptions().position(latLng).title(profileData.getProfileAddr1()
//                                        + ", " + profileData.getProfileAddr2() + ", " + profileData.getProfileCity()
//                                        + ", " + profileData.getProfileCountry()));
//                            }
//                        });
//                        builder.include(latLng);
//                    }
//                }
//                final LatLngBounds bounds = builder.build();
//                mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
//                    @Override
//                    public void onMapLoaded() {
//                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
//                    }
//                });
//            }
//        }
//    }
}
