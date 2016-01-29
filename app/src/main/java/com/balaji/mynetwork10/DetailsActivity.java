package com.balaji.mynetwork10;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.balaji.mynetwork10.event.PassProfileDataEvent;
import com.balaji.mynetwork10.model.ProfileData;
import com.balaji.mynetwork10.utils.ReusableClass;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected GoogleApiClient mGoogleApiClient;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.buttonNavigate)
    Button buttonNavigate;
    @Bind(R.id.textViewName)
    TextView textViewName;
    @Bind(R.id.textViewAddressOne)
    TextView textViewAddressOne;
    @Bind(R.id.textViewAddressTwo)
    TextView textViewAddressTwo;
    @Bind(R.id.textViewCity)
    TextView textViewCity;
    @Bind(R.id.textViewZipcode)
    TextView textViewZipcode;
    private ProgressDialog dialog;
    private List<LatLng> pontos;
    private GoogleMap mMap;
    private ProfileData profileData;
    //private ProgressDialog progressDialog;
    private String LOG_TAG = "TAG";
    private ArrayList<Polyline> polylines;
    private int[] colors = new int[]{R.color.colorPrimary, R.color.route2, R.color.route3, R.color.route4, R.color.route5};
    private boolean navigated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        MapsInitializer.initialize(this);
        mGoogleApiClient.connect();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
                Intent i = new Intent(this, FavoritesActivity.class);
                startActivity(i);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (!navigated) {
            if (!SmartLocation.with(this).location().state().isGpsAvailable()
                    && !SmartLocation.with(this).location().state().isNetworkAvailable()) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage(this.getResources().getString(R.string.gps_network_not_enabled));
                dialog.setPositiveButton(this.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        DetailsActivity.this.startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton(DetailsActivity.this.getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Toast.makeText(DetailsActivity.this, "You need to give the location permission.", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
                dialog.show();
            } else {
                if (ReusableClass.haveNetworkConnection(this)) {
                    SmartLocation.with(this).location()
                            .oneFix()
                            .start(new OnLocationUpdatedListener() {
                                @Override
                                public void onLocationUpdated(Location location) {
                                    final double latitude = location.getLatitude();
                                    final double longitude = location.getLongitude();
                                    final LatLng latLng = new LatLng(latitude, longitude);
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));


                                    ProfileData profileData = new Gson().fromJson(ReusableClass.getFromPreference("profileDataObj", DetailsActivity.this), ProfileData.class);

                                    Log.d("TAG", "FROM lat: " + latitude + " FROM lng: " + longitude);
                                    Log.d("TAG", "TO lat: " + profileData.getProfileGeopoint().getLatitude()
                                            + " TO lng: " + profileData.getProfileGeopoint().getLongitude());
                                    //mMap.clear();


                                    if (profileData != null) {
                                        textViewName.setText(profileData.getProfileName());
                                        textViewAddressOne.setText(profileData.getProfileAddr1());
                                        textViewAddressTwo.setText(profileData.getProfileAddr2());
                                        textViewCity.setText(profileData.getProfileCity());
                                        //textViewZipcode.setText(profileData.getProfileZip());

                                        final LatLng start = new LatLng(latitude, longitude);
                                        final LatLng end = new LatLng(profileData.getProfileGeopoint().getLatitude()
                                                , profileData.getProfileGeopoint().getLongitude());

                                        MarkerOptions options = new MarkerOptions();
                                        options.position(start);
                                        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
                                        mMap.addMarker(options);

                                        // End marker
                                        options = new MarkerOptions();
                                        options.position(end);
                                        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));
                                        mMap.addMarker(options);

                                        polylines = new ArrayList<>();
                                        //progressDialog = ProgressDialog.show(DetailsActivity.this, "Please wait.","Fetching route information.", true);
                                        Routing routing = new Routing.Builder()
                                                .travelMode(AbstractRouting.TravelMode.DRIVING)
                                                .withListener(DetailsActivity.this)
                                                .alternativeRoutes(true)
                                                        //.key(getResources().getString(R.string.google_maps_key))
                                                .waypoints(start, end)
                                                .build();
                                        routing.execute();

                                        buttonNavigate.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                openingNavigation(start.latitude, start.longitude, end.latitude, end.longitude);
                                            }
                                        });
                                    }
                                    navigated = true;
                                }
                            });
                } else
                    Toast.makeText(this, R.string.error_internet_connection, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onStart() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().registerSticky(this);
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onEventMainThread(PassProfileDataEvent event) {
        profileData = event.getProfileData();

        ReusableClass.saveInPreference("profileDataObj", new Gson().toJson(profileData), DetailsActivity.this);

        EventBus.getDefault().removeStickyEvent(event);
    }

    public void openingNavigation(double latitude_currrent, double longitude_current, double latitude_destination, double longitude_destination) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=" + latitude_destination + "," + longitude_destination + ""));
        startActivity(intent);
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        // The Routing request failed
        //progressDialog.dismiss();
        if (e != null) {
            //Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Sorry no route found.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {
        // The Routing Request starts
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        //progressDialog.dismiss();
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
//        for (int i = 0; i < route.size(); i++) {

        //In case of more than 5 alternative routes
        int colorIndex = 0 % colors.length;

        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(getResources().getColor(colors[colorIndex]));
        polyOptions.width(10 + 0 * 3);
        polyOptions.addAll(route.get(0).getPoints());
        Polyline polyline = mMap.addPolyline(polyOptions);
        polylines.add(polyline);

//            Toast.makeText(getApplicationContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void onRoutingCancelled() {
        Log.i(LOG_TAG, "Routing was cancelled.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.v(LOG_TAG, connectionResult.toString());
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
