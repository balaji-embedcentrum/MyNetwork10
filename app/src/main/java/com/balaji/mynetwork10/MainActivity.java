package com.balaji.mynetwork10;

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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.balaji.mynetwork10.model.ProfileData;
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
import com.parse.FindCallback;
import com.parse.Parse;
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

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Bind(R.id.toolBar)
    Toolbar toolBar;
    @Bind(R.id.tabLayout)
    TabLayout tabLayout;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolBar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        listenForAddressSearch();
    }

    private void listenForAddressSearch() {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("TAG", "Place: " + place.getLatLng());

                Log.d("TAG", "lat: " + place.getLatLng().latitude + "\n lng: " + place.getLatLng().longitude);
                final double latitude = place.getLatLng().latitude;
                final double longitude = place.getLatLng().longitude;
                final LatLng latLng = new LatLng(latitude, longitude);
                mMap.clear();

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 13));
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
            SmartLocation.with(this).location()
                    .oneFix()
                    .start(new OnLocationUpdatedListener() {
                        @Override
                        public void onLocationUpdated(Location location) {
                            final double latitude = location.getLatitude();
                            final double longitude = location.getLongitude();
                            final LatLng latLng = new LatLng(latitude, longitude);
                            //mMap.addMarker(new MarkerOptions().position(latLng));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

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
            mMap.setOnCameraChangeListener(getCameraChangeListener());
            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude()), 13));
                    return true;
                }
            });
        }
    }


    public void getCircleOptions(final LatLng latLng) {
        // circle settings
        final int radiusM = 2000;// your radius in meters

        // draw circle
        int d = 500; // diameter
        Bitmap bm = Bitmap.createBitmap(d, d, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint();
        p.setColor(ContextCompat.getColor(this, R.color.green));
        c.drawCircle(d / 2, d / 2, d / 2, p);

        // generate BitmapDescriptor from circle Bitmap
        final BitmapDescriptor bmD = BitmapDescriptorFactory.fromBitmap(bm);

        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                mMap.addGroundOverlay(new GroundOverlayOptions().
                        image(bmD).
                        position(latLng, radiusM * 2, radiusM * 2).
                        transparency(0.4f));
            }
        });
    }

    public GoogleMap.OnCameraChangeListener getCameraChangeListener() {
        return new GoogleMap.OnCameraChangeListener() {
            float minZoom = 13.0f;

            @Override
            public void onCameraChange(CameraPosition position) {
                Log.d("tag", "Zoom: " + position.zoom);
                if (position.zoom < minZoom)
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(minZoom));
            }
        };
    }

    private void getNearByLocation(double lat, double lng) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ProfileData");
        query.whereWithinKilometers("profileGeopoint", new ParseGeoPoint(lat, lng), 2);
        query.setLimit(1000);
        query.findInBackground(
                new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> profileList, ParseException e) {
                        if (e == null) {
                            if (profileList.size() > 0) {
                                final ArrayList<ProfileData> nearByProfileDataArrayList = new ArrayList<>();

                                for (int i = 0; i < profileList.size(); i++) {
                                    ParseObject p = profileList.get(i);

                                    ProfileData profileData = new ProfileData();
                                    profileData.setObjectId(p.getString("objectId"));
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
                    }
                }
        );
    }

    public void addingMarker(ArrayList<ProfileData> profileDataArrayList) {
        if (mMap != null) {
            final LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (int i = 0; i < profileDataArrayList.size(); i++) {
                final ProfileData profileData = profileDataArrayList.get(i);
                //Log.i("TAG", "Group: " + profileData.getProfileCategory());
                if (profileData.getProfileGeopoint() != null) {
                    final LatLng latLng = new LatLng(profileData.getProfileGeopoint().getLatitude(),
                            profileData.getProfileGeopoint().getLongitude());

                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            mMap.addMarker(new MarkerOptions().position(latLng).title(profileData.getProfileAddr1()
                                    + ", " + profileData.getProfileAddr2() + ", " + profileData.getProfileCity()
                                    + ", " + profileData.getProfileCountry()));
                        }
                    });
                    builder.include(latLng);
                }
            }
        }
    }
}
