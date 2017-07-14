package com.example.damia.directionsapitest.data;

import android.util.Log;

import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by damia on 7/14/2017.
 */

public class DirectionsData {

    final String URL_BASE = "https://maps.googleapis.com/maps/api/directions/json";
    final String URL_ORIGIN = "?origin=";
    final String URL_DESTINATION = "&destination=";
    final String URL_API_KEY = "&key=AIzaSyD-d8GLSFKBNN6cojQbeScvpTRtkWI4eYg";

    public void downloadDirectionsData(ArrayList<MarkerOptions> markerOptions){
        final String coordinates = URL_ORIGIN + markerOptions.get(0).getPosition().latitude + "," + markerOptions.get(0).getPosition().longitude
                + URL_DESTINATION + markerOptions.get(1).getPosition().latitude + "," + markerOptions.get(1).getPosition().longitude;

        final String strRequest = URL_BASE + coordinates + URL_API_KEY;
        Log.v("COORDS", strRequest);
    }
}
