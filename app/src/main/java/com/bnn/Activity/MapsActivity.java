package com.bnn.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bnn.R;
import com.bnn.Task.LocationProvider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

/**
 * Created by ferdinandprasetyo on 11/10/17.
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationProvider.LocationCallback {

    private GoogleMap mMap;
    private double lat, latCenter;
    private double lng, lngCenter;

    ImageView imgBack, imgGetLokasi;
    TextView txtLokasi;

    LocationProvider locationProvider;

    MarkerOptions markerOptions;

    String classIntent = "";

    public interface LocationListener {
        void getLatLong(double lat, double lng);
    }

    public static LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgGetLokasi = (ImageView) findViewById(R.id.imgGetLokasi);
        txtLokasi = (TextView) findViewById(R.id.txtLokasi);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgGetLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (locationListener != null) {

                    if (latCenter != 0.0 && lngCenter != 0.0) {
                        locationListener.getLatLong(latCenter, lngCenter);
                    } else if ((latCenter == 0.0 && lngCenter == 0.0) && (lat != 0.0 && lng != 0.0)) {
                        locationListener.getLatLong(lat, lng);
                    }

                    finish();
                }
            }
        });

        locationProvider = new LocationProvider(MapsActivity.this, this);
        locationProvider.connect();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);

        if (lat != 0.0 && lng != 0.0) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 16));
        }

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
//                markerOptions = new MarkerOptions()
//                        .position(mMap.getCameraPosition().target)
//                        .title("Lat: "+mMap.getCameraPosition().target.latitude+" Long: "+mMap.getCameraPosition().target.longitude);
//                mMap.addMarker(markerOptions);
                latCenter = mMap.getCameraPosition().target.latitude;
                lngCenter = mMap.getCameraPosition().target.longitude;


//                if (txtLokasi.getVisibility() == View.VISIBLE) {
//                    txtLokasi.setVisibility(View.GONE);
//                }
            }
        });
    }

    @Override
    public void handleNewLocation(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();

        if (mMap != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 16));
        }

        if (lat != 0.0 && lng != 0.0) {
            if (locationListener != null) {
                locationListener.getLatLong(latCenter, lngCenter);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

//        Intent back = new Intent();
//        startActivity(back);
        finish();
    }
}

