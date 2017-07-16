package com.example.damia.directionsapitest.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.damia.directionsapitest.R;
import com.example.damia.directionsapitest.data.DirectionsData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, DirectionsData.OnDownloadCompleteListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private final int LOCATION_PERMISSION = 1;
    MarkerOptions user;
    private int optionsPosition = 0;

    private ArrayList<MarkerOptions> markerOptions;

    DirectionsData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.damia.directionsapitest.R.layout.activity_maps);

        addMapFragment();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(LocationServices.API)
                .build();

        markerOptions = new ArrayList<>();

        data = DirectionsData.getInstance(this);

        for(int i = 0; i < 2; i++)
            markerOptions.add(null);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();

                MarkerOptions options = new MarkerOptions().position(latLng);
                options.title(optionsPosition + "");
                markerOptions.set(0, markerOptions.get(1));
                markerOptions.add(1, options);

                mMap.addMarker(markerOptions.get(0));
                mMap.addMarker(markerOptions.get(1));

                Log.v("MAPS", "" + optionsPosition);
                Log.v("lat", "" + options.getPosition().latitude);
                Log.v("lon", "" + options.getPosition().longitude);
                data.downloadDirectionsData(markerOptions);
            }
        });
    }

    @Override
    public void downloadComplete(List<LatLng> points) {
        PolylineOptions polylineOptions = new PolylineOptions();
        for(int i = 0; i < points.size(); i++){
            polylineOptions.add(points.get(i));
        }
        mMap.addPolyline(polylineOptions);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Log.v("PERMS", "requesting permissions");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
        } else {
            Log.v("PERMS", "permissions already granted");
            startLocationServices();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        user = new MarkerOptions().position(latLng);
        mMap.addMarker(user);
        markerOptions.add(1, user);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case LOCATION_PERMISSION:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.v("PERMS", "Permissions granted by user");
                    startLocationServices();
                }else{
                    Log.v("PERMS", "Permissions not granted by user");
                }
            }
        }
    }

    public void addMapFragment(){
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void startLocationServices(){
        Log.v("REQ", "Location services started");
        try{
            LocationRequest request = new LocationRequest().setPriority(LocationRequest.PRIORITY_LOW_POWER);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, this);
        }catch(SecurityException e){
            Log.v("SECURITY", e.getLocalizedMessage().toString());
        }
    }

}
