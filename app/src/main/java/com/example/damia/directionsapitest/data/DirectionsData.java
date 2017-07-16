package com.example.damia.directionsapitest.data;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by damia on 7/16/2017.
 */

public class DirectionsData {

    final String URL_BASE = "https://maps.googleapis.com/maps/api/directions/json";
    final String URL_ORIGIN = "?origin=";
    final String URL_DESTINATION = "&destination=";
    final String URL_API_KEY = "&key=AIzaSyD-d8GLSFKBNN6cojQbeScvpTRtkWI4eYg";

    public OnDownloadCompleteListener onDownloadComplete;
    public OnJSONExceptionListener onJSONExceptionListener;

    public interface OnDownloadCompleteListener{
        void downloadComplete(List<LatLng> points);
    }

    public interface OnJSONExceptionListener{
        void onJSONException();
    }

    private static DirectionsData instance = null;
    public RequestQueue requestQueue;

    public static DirectionsData getInstance(Context context) {
        if(instance == null){
            instance = new DirectionsData(context);
        }

        return instance;
    }

    private DirectionsData(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        onDownloadComplete = (OnDownloadCompleteListener) context;
        onJSONExceptionListener = (OnJSONExceptionListener) context;
    }

    public void downloadDirectionsData(ArrayList<MarkerOptions> markerOptions){
        final String coordinates = URL_ORIGIN + markerOptions.get(0).getPosition().latitude + "," + markerOptions.get(0).getPosition().longitude
                + URL_DESTINATION + markerOptions.get(1).getPosition().latitude + "," + markerOptions.get(1).getPosition().longitude;
        final String strRequest = URL_BASE + coordinates + URL_API_KEY;

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, strRequest, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray routes = response.getJSONArray("routes");
                    JSONObject main = routes.getJSONObject(0);
                    JSONObject overview = main.getJSONObject("overview_polyline");
                    String encodedPolyline = overview.getString("points");
                    PolyUtil.decode(encodedPolyline);
                    List<LatLng> points = PolyUtil.decode(encodedPolyline);
                    onDownloadComplete.downloadComplete(points);
                    Log.v("POLY", PolyUtil.decode(encodedPolyline).toString());
                }catch(JSONException e){
                    onJSONExceptionListener.onJSONException();
                    Log.v("JSON", e.getLocalizedMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
        Log.v("COORDS", strRequest);
    }

}
