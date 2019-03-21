package com.findsolucoes.backpackpro.googlemaps.GoogleMapsUtils;

import android.util.Log;

import com.findsolucoes.backpackpro.googlemaps.EstimatedTime.TravelMode;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class Utils {
    private static String serviceGoogleMapsRout = "https://maps.googleapis.com/maps/api/directions/json?";
    private static String TAG = "UtilsGoogleMaps";

    public static String createUrlDirectionApi(TravelMode travelMode, String apiKey, LatLng origin, LatLng destination){
        String url =  serviceGoogleMapsRout+"origin="+
                origin.latitude+","+origin.longitude+ "&destination="+
                destination.latitude+","+destination.longitude+"&sensor=true&mode="+travelMode.toString().toLowerCase()+"&units=metric"+
                "&key="
                +apiKey;
        Log.d(TAG, "Travel mode: "+travelMode);
        Log.d(TAG, "Create url google maps diractions: "+url);
        return url;
    }

    /**
     * Get line in rout
     * @param rout
     * @return
     */
    public static PolylineOptions getRoutLine(ArrayList<LatLng> rout){
        PolylineOptions polylineOptions = new PolylineOptions();

        for(LatLng r : rout){
           polylineOptions.add(r);
        }

        return polylineOptions;
    }


    /**
     *  GET ALL MARKEROPTIONS FROM ROUNT
     * @param mListLatLgn
     * @param defaultTitle
     * @return
     */
    public static ArrayList<MarkerOptions> getMarkerOptions(ArrayList<LatLng> mListLatLgn, String defaultTitle) {
        ArrayList<MarkerOptions> markers = new ArrayList<>();
        int point = 1;
        if(mListLatLgn!=null && !mListLatLgn.isEmpty()){
            for(LatLng latLng : mListLatLgn){
                MarkerOptions markerOptions = new MarkerOptions().title(defaultTitle+": "+point).position(latLng);
                markers.add(markerOptions);
                point++;
            }
        }
        return markers;
    }

    /**
     * GET MARKERS FROM MARKERSOPTION
     * @param markerOptions
     * @param map
     * @return
     */
    public static ArrayList<Marker> getMarkerFromOptions(ArrayList<MarkerOptions> markerOptions, GoogleMap map){
        ArrayList<Marker> markersList = new ArrayList<>();
        for(MarkerOptions markerOption : markerOptions){
            markersList.add(map.addMarker(markerOption));
        }
        return markersList;
    }

}
